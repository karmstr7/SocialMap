package com.example.quinnm.socialmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.quinnm.socialmap.api.model.AddMessage;
import com.example.quinnm.socialmap.api.model.FriendsList;
import com.example.quinnm.socialmap.api.model.GetMessage;
import com.example.quinnm.socialmap.api.model.User;
import com.example.quinnm.socialmap.api.service.FriendsListClient;
import com.example.quinnm.socialmap.api.service.MessageClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The main map view after the user has logged in.
 * Loads map from Mapbox
 * Attempts to locate the user's location
 * Single tap to activate toolbar, appear on top right.
 * The toolbar contains 4 buttons, add message, view friends, view messages, and view profile.
 *
 *
 * @author Keir Armstrong, Quinn Milinois
 * @since May 13, 2018
 *
 * REFERENCES:
 *  Mapbox API Reference
 *      https://www.mapbox.com/android-docs/api/map-sdk/6.1.3/index.html
 *  Adding User Location to Mapbox
 *      https://www.youtube.com/watch?v=2rclnd8OKHU
 */
public class MainActivity extends AppCompatActivity implements
        NewMessageDialogFragment.NewMessageDialogListener,
        OnMapReadyCallback,
        MapboxMap.OnMapClickListener,
        LocationEngineListener,
        PermissionsListener{

    // for debugging purposes
    private static final String TAG = "MainActivity";

    // default visibility setting for the toolbar buttons
    private boolean toolbarVisible = false;
    // input references
    private ImageButton _newMessageButton, _viewFriendsButton, _viewMyMessagesButton, _viewMyProfileButton;

//    // TODO: REMOVE THIS BEFORE SUBMITTING
//    private static final User DEFAULT_USER = new User("root","root");

    // Mapbox objects
    private MapView mapView;
    private MapboxMap mapboxMap;
    private boolean addMarkerMode = false;
    private LatLng currentPoint;
    private LocationEngine locationEngine;
    private LocationListener locationListener;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;

    // on Activity creation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // display the activity using one of the layout structures inside /res/layout
        setContentView(R.layout.activity_main);

        // get input references for this activity
        _newMessageButton = findViewById(R.id.btn_new_message);
        _viewFriendsButton = findViewById(R.id.btn_view_friends);
        _viewMyProfileButton = findViewById(R.id.btn_view_my_profile);
        _viewMyMessagesButton = findViewById(R.id.btn_view_my_messages);

        // instantiate Mapbox
        Mapbox.getInstance(this, getString(R.string.mapbox_token));
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        // wait for Mapbox to load map
        mapView.getMapAsync(this);

        // wait for user to choose where to create new message and marker
        _newMessageButton.setOnClickListener(
                (View v) -> {
                    // disable the add message button until the adding process ends
                    _newMessageButton.setEnabled(false);
                    // to distinguish between multiple listeners
                    addMarkerMode = true;
                    // instruct the user to place marker
                    Toast.makeText(MainActivity.this, "Choose a point to add a new message", Toast.LENGTH_LONG).show();
                    // instantiate new listener to get the position of the new message
                    mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng point) {
                            // record the coordinates
                            currentPoint = point;
                            // continue with creating new message
                            onCreateNewMessage();
                            // remove the listener just created
                            mapboxMap.removeOnMapClickListener(this);
                        }
                    });
                }
        );

        // provide click listener to the View Friends button
        _viewFriendsButton.setOnClickListener(
                // call showMyFriends() every time the button is clicked
                (View v) -> showMyFriendsList()
        );

//        provide click listener to the View My Profile button
        _viewMyProfileButton.setOnClickListener(
                // call showMyProfile() every time the button is clicked
                (View v) -> showMyProfile()
        );

