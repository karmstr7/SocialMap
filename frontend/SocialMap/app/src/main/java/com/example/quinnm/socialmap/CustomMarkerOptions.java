package com.example.quinnm.socialmap;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.mapbox.mapboxsdk.annotations.BaseMarkerOptions;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Builder for CustomMarker class.
 *
 * @author Keir Armstrong
 * @since June 3, 2018
 *
 * REFERENCES:
 *   Marker - extra object - tobrun - Github Issues
 *      https://github.com/mapbox/mapbox-gl-native/issues/5370
 */
public class CustomMarkerOptions extends BaseMarkerOptions<CustomMarker, CustomMarkerOptions> {
    private String markerId;

    public CustomMarkerOptions markerId(String name) {
        markerId = name;
        return getThis();
    }

    public CustomMarkerOptions() {
    }

    private CustomMarkerOptions(Parcel in) {
        position((LatLng) in.readParcelable(LatLng.class.getClassLoader()));
        snippet(in.readString());
        String iconId = in.readString();
        Bitmap iconBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        Icon icon = IconFactory.recreate(iconId, iconBitmap);
        icon(icon);
        markerId(in.readString());
    }

    @Override
    public CustomMarkerOptions getThis() {
        return this;
    }

    @Override
    public CustomMarker getMarker() {
        return new CustomMarker(this, markerId);
    }

    public static final Parcelable.Creator<CustomMarkerOptions> CREATOR
            = new Parcelable.Creator<CustomMarkerOptions>() {
        public CustomMarkerOptions createFromParcel(Parcel in) {
            return new CustomMarkerOptions(in);
        }

        public CustomMarkerOptions[] newArray(int size) {
            return new CustomMarkerOptions[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(position, flags);
        out.writeString(snippet);
        out.writeString(icon.getId());
        out.writeParcelable(icon.getBitmap(), flags);
        out.writeString(markerId);
    }
}
