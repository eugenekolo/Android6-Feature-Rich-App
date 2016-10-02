package com.flyingcats.vacation;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.flyingcats.vacation.util.Util;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceIntent extends IntentService {

    public GeofenceIntent() {
        super(GeofenceIntent.class.getSimpleName());
    }

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Multiple geofences can be triggered at the same time. Inform the user of them all
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        ArrayList triggeringGeofencesNames = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesNames.add(geofence.getRequestId().split("_ID_")[0]);
        }
        String triggeringGeofencesNamesString = TextUtils.join(", ", triggeringGeofencesNames);

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Util.sendSimpleNotification(getBaseContext(), "You're nearby", triggeringGeofencesNamesString);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Util.sendSimpleNotification(getBaseContext(), "You're leaving", triggeringGeofencesNamesString);
        } else {
            // Do nothing
        }
    }
}