//        provide click listener to the View Messages button
        _viewMyMessagesButton.setOnClickListener(
                // call showMyMessages() every time the button is clicked
                (View v) -> showMyMessages()
        );
    }

    public void setToolbarVisibility() {
        // if the buttons are visible, change them to be invisible
        // if the buttons are invisible, change them to be visible
        if (toolbarVisible) {
            _newMessageButton.setVisibility(View.INVISIBLE);
            _viewFriendsButton.setVisibility(View.INVISIBLE);
            _viewMyMessagesButton.setVisibility(View.INVISIBLE);
            _viewMyProfileButton.setVisibility(View.INVISIBLE);
        }
        else {
            _newMessageButton.setVisibility(View.VISIBLE);
            _viewFriendsButton.setVisibility(View.VISIBLE);
            _viewMyMessagesButton.setVisibility(View.VISIBLE);
            _viewMyProfileButton.setVisibility(View.VISIBLE);
        }
        toolbarVisible = !toolbarVisible;
    }

    public void onCreateNewMessage() {
        // create a dialog fragment, to have to user write the message text
        FragmentManager fm = getSupportFragmentManager();
        NewMessageDialogFragment newMessageDialogFragment = NewMessageDialogFragment.newInstance("New Message");
        newMessageDialogFragment.show(fm, "NewMessageDialogFragment");
    }

    public void showMyFriendsList() {
        // go to activity ViewMyFriends
        Toast.makeText(MainActivity.this, "View Friends List", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), ViewFriendsActivity.class);
        MainActivity.this.startActivity(intent);
    }

    public void showMyMessages() {
        // go to activity ViewMyMessages
        Toast.makeText(MainActivity.this, "View My Messages", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), ViewMessagesActivity.class);
        MainActivity.this.startActivity(intent);
    }

    public void showMyProfile() {
        // go to activity ViewMyProfile
        Toast.makeText(MainActivity.this, "View Profile", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), ViewProfileActivity.class);
        MainActivity.this.startActivity(intent);
    }

    private void getUserData() {
        // used to load and refresh friends list and messages
        getFriends();
        getMessages();
    }

    private void getFriends() {
        // get user's friends list

        // get username from store
        String username = ((ApplicationStore) this.getApplication()).getUsername();

        FriendsList friendsList = new FriendsList(
                username
        );

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        FriendsListClient client = retrofit.create(FriendsListClient.class);
        Call<FriendsList> call = client.getFriendsList(friendsList);

        call.enqueue(new Callback<FriendsList>() {
            @Override
            public void onResponse(@NonNull Call<FriendsList> call, @NonNull Response<FriendsList> response) {
                if (response.body() != null && response.isSuccessful() && response.body().getErrorMsg().equals("")) {
                    onGetFriendsListSuccess(response);
                    // on get friend list success
                }
                else {
                    Toast.makeText(getBaseContext(),
                            "ERROR: " + response.body().getErrorMsg(),
                            Toast.LENGTH_SHORT).show();
                    // something went wrong as the server tried to get the friend list
                }
            }

            @Override
            public void onFailure(@NonNull Call<FriendsList> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
                // usually network errors
            }
        });
    }

    private void onGetFriendsListSuccess(Response<FriendsList> response) {
        // refresh the friends list in the store
        ((ApplicationStore) this.getApplication()).setFriends(response.body().getFriends());
        getMessages();
    }

    public void getMessages() {
        String username = ((ApplicationStore) this.getApplication()).getUsername();
        List<String> friends = ((ApplicationStore) this.getApplication()).getFriends();

        GetMessage getMessage = new GetMessage(
                username,
                friends
        );

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        MessageClient client = retrofit.create(MessageClient.class);
        Call<GetMessage> call = client.getMessages(getMessage);

        call.enqueue(new Callback<GetMessage>() {
            @Override
            public void onResponse(@NonNull Call<GetMessage> call, @NonNull Response<GetMessage> response) {
                if (response.body() != null && response.isSuccessful() && response.body().getErrorMsg().equals("")) {
                    showSavedMessages(response);
                }
                else {
                    Toast.makeText(getBaseContext(),
                            "ERROR: " + response.body().getErrorMsg(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetMessage> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSavedMessages(Response<GetMessage> response) {
        try {
            ((ApplicationStore) this.getApplication()).setMessages(response.body().getMessages());
        } catch (NullPointerException e) {
            Toast.makeText(getBaseContext(), "Oops, couldn't parse messages", Toast.LENGTH_SHORT).show();
        }

        String thisUser = ((ApplicationStore) this.getApplication()).getUsername();

        List<Map<String, Object>> messages = response.body().getMessages();
        int listSize = messages.size();

        ((ApplicationStore) this.getApplication()).setNumberOfMessages(listSize);

        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.green_marker);
        Icon friendIcon = iconFactory.fromBitmap(bitmap);

        for (int i = 0; i < listSize; i++) {
            double lat, lng;
            String message_id = messages.get(i).get("message_id").toString();
            String msg_body = messages.get(i).get("msg_body").toString();
            String user = messages.get(i).get("username").toString();

            HashMap<String,String> map = new Gson().fromJson(
                    messages.get(i).get("msg_data").toString(),
                    new TypeToken<HashMap<String, String>>(){}.getType());

            lat = Double.valueOf(map.get("latitude"));
            lng = Double.valueOf(map.get("longitude"));
            LatLng point = new LatLng(lat, lng);

            if (user.equals(thisUser)) {
                mapboxMap.addMarker(new CustomMarkerOptions()
                        .markerId(message_id)
                        .snippet("You: " + msg_body)
                        .position(point)
                );
            }
            else {
                mapboxMap.addMarker(new CustomMarkerOptions()
                        .markerId(message_id)
                        .snippet(user + ": " + msg_body)
                        .position(point)
                        .icon(friendIcon)
                );
            }

        }
    }

    @Override
    public void OnFinishNewMessage(String messageText) {
        String username = ((ApplicationStore) this.getApplication()).getUsername();

        AddMessage addMessage = new AddMessage(
                username,
                messageText,
                currentPoint
        );

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        MessageClient client = retrofit.create(MessageClient.class);
        Call<AddMessage> call = client.addMessage(addMessage);

        call.enqueue(new Callback<AddMessage>() {
            @Override
            public void onResponse(@NonNull Call<AddMessage> call, @NonNull Response<AddMessage> response) {
                if (response.body() != null && response.isSuccessful() && response.body().getErrorMsg().equals("")) {
                    Toast.makeText(getBaseContext(),
                            "Message saved",
                            Toast.LENGTH_SHORT).show();
                    onAddMessageResponse(response);
                }
                else {
                    Toast.makeText(getBaseContext(),
                            "ERROR: " + response.body().getErrorMsg(),
                            Toast.LENGTH_LONG).show();
                    currentPoint = null;
                    addMarkerMode = false;
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddMessage> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), t.toString(), Toast.LENGTH_LONG).show();
                currentPoint = null;
                addMarkerMode = false;
            }
        });
        _newMessageButton.setEnabled(true);
    }

    private void onAddMessageResponse(@NonNull Response<AddMessage> response) {
        // when the user has created new message
        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put("message_id", response.body().getMessageId());
        newMessage.put("username", ((ApplicationStore) this.getApplication()).getUsername());
        newMessage.put("msg_body", response.body().getMessageBody());
        newMessage.put("msg_data", response.body().getMessageData());
        ((ApplicationStore) this.getApplication()).addMessage(newMessage);
        ((ApplicationStore) this.getApplication()).incrementNumberOfMessages();

        mapboxMap.addMarker(new CustomMarkerOptions()
                .markerId(response.body().getMessageId())
                .position(currentPoint)
                .snippet("You: " + response.body().getMessageBody())
        );
        currentPoint = null;
        addMarkerMode = false;
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        // when the map is loaded
        MainActivity.this.mapboxMap = mapboxMap;
        enableLocation();

        getUserData();
        mapboxMap.addOnMapClickListener(this);
    }

    private void enableLocation() {
        // get location permission from user
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine();
            initializeLocationLayer();
        }
        else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine() {
        // sets up the location getter
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        }
        else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void initializeLocationLayer() {
        // sets up the layer plugin
        locationLayerPlugin = new LocationLayerPlugin(mapView, mapboxMap, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }

    private void setCameraPosition(Location location) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 13));
//        CameraPosition position = new CameraPosition.Builder()
//                .target(new LatLng(location.getLatitude(), location.getLongitude()))
//                .tilt(30)
//                .zoom(15)
//                .build();
//
//        mapboxMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(position));
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        if (!addMarkerMode) {
            setToolbarVisibility();
        }
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            setCameraPosition(location);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionToExplain) {
        // Toast or Dialog
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        if (mapboxMap != null) {
            mapboxMap.clear();
            getFriends();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
