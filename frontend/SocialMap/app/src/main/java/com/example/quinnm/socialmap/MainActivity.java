package com.example.quinnm.socialmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.List;
//import com.mapbox.services.commons.geojson.FeatureCollection;
//import com.mapbox.services.commons.geojson.Point;

public class MainActivity extends AppCompatActivity{

    private static final String MARKER_SOURCE = "markers-source";
    private static final String MARKER_STYLE_LAYER = "markers-style-layer";
    private static final String MARKER_IMAGE = "custom-marker";

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
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-123.07640946242944, 44.04665947871217))
                        .title("University of Oregon")
                        .snippet("Eugene, Oregon"));

            }
        });
    }

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
