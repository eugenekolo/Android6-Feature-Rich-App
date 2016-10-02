// Source: https://github.com/googlesamples/android-Geofencing/blob/master/Application/src/main/java/com/example/android/wearable/geofencing/SimpleGeofence.java

package com.flyingcats.vacation.util;

import com.google.android.gms.location.Geofence;

public class SimpleGeofence {
    // Instance variables
    private final String mId;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;
    private long mExpirationDuration;
    private int mTransitionType;

    public SimpleGeofence(String geofenceId, double latitude, double longitude, float radius,
                          long expiration, int transition) {
        // Set the instance fields from the constructor.
        this.mId = geofenceId;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mExpirationDuration = expiration;
        this.mTransitionType = transition;
    }

    // Instance field getters.
    public String getId() {
        return mId;
    }
    public double getLatitude() {
        return mLatitude;
    }
    public double getLongitude() {
        return mLongitude;
    }
    public float getRadius() {
        return mRadius;
    }
    public long getExpirationDuration() {
        return mExpirationDuration;
    }
    public int getTransitionType() {
        return mTransitionType;
    }

    public Geofence toGeofence() {
        // Build a new Geofence object.
        return new Geofence.Builder()
                .setRequestId(mId)
                .setTransitionTypes(mTransitionType)
                .setCircularRegion(mLatitude, mLongitude, mRadius)
                .setExpirationDuration(mExpirationDuration)
                .build();
    }

}