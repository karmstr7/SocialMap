package com.example.quinnm.socialmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.quinnm.socialmap.api.model.Message;
import com.example.quinnm.socialmap.api.model.User;
import com.example.quinnm.socialmap.api.service.MessageClient;
import com.example.quinnm.socialmap.api.service.UserClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
//import com.mapbox.services.commons.geojson;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//import com.mapbox.services.commons.geojson.FeatureCollection;
//import com.mapbox.services.commons.geojson.Point;

public class MainActivity extends AppCompatActivity{

    private static final String MARKER_SOURCE = "markers-source";
    private static final String MARKER_STYLE_LAYER = "markers-style-layer";
    private static final String MARKER_IMAGE = "custom-marker";
    private static final String TAG = "MainActivity";

    private static final User DEFAULT_USER = new User("root","root");

    private MapView mapView;
    private MapboxMap mapboxMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoicXVpbm5taWwiLCJhIjoiY2poNndlc2NuMDEyODJwcGd4OWw5d2M0YyJ9.ld7OWGt932HW0ebcv8CZFw");

        setContentView(R.layout.activity_main);


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                MainActivity.this.mapboxMap = mapboxMap;

                //        Image: an image is loaded an added to the map
                //        Bitmap icon = BitmapFactory.decodeResource(
                //                MainActivity.this.getResources(), R.drawable.custom_marker);
                //        mapboxMap.addImage(MARKER_IMAGE, icon);
                ////        addMarkers();

                getMessages();

                mapboxMap.addMarker(new MarkerOptions()
//                        These coords aren't accurate
                        .position(new LatLng(44.44, -123.07))
                        .title("University of Oregon")
                        .snippet("Eugene, Oregon"));

                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(35.20859, -106.449893))
                        .title("Big Hike")
                        .snippet("Sandia Crest"));
            }
        });
    }


    public void getMessages(){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("localhost:8000/socialmap/api/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        MessageClient client = retrofit.create(MessageClient.class);
        Call<Message> call = client.getMessages(DEFAULT_USER);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Log.d(TAG,"Message response" + response.toString());
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(getBaseContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });

                


    }

//msg=
//    {
//        "token": token,    # Type: string
//        "username": username,
//        "data":  data,    # Object containing data
//        "body": body    # Type: string
//    }


//    private void addMarkers(){
//        List<Feature> features = new ArrayList<>();
//        /* Source: A data source specifies the geographic coordinate where the image marker gets placed. */
////        ^ from Mapbox SDK
//        features.add(Feature.fromGeometry(Point.fromCoordinates(new double[] {-123.07640946242944,44.04665947871217})));
//        FeatureCollection featureCollection = FeatureCollection.fromFeatures(features);
//        GeoJsonSource source = new GeoJsonSource(MARKER_SOURCE, featureCollection);
//        mapboxMap.addSource(source);
//
//        /* Style layer: A style layer ties together the source and image and specifies how they are displayed on the map. */
//
//        SymbolLayer markerStyleLayer = new SymbolLayer(MARKER_STYLE_LAYER, MARKER_SOURCE)
//                .withProperties(
//                        PropertyFactory.iconAllowOverlap(true),
//                        PropertyFactory.iconImage(MARKER_IMAGE));
//            mapboxMap.addLayer(markerStyleLayer);
//    }

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
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    }
