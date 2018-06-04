package com.example.quinnm.socialmap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.quinnm.socialmap.api.model.AddMessage;
import com.example.quinnm.socialmap.api.model.GetMessage;
import com.example.quinnm.socialmap.api.model.Message;
import com.example.quinnm.socialmap.api.model.User;
import com.example.quinnm.socialmap.api.service.MessageClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.ArrayList;
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
 */
public class MainActivity extends AppCompatActivity implements
        NewMessageDialogFragment.NewMessageDialogListener,
        OnMapReadyCallback,
        MapboxMap.OnMapClickListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_ADD_MESSAGE_DIALOGFRAGMENT = 2;
    private static final int REQUEST_VIEW_FIRENDS_ACTIVITY = 3;
    private static final int REQUEST_VIEW_PROFILE_ACTIVITY = 4;

    private boolean toolbarVisible = false;
    private ImageButton _newMessageButton, _viewFriendsButton, _viewMyProfileButton;

    private static final User DEFAULT_USER = new User("root","root");

    private MapView mapView;
    private MapboxMap mapboxMap;
    private static final double DEFAULT_LATITUDE = 44.04665947871217;
    private static final double DEFAULT_LONGITUDE = -123.07640946242944;
    private static final String MARKER_SOURCE = "markers-source";
    private static final String MARKER_STYLE_LAYER = "markers-style-layer";
    private static final String MARKER_IMAGE = "custom-marker";
    private MapboxMap.OnMapClickListener addNewMarkerListener;
    private boolean addMarkerMode = false;
    private LatLng currentPoint;
    private ArrayList<LatLng> allMarkers = new ArrayList<>();
    private LocationEngine locationEngine;
    private LocationListener locationListener;
    private PermissionsManager permissionsManager;

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private Location lastKnownLocation;

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

        //        initLocationService();
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
        // TODO: DISABLE OUTSIDE AREA UNTIL CANCEL/CONFIRM
        FragmentManager fm = getSupportFragmentManager();
        NewMessageDialogFragment newMessageDialogFragment = NewMessageDialogFragment.newInstance("Some Title");
        newMessageDialogFragment.show(fm, "NewMessageDialogFragment");
    }

    public void showFriendsListDialog() {
        Toast.makeText(MainActivity.this, "friends list", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), ViewFriendsActivity.class);
        MainActivity.this.startActivity(intent);
    }

    public void showMyProfileDialog() {
        Toast.makeText(MainActivity.this, "your info", Toast.LENGTH_LONG).show();
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
                .baseUrl("http://10.0.2.2:8000/socialmap/api/")
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

        List<Map<String, Object>> messages = response.body().getMessages();
        int listSize = messages.size();

        for (int i = 0; i < listSize; i++) {
            double lat, lng;

            HashMap<String,String> map = new Gson().fromJson(
                    messages.get(i).get("msg_data").toString(),
                    new TypeToken<HashMap<String, String>>(){}.getType());

            lat = Double.valueOf(map.get("latitude"));
            lng = Double.valueOf(map.get("longitude"));

            LatLng point = new LatLng(lat, lng);

            mapboxMap.addMarker(new CustomMarkerOptions()
                .markerId(messages.get(i).get("message_id").toString())
                .snippet(messages.get(i).get("msg_body").toString())
                .position(point)
            );
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
                .baseUrl("http://10.0.2.2:8000/socialmap/api/")
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
    }

    private void onAddMessageResponse(@NonNull Response<AddMessage> response) {
        mapboxMap.addMarker(new CustomMarkerOptions()
                .markerId(response.body().getMessageId())
                .position(currentPoint)
                .snippet(response.body().getMessageBody())
        );
        currentPoint = null;
        addMarkerMode = false;
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        MainActivity.this.mapboxMap = mapboxMap;
        getMessages();
        mapboxMap.addOnMapClickListener(this);
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        if (!addMarkerMode) {
            setToolbarVisibility();
        }
//        CameraPosition position = new CameraPosition.Builder()
//                .target(new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE))
//                .zoom(15)
//                .tilt(30)
//                .build();
//
//        mapboxMap.animateCamera(CameraUpdateFactory
//            .newCameraPosition(position), 5000);
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
