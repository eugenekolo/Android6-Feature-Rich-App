package com.flyingcats.vacation;

import android.app.Activity;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flyingcats.vacation.util.Util;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ExchangeActivity extends Activity {

    String currentCountry;
    Boolean USDInputted = true; //Program uses the variable to know how to apply conversion rate

    public static final String MY_PREFS_NAME = "GPSData";
    public static SharedPreferences preferenceSettings;
    public static SharedPreferences.Editor preferenceEditor;

    //Initialize different UI elements
    EditText edtUSD;
    EditText edtCurrency;
    TextView lblLastUpdatedDisplay;
    TextView txtCurrencyTitle;
    TextView lblCurrentCountryDisplay;
    TextView lblCurrencyDisplay;
    TextView lblErrorMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        edtUSD = (EditText) findViewById(R.id.edtUSD);
        edtCurrency = (EditText) findViewById(R.id.edtCurrency);
        lblErrorMessages = (TextView) findViewById(R.id.lblErrorMessages);


        initializeSharedPreferences(); //The local on-device database

        //The whole logic flow is built around the value of currentCountry
        //All other would-be variable are simple derive from currentCountry
        updateCurrentCountry();


        //By default, set the conversion to 1USD with the currency so it's not just two empty boxes
        edtUSD.setText("1");

        GetRates job = new GetRates();
        job.execute();


        Button btnConvert = (Button) findViewById(R.id.btnConvert);

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateCurrentCountry();

                GetRates job = new GetRates();
                job.execute();
            }
        });

        edtUSD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //Purpose of this event handler is to clear the value of the box that isn't selected
                //This is so it's easy to understand which currency is the input vs output

                updateCurrentCountry();

                if (hasFocus) {
                    edtCurrency.setText("");
                    edtUSD.setSelectAllOnFocus(true);
                    USDInputted = true;
                } else {
                    edtUSD.setText("");
                    edtCurrency.setSelectAllOnFocus(true);
                    USDInputted = false;
                }
            }
        });

        edtCurrency.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //Purpose of this event handler is to clear the value of the box that isn't selected
                //This is so it's easy to understand which currency is the input vs output

                updateCurrentCountry();

                if (hasFocus) {
                    edtUSD.setText("");
                    edtCurrency.setSelectAllOnFocus(true);
                    USDInputted = false;
                } else {
                    edtCurrency.setText("");
                    edtUSD.setSelectAllOnFocus(false);
                    USDInputted = true;
                }
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // "MainActivity" -> "TwitterActivity" ->  "SmsMessageActivity" -> "PackingListActivity" ->
        // "ExchangeActivity" -> "TranslationActivity" -> "PlacesActivity";
        Util.onSwipeChangeScreen(event, this, PackingListActivity.class, TranslationActivity.class);
        return super.onTouchEvent(event);
    }


    private Double round(Double input) {
        return Math.round(input * 100.0) / 100.0;
    }

    public static String getCurrencyCode(String country) {
        if (country.equals("France")) {
            return "USDEUR";
        } else if (country.equals("Germany")) {
            return "USDEUR";
        } else if (country.equals("Japan")) {
            return "USDJPY";
        } else if (country.equals("Mexico")) {
            return "USDMXN";
        } else if (country.equals("Russia")) {
            return "USDRUB";
        } else if (country.equals("United States")) {
            return "USDUSD";
        } else {
            return "error unknown country code";
        }
    }

    public static String getCurrencyName(String country) {
        if (country.equals("France")) {
            return "Euro";
        } else if (country.equals("Germany")) {
            return "Euro";
        } else if (country.equals("Japan")) {
            return "Japanese Yen";
        } else if (country.equals("Mexico")) {
            return "Mexican Peso";
        } else if (country.equals("Russia")) {
            return "Ruble";
        } else if (country.equals("United States")) {
            return "US Dollar";
        } else {
            return "error unknown country code";
        }
    }

    public void updateCurrentCountry() {

        //To figure out the country, we use Geocoder, a built-in android class that takes
        //latitude and longitude and returns a country (and can return other things like address, city, etc)

        GPSTracker gps = new GPSTracker(ExchangeActivity.this);


        if (gps.canGetLocation() && isNetworkAvailable() == true) {
            //If have gps AND network, we can get the country

            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1);

            } catch (IOException e) {
                e.printStackTrace();
            }


            Log.d("CS591", "got here");

            if (addresses.isEmpty() == false) {

                currentCountry = addresses.get(0).getCountryName();
                preferenceEditor.putString("country", currentCountry);
                preferenceEditor.apply();


                GetRates job = new GetRates();
                job.execute();

            } else {
                //Almsot unreachable case unless somehow network is lost after entering the network check if statement
                //The other case is if you haven't sent gps data to the emulator, it will also get here

                Log.d("CS591", "Need to send GPS Data to emulator");
                //Toast.makeText(getApplicationContext(), "Please enable gps connection", Toast.LENGTH_LONG).show();
                currentCountry = preferenceSettings.getString("country", "Unknown Country");
            }



        } else {
            Log.d("CS591", "3rd case");
            //If we don't have gps AND network, then we use the values from device storage

            if(gps.canGetLocation() == false && isNetworkAvailable() == false ){
                Log.d("CS591", "Neither gps nor network available");
                lblErrorMessages.setText("GPS & network unavailable");
            }else if(gps.canGetLocation() == false && isNetworkAvailable() == true){
                Log.d("CS591", "Gps unavailable");
                lblErrorMessages.setText("GPS unavailable");
            }else{
                lblErrorMessages.setText("Network unavailable");
                Log.d("CS591", "Network unavailable");
            }

            currentCountry = preferenceSettings.getString("country", "Unknown Country");
        }


        //Updates the values for the labels
        lblCurrentCountryDisplay = (TextView) findViewById(R.id.lblCurrentCountryDisplay);
        lblCurrencyDisplay = (TextView) findViewById(R.id.lblCurrencyDisplay);
        txtCurrencyTitle = (TextView) findViewById(R.id.txtCurrencyTitle);

        lblCurrentCountryDisplay.setText(currentCountry);
        lblCurrencyDisplay.setText(getCurrencyName(currentCountry));

        String temp = getCurrencyCode(currentCountry);
        txtCurrencyTitle.setText(temp.charAt(3) + "" + temp.charAt(4) + "" + temp.charAt(5));
    }


    public boolean isNetworkAvailable() {
        //This entire method is from: http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
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

    private void initializeSharedPreferences() {

        preferenceSettings = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        preferenceEditor = preferenceSettings.edit();

        if (preferenceSettings.getFloat("exchangeRate", -1) == -1) {
            preferenceEditor.putFloat("exchangeRate", 1);
        }

        if (preferenceSettings.getString("country", "empty").equals("empty")) {
            preferenceEditor.putString("country", "Unknown Country");
        }

        if (preferenceSettings.getString("dateUpdated", "empty").equals("empty")) {
            preferenceEditor.putString("dateUpdated", "never");
        }

        if (preferenceSettings.getFloat("latitude", (float) -1) == -1) {
            preferenceEditor.putFloat("latitude", (float) 0);
        }

        if (preferenceSettings.getFloat("longitude", (float) -1) == -1) {
            preferenceEditor.putFloat("longitude", (float) 0);
        }

        preferenceEditor.apply();
    }


    private class GetRates extends AsyncTask<String, Void, String> {

        URL url;
        String data = null;

        @Override
        protected String doInBackground(String[] params) {

            try {
                url = new URL("http://apilayer.net/api/live?access_key=XXX_FILL_ME_XXX");
                data = IOUtils.toString(url);

            } catch (MalformedURLException e1) {

            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String message) {

            //This method handles the API calls.
            //For exchange rate we are using a service called currencylayer.com

            String currencyCode = getCurrencyCode(currentCountry);

            try {

                Double conversionRate = 1.0;
                lblLastUpdatedDisplay = (TextView) findViewById(R.id.lblLastUpdatedDisplay);

                if (isNetworkAvailable() == true) {

                    JSONObject json = new JSONObject(data);

                    conversionRate = json.getJSONObject("quotes").getDouble(currencyCode);
                    preferenceEditor.putFloat("exchangeRate", (float) json.getJSONObject("quotes").getDouble(currencyCode));
                    preferenceEditor.apply();

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
                    Date date = new Date();
                    String formattedDate = dateFormat.format(date);
                    lblLastUpdatedDisplay.setText(formattedDate);

                    preferenceEditor.putString("dateUpdated", formattedDate);
                    preferenceEditor.apply();

                } else {
                    conversionRate = (double) preferenceSettings.getFloat("exchangeRate", (float) -1);
                    lblLastUpdatedDisplay.setText(preferenceSettings.getString("dateUpdated", "never"));
                }

                if (USDInputted == true) {
                    edtCurrency.setText(round(Double.parseDouble(edtUSD.getText().toString()) * conversionRate) + "");
                    edtUSD.setText(Double.parseDouble(edtUSD.getText().toString()) + " ");
                } else {
                    edtUSD.setText(round(Double.parseDouble(edtCurrency.getText().toString()) / conversionRate) + "");
                    edtCurrency.setText(round(Double.parseDouble(edtCurrency.getText().toString())) + "");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            txtCurrencyTitle = (TextView) findViewById(R.id.txtCurrencyTitle);
            String temp = getCurrencyCode(currentCountry);
            txtCurrencyTitle.setText(temp.charAt(3) + "" + temp.charAt(4) + "" + temp.charAt(5));

        }
    }


    private class GPSTracker extends Service implements LocationListener{

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
            // TODO Auto-generated method stub
            updateCurrentCountry();
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
