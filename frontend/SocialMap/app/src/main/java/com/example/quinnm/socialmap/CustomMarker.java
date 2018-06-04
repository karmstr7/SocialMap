package com.example.quinnm.socialmap;

import com.mapbox.mapboxsdk.annotations.BaseMarkerOptions;
import com.mapbox.mapboxsdk.annotations.Marker;

/**
 * Base class for the customized marker.
 * Need customized markers so they can store additional details, like their Id's.
 *
 * @author Keir Armstrong
 * @since June 3, 2018
 *
 * REFERENCES:
 *   Marker - extra object - tobrun - Github Issues
 *      https://github.com/mapbox/mapbox-gl-native/issues/5370
 */


public class CustomMarker extends Marker{
    // id and getId() are reserved variable name and method name in the Mapbox SDK.
    private String markerId;

    public CustomMarker(BaseMarkerOptions baseMarkerOptions, String markerId) {
        super(baseMarkerOptions);
        this.markerId = markerId;
    }

    public String getMarkerId() {
        return markerId;
    }
}
