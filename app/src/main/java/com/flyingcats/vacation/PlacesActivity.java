package com.flyingcats.vacation;

import com.flyingcats.vacation.util.ApiRequestTask;
import com.flyingcats.vacation.util.SimpleGeofence;
import com.flyingcats.vacation.util.SimpleGeofenceStore;
import com.flyingcats.vacation.util.Util;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class PlacesActivity extends FragmentActivity implements OnMapReadyCallback, ResultCallback<Status>,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    // Google Maps stuff
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LatLng LLCoord;
    private Boolean isShowingNearby;

    // Google Places stuff
    private JSONObject googlePlacesJson;
    private final int PLACE_PICKER_REQUEST = 4242;
    private final String GOOGLE_PLACES_WEB_KEY = "XXX_FILL_ME_XXX";
    private final int GOOGLE_PLACES_RADIUS = 5000; // 5000m radius
    private final String GOOGLE_PLACES_TYPE = "aquarium|amusement_park|cafe|museum|park";

    // Google Geofencing stuff
    private final int GEOFENCE_RADIUS_METERS = 500; // 500m radius
    public static final long GEOFENCE_EXPIRATION_TIME = Geofence.NEVER_EXPIRE;
    private SimpleGeofenceStore geofenceStore;
    private List<Geofence> geofenceList;
    private PendingIntent geofenceIntent;

    // Graphical components
    private TextView placeVicinityText;
    private TextView placeNameText;
    private TextView placeIsOpenText;
    private Button placeTrackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        placeNameText = (TextView) findViewById(R.id.placeNameText);
        placeVicinityText = (TextView) findViewById(R.id.placeVicinityText);
        placeIsOpenText = (TextView) findViewById(R.id.placeIsOpenText);
        placeTrackBtn = (Button) findViewById(R.id.placeTrackBtn);
        geofenceStore = new SimpleGeofenceStore(this);
        geofenceList = new ArrayList<Geofence>();

        if (!Util.isGooglePlayServicesAvailable(this)) {
            finish();
            return;
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set up a Google API Client
        client = new GoogleApiClient.Builder(this)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Redirect the user to pick a location from where to look for attractions
        try {
            startActivityForResult(new PlacePicker.IntentBuilder().build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            Log.e("CS591", "Failed to launch PlacePicker");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (LLCoord != null && !isShowingNearby) {
            mMap.addMarker(new MarkerOptions().position(LLCoord).title("Attractions nearby").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LLCoord));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10.8f));
        }

        /* When a map marker is clicked, update the bottom information */
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 450, getResources().getDisplayMetrics());
                findViewById(R.id.map).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)pixels));
                String jsonStr = marker.getSnippet();
                try {
                    final JSONObject place = new JSONObject(jsonStr);
                    String name = place.optString("name");
                    String vicinity = place.optString("vicinity");
                    JSONObject opening_hours = place.optJSONObject("opening_hours");

                    if (name != null) {
                        placeNameText.setText(name);
                    } else {
                        placeNameText.setText("Unknown place name");
                    }

                    if (vicinity != null) {
                        placeVicinityText.setText(vicinity);
                    } else {
                        placeVicinityText.setText("Unknown location");
                    }

                    if (opening_hours != null) {
                        Boolean isOpen = opening_hours.optBoolean("open_now");
                        if (isOpen != null) {
                            placeIsOpenText.setText(isOpen ? "Open now!" : "Closed!");
                        }
                    } else {
                        placeIsOpenText.setText("Unknown open/close status");
                    }

                    /* When the TRACK button is clicked, add a new geofence */
                    placeTrackBtn.setVisibility(View.VISIBLE);
                    placeTrackBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                addGeofence(place.optString("name") + "_ID_" + place.optString("id"),
                                        Double.parseDouble(place.getJSONObject("geometry").getJSONObject("location").getString("lat")),
                                        Double.parseDouble(place.getJSONObject("geometry").getJSONObject("location").getString("lng")),
                                        GEOFENCE_RADIUS_METERS,
                                        GEOFENCE_EXPIRATION_TIME);
                            } catch (Exception e) {
                                // Do nothing
                            }
                        }
                    });

                } catch (Exception e) {
                    Util.toast(getBaseContext(), "No location info found");
                }

                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        Action viewAction = Action.newAction(Action.TYPE_VIEW, "Maps Page", Uri.parse("http://host/path"),
                Uri.parse("android-app://com.flyingcats.vacation/http/host/path"));
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(Action.TYPE_VIEW, "Maps Page", Uri.parse("http://host/path"),
                Uri.parse("android-app://com.flyingcats.vacation/http/host/path"));
        AppIndex.AppIndexApi.end(client, viewAction);

        client.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // The connection to Google Play services completed successfully.
        Util.print("Google Play connected!");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // The connection failed to complete.
        Util.print("Google Play connection failed!");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Util.print("Google Play connection lost!");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case PLACE_PICKER_REQUEST: // User has picked a location from Google Places Picker
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);

                    if (geofenceIntent != null) {
                        LocationServices.GeofencingApi.removeGeofences(client, geofenceIntent);
                    }

                    LLCoord = place.getLatLng();
                    try {
                        /* Display the picked location onto a Google Map */
                        mMap.addMarker(new MarkerOptions().position(LLCoord).title("Attractions nearby").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(LLCoord));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10.8f));
                        isShowingNearby = true;

                        /* Use Google Places Web API to pull a list of nearby fun places */
                        ApiRequestTask googlePlacesRequest = new ApiRequestTask(new ApiRequestTask.ApiResponse() {
                            /* On response of a Google Places Web API request, map the places onto a Google Map */
                            public void onResponse(String response) {
                                try {
                                    googlePlacesJson = new JSONObject(response);
                                    JSONArray results = googlePlacesJson.getJSONArray("results");

                                    for (int i = 0; i < results.length(); i++) {
                                        JSONObject place = results.getJSONObject(i);
                                        MarkerOptions marker = new MarkerOptions();
                                        marker.snippet(place.toString());

                                        Double lat = Double.parseDouble(place.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                                        Double lng = Double.parseDouble(place.getJSONObject("geometry").getJSONObject("location").getString("lng"));
                                        marker.position(new LatLng(lat, lng));

                                        JSONArray types = place.getJSONArray("types");
                                        List<String> typeslist = new ArrayList();
                                        for (int j = 0; j < types.length(); typeslist.add(types.getString(j++)));
                                        if (typeslist.contains("cafe")) { // Filter out the food/cafes to orange
                                            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                        } else {
                                            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                        }

                                        String markerStr = "";
                                        if (!place.isNull("name")) {
                                            markerStr += place.getString("name");
                                        }
                                        if (!place.isNull("vicinity")) {
                                            markerStr += " at " + place.getString("vicinity");;
                                        }
                                        marker.title(markerStr);

                                        mMap.addMarker(marker);
                                    }
                                } catch (Exception e) {
                                    Log.e("CS591", "Failed Google Places Request");
                                }
                            }
                        });
                        googlePlacesRequest.execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                                                    + "location=" + Double.toString(LLCoord.latitude) + "," + Double.toString(LLCoord.longitude)
                                                    + "&radius=" + GOOGLE_PLACES_RADIUS
                                                    + "&key=" + GOOGLE_PLACES_WEB_KEY
                                                    + "&type=" + GOOGLE_PLACES_TYPE, "GET", "blank");
                    } catch (Exception e) {
                        Toast.makeText(this, "Sorry, an error occurred finding nearby attractions.", Toast.LENGTH_LONG).show();
                        Log.e("CS591", "Google Map not loaded yet...");
                    }
                }
                break;

        }
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Can do something fun here
        } else {
            // Can do something less fun here
        }
    }

    public void addGeofence(String id, Double lat, Double lng, float radius, long expiration) {
        SimpleGeofence geofence = new SimpleGeofence(id, lat, lng, radius, expiration,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

        // Save it into the store for later use as well
        geofenceStore.setGeofence(id, geofence);
        geofenceList.add(geofence.toGeofence());

        // Add the geofence to start being tracked
        ArrayList<Geofence> singleGeofence = new ArrayList<Geofence>();
        singleGeofence.add(geofence.toGeofence());
        geofenceIntent = getGeofenceIntent();
        try {
            LocationServices.GeofencingApi.addGeofences(client, singleGeofence, geofenceIntent)
                    .setResultCallback(this);
        } catch (SecurityException securityException) {
            Util.toast(this, "Unable to track approaching a location");
        }

        // Some fun feedback to the user
        drawCircleForGeofence(geofence);
    }

    public void drawCircleForGeofence(SimpleGeofence fence){
        if(fence == null){
            return;
        }

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(fence.getLatitude(),fence.getLongitude()))
                .radius(fence.getRadius())
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);

        mMap.addCircle(circleOptions);
    }

    /**
     *  Trigger a pending intent when a geofence transistion occurs
     *  When a geofence is triggered, kick off the GeofenceIntent, which as of 4/22/2016, simply creates
     *  a notification informing the user. */
    private PendingIntent getGeofenceIntent() {
        // Return the old one if we already made it
        if (geofenceIntent != null) {
            return geofenceIntent;
        }
        Intent intent = new Intent(this, GeofenceIntent.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // "MainActivity" -> "TwitterActivity" ->  "SmsMessageActivity" -> "PackingListActivity" ->
        // "ExchangeActivity" -> "TranslationActivity" -> "PlacesActivity";
        Util.onSwipeChangeScreen(event, this, TranslationActivity.class, MainActivity.class);
        return super.onTouchEvent(event);
    }
}
