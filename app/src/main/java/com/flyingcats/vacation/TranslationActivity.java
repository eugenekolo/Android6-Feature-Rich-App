package com.flyingcats.vacation;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TranslationActivity extends AppCompatActivity {

    private String selectedLocationType;
    private String selectedLanguage;
    private TextView lblPhrases;
    private TextView lblCurrentCountryDisplay;
    private PhrasesGetter phrasesGetter;
    private String currentCountry;
    Spinner spnrLocationType;
    Spinner spnrLanguage;

    public static final String MY_PREFS_NAME = "PhrasesData";
    public static SharedPreferences preferenceSettings;
    public static SharedPreferences.Editor preferenceEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        phrasesGetter = new PhrasesGetter();
        preferenceSettings = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        preferenceEditor = preferenceSettings.edit();
        phrasesGetter.initializePhrases();

        if(preferenceSettings.getString("country", "empty").equals("empty")){
            preferenceEditor.putString("country", "France");
            preferenceEditor.apply();
            currentCountry = "France";
        }


        lblPhrases = (TextView) findViewById(R.id.lblPhrases);
        lblCurrentCountryDisplay = (TextView) findViewById(R.id.lblCurrentCountryDisplay);

        spnrLocationType = (Spinner) findViewById(R.id.spnrLocationType);


        spnrLocationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedLocationType = parent.getItemAtPosition(position).toString();

                updatePhrases();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        spnrLanguage = (Spinner) findViewById(R.id.spnrLanguage);

        spnrLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedLanguage = parent.getItemAtPosition(position).toString();

                updatePhrases();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        updateSpinners();
    }

    private void updateSpinners(){
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Airport/Train Station");
        categories.add("Coffee Shop");
        categories.add("Restaurant");
        categories.add("On the street");
        categories.add("Hotel");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnrLocationType.setAdapter(dataAdapter);

        // Spinner Drop down elements
        List<String> categories2 = new ArrayList<String>();

        if(isFrenchSpeakingCountry(MainActivity.chosen_dest_static)){
            if(!getLanguage(getCountry()).equals("French")){
                categories2.add("French");
            }else{
                categories2.add("French (current location's language)");
            }
        }else if(isGermanSpeakingCountry(MainActivity.chosen_dest_static)){
            if(!getLanguage(getCountry()).equals("German")){
                categories2.add("German");
            }else{
                categories2.add("German (current location's language)");
            }
        }else if(isJapaneseSpeakingCountry(MainActivity.chosen_dest_static)){
            if(!getLanguage(getCountry()).equals("Japanese")){
                categories2.add("Japanese");
            }else{
                categories2.add("Japanese (current location's language)");
            }
        }else if(isRussianSpeakingCountry(MainActivity.chosen_dest_static)){
            if(!getLanguage(getCountry()).equals("Russian")){
                categories2.add("Russian");
            }else{
                categories2.add("Japanese (current location's language)");
            }
        }else{
            if(!getLanguage(getCountry()).equals("Spanish")){
                categories2.add("Spanish");
            }else{
                categories2.add("Spanish (current location's language)");
            }
        }

        if(!isFrenchSpeakingCountry(MainActivity.chosen_dest_static)){
            if(!getLanguage(getCountry()).equals("French")){
                categories2.add("French");
            }else{
                categories2.add("French (current location's language)");
            }
        }

        if(!isSpanishSpeakingCountry(MainActivity.chosen_dest_static)){
            if(!getLanguage(getCountry()).equals("Spanish")){
                categories2.add("Spanish");
            }else{
                categories2.add("Spanish (current location's language)");
            }
        }

        if(!isGermanSpeakingCountry(MainActivity.chosen_dest_static)){
            if(!getLanguage(getCountry()).equals("German")){
                categories2.add("German");
            }else{
                categories2.add("German (current location's language)");
            }
        }

        if(!isRussianSpeakingCountry(MainActivity.chosen_dest_static)){
            if(!getLanguage(getCountry()).equals("Russian")){
                categories2.add("Russian");
            }else{
                categories2.add("Russian (current location's language)");
            }

        }

        if(!MainActivity.chosen_dest_static.equals("To")){
            if(!getLanguage(getCountry()).equals("Japanese")){
                categories2.add("Japanese");
            }else{
                categories2.add("Japanese (current location's language)");
            }
        }


        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories2);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnrLanguage.setAdapter(dataAdapter);

    }


    private void updatePhrases(){

        getCountry();

        lblCurrentCountryDisplay.setText(currentCountry);

        Log.d("CS591", MainActivity.chosen_dest_static);
        lblPhrases.setText(  phrasesGetter.getPhrases(selectedLanguage.split("\\s+")[0], selectedLocationType) );

    }

    private String getCountry(){
        GPSTracker gps = new GPSTracker(this);

        //Log.d("CS591", gps.canGetLocation() + "");
        //Log.d("CS591", isNetworkAvailable() + "");


        if (gps.canGetLocation() && isNetworkAvailable() == true) {
            //If have gps AND network, we can get the country

            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1);

            } catch (IOException e) {
                e.printStackTrace();
            }


            if (addresses.isEmpty() == false) {

                currentCountry = addresses.get(0).getCountryName();
                preferenceEditor.putString("country",currentCountry);
                preferenceEditor.apply();

            }else{
                Log.d("CS591", "got here 1");
                currentCountry = preferenceSettings.getString("country", "error1");
            }
        }else{
            Log.d("CS591", "got here 2");
            currentCountry = preferenceSettings.getString("country", "error2");

        }

        return currentCountry;
    }


    private String getLanguage(String input){

        Log.d("CS591",input);

        if(isSpanishSpeakingCountry(input) == true){
            return "Spanish";
        }else if(isFrenchSpeakingCountry(input) == true){
            return "French";
        }else if(isGermanSpeakingCountry(input) == true){
            return "German";
        }else if(isRussianSpeakingCountry(input) == true){
            return "Russian";
        }else if(isJapaneseSpeakingCountry(input) == true){
            return "Japanese";
        }else{
            //By default return Spanish
            return "Spanish";
        }
    }


    private boolean isSpanishSpeakingCountry(String input){
        return input.equals("Spain") || input.equals("Mexico") || input.equals("Puerto Rico") || input.equals("Cuba") || input.equals("Dominican Republic") || input.equals("Cancun");
    }

    private boolean isFrenchSpeakingCountry(String input){
        return input.equals("France") || input.equals("Monaco") || input.equals("Paris");
    }

    private boolean isGermanSpeakingCountry(String input){
        return input.equals("Germany") || input.equals("Austria") || input.equals("Switzerland") || input.equals("Liechtenstein") || input.equals("Berlin");
    }

    private boolean isRussianSpeakingCountry(String input){
        return input.equals("Russia") || input.equals("Moscow");
    }

    private boolean isJapaneseSpeakingCountry(String input){
        return input.equals("Japan") || input.equals("Tokyo");
    }


    public boolean isNetworkAvailable() {
        //This entire method is from: http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
        //it returns if the network on the phone is available
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                if (ni.isConnected()) {
                    haveConnectedWifi = true;
                }
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected()) {
                    haveConnectedMobile = true;
                }
            }
        }

        return haveConnectedWifi || haveConnectedMobile;
    }

    private class GPSTracker extends Service implements LocationListener {

        //This entire class GPSTracker is adapted from http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/
        //This class is used to return latitude and longitude
        //This is NOT our code


        private final Context context;

        boolean isGPSEnabled = false;
        boolean isNetworkEnabled = false;
        boolean canGetLocation = false;

        Location location;

        double latitude;
        double longitude;

        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
        private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

        protected LocationManager locationManager;

        public GPSTracker(Context context) {
            this.context = context;
            getLocation();
        }

        public Location getLocation() {
            try {
                locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if(!isGPSEnabled && !isNetworkEnabled) {

                } else {
                    this.canGetLocation = true;

                    if (isNetworkEnabled) {

                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null) {

                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }

                    }

                    if(isGPSEnabled) {
                        if(location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                            if(locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if(location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return location;
        }


        public void stopUsingGPS() {
            if(locationManager != null) {
                locationManager.removeUpdates(GPSTracker.this);
            }
        }

        public double getLatitude() {
            if(location != null) {
                latitude = location.getLatitude();
            }
            return latitude;
        }

        public double getLongitude() {
            if(location != null) {
                longitude = location.getLongitude();
            }

            return longitude;
        }

        public boolean canGetLocation() {
            return this.canGetLocation;
        }

        public void showSettingsAlert() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            alertDialog.setTitle("GPS is settings");

            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialog.show();
        }

        @Override
        public void onLocationChanged(Location arg0) {
            getCountry();
            updatePhrases();
            updateSpinners();
        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public IBinder onBind(Intent intent) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    //End of GPSTracker.java
}
