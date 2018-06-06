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
import com.example.quinnm.socialmap.api.model.GetMessage;
import com.example.quinnm.socialmap.api.model.User;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The main map view after the user has logged in.
 * Loads map from Mapbox
 * Single tap to activate toolbar, appear on top right.
 * TODO: Get user's location
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

    private static final String TAG = "MainActivity";

    private boolean toolbarVisible = false;
    private ImageButton _newMessageButton, _viewFriendsButton, _viewMyProfileButton;

    // TODO: REMOVE THIS BEFORE SUBMITTING
    private static final User DEFAULT_USER = new User("root","root");

    private MapView mapView;
    private MapboxMap mapboxMap;
    private boolean addMarkerMode = false;
    private LatLng currentPoint;
    private LocationEngine locationEngine;
    private LocationListener locationListener;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get toolbar buttons
        _newMessageButton = findViewById(R.id.btn_new_message);
        _viewFriendsButton = findViewById(R.id.btn_view_friends);
        _viewMyProfileButton = findViewById(R.id.btn_view_my_profile);

        Mapbox.getInstance(this, getString(R.string.mapbox_token));
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // wait for user to choose where to create new message and marker
        _newMessageButton.setOnClickListener(
                (View v) -> {
                    _newMessageButton.setEnabled(false);
                    addMarkerMode = true;
                    Toast.makeText(MainActivity.this, "Choose a point to add a new message", Toast.LENGTH_LONG).show();
                    mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng point) {
                            currentPoint = point;
                            onCreateNewMessage();
                            mapboxMap.removeOnMapClickListener(this);
                        }
                    });
                }
        );

        _viewFriendsButton.setOnClickListener(
                (View v) -> showFriendsListDialog()
        );

        _viewMyProfileButton.setOnClickListener(
                (View v) -> showMyProfileDialog()
        );
    }

    public void setToolbarVisibility() {
        if (toolbarVisible) {
            _newMessageButton.setVisibility(View.INVISIBLE);
            _viewFriendsButton.setVisibility(View.INVISIBLE);
            _viewMyProfileButton.setVisibility(View.INVISIBLE);
        }
        else {
            _newMessageButton.setVisibility(View.VISIBLE);
            _viewFriendsButton.setVisibility(View.VISIBLE);
            _viewMyProfileButton.setVisibility(View.VISIBLE);
        }
        toolbarVisible = !toolbarVisible;
    }

    public void onCreateNewMessage() {
        FragmentManager fm = getSupportFragmentManager();
        NewMessageDialogFragment newMessageDialogFragment = NewMessageDialogFragment.newInstance("New Message");
        newMessageDialogFragment.show(fm, "NewMessageDialogFragment");
    }

    public void showFriendsListDialog() {
        Toast.makeText(MainActivity.this, "View Friends List", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), ViewFriendsActivity.class);
        MainActivity.this.startActivity(intent);
    }

    public void showMyProfileDialog() {
        Toast.makeText(MainActivity.this, "View Profile", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), ViewProfileActivity.class);
        MainActivity.this.startActivity(intent);
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
        MainActivity.this.mapboxMap = mapboxMap;
        enableLocation();

        getMessages();
        mapboxMap.addOnMapClickListener(this);
    }

    private void enableLocation() {
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
        locationLayerPlugin = new LocationLayerPlugin(mapView, mapboxMap, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }

    private void setCameraPosition(Location location) {
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .tilt(30)
                .zoom(15)
                .build();

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position));
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
            getMessages();
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
