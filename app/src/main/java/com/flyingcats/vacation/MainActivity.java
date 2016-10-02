package com.flyingcats.vacation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.flyingcats.vacation.util.Util;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    private String TAG = "CS591";
    private Spinner spinner_dest;

    private static final String TWITTER_KEY = "XXX_FILL_ME_XXX";
    private static final String TWITTER_SECRET = "XXX_FILL_ME_XXX";

    private float acceleration = 0.00f;
    private float currentAcceleration = SensorManager.GRAVITY_EARTH;
    private float lastAcceleration = SensorManager.GRAVITY_EARTH;
    private static int SIGNIFICANT_SHAKE = 200000;   //tweak this as necessary

    private static Util.Accelerometer accelerometer;
    private String chosen_dest;
    public static String chosen_dest_static;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(getBaseContext() ,Manifest.permission.WRITE_CALENDAR);

        spinner_dest = (Spinner) findViewById(R.id.spinner_dest);

        /* Setup for accelerometer stuff */
        accelerometer = new Util.Accelerometer((SensorManager)getApplicationContext().getSystemService(Context.SENSOR_SERVICE));

        /* Setup for Twitter */
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        //create spinner for languages
        chosen_dest = ""; //default language is English
        spinner_dest = (Spinner) findViewById(R.id.spinner_dest); // Spinner element /
        final List<String> destinations = new ArrayList<String>();  // Spinner Drop down elements
        destinations.add("Paris");
        destinations.add("Cancun");
        destinations.add("Berlin");
        destinations.add("Tokyo");
        destinations.add("Moscow");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, destinations);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner_dest.setAdapter(dataAdapter1);

        spinner_dest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (item.equals("Paris")) {
                    chosen_dest = "Paris";
                    chosen_dest_static = "Paris";
                } else if (item.equals("Cancun")) {
                    chosen_dest = "Cancun";
                    chosen_dest_static = "Cancun";
                } else if (item.equals("Berlin")) {
                    chosen_dest = "Berlin";
                    chosen_dest_static = "Berlin";
                } else if (item.equals("Tokyo")) {
                    chosen_dest = "Tokyo";
                    chosen_dest_static = "Tokyo";
                } else if (item.equals("Moscow")) {
                    chosen_dest = "Moscow";
                    chosen_dest_static = "Moscow";
                } else {//default is Paris
                    chosen_dest = "Paris";
                    chosen_dest_static = "Paris";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        accelerometer.enableAccelerometerListening(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                // Step 1: save previous acceleration value
                lastAcceleration = currentAcceleration;
                // Step 2: calculate the current acceleration
                currentAcceleration = x * x + y * y + z * z;
                // Step 3: calculate the change in acceleration
                acceleration = currentAcceleration * (currentAcceleration - lastAcceleration);

                // Step 4: if the change in acceleration is above a certain threshold, open the next screen
                if (acceleration > SIGNIFICANT_SHAKE) {
                    startActivity(new Intent("com.flyingcats.vacation.SmsActivity"));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Does nothing ATM
            }
        });
    }

    @Override
    protected void onStop() {
        accelerometer.disableAccelerometerListening();
        super.onStop();
    }

    public void onAnyButtonClick(View v) {
       // InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        Intent NextScreen;
        switch (v.getId()){
            case R.id.imgBtnTwitter:
                NextScreen = new Intent("com.flyingcats.vacation.TwitterActivity");
                NextScreen.putExtra("IntentData", chosen_dest); //send destination string
                startActivity(NextScreen);
                break;

            case R.id.imgBtnSms:
                NextScreen = new Intent("com.flyingcats.vacation.SmsActivity");
                NextScreen.putExtra("IntentData", chosen_dest); //send destination string
                startActivity(NextScreen);
                break;

            case R.id.imgBtnPackingList :
                NextScreen = new Intent("com.flyingcats.vacation.PackingListActivity");
                NextScreen.putExtra("IntentData", chosen_dest); //send destination string
                startActivity(NextScreen);
                break;

            case R.id.imgBtnExchange:
                NextScreen = new Intent("com.flyingcats.vacation.ExchangeActivity");
                startActivity(NextScreen);
                break;

            case R.id.imgBtnTranslate:
                NextScreen = new Intent("com.flyingcats.vacation.TranslationActivity");
                startActivity(NextScreen);
                break;

            case R.id.imgBtnPlaces:
                NextScreen = new Intent("com.flyingcats.vacation.PlacesActivity");
                startActivity(NextScreen);
                break;

            case R.id.imgBtnCalendar:
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", cal.getTimeInMillis()); //set default start date to current date
                intent.putExtra("allDay", false);
                intent.putExtra("endTime", cal.getTimeInMillis()+ (1000 * 604800)); //set default endtime to one week from current date
                intent.putExtra("title", "Vacation");
                intent.putExtra("eventLocation", chosen_dest);
                intent.putExtra("description", "Going on vacation to " + chosen_dest);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // "MainActivity" -> "TwitterActivity" ->  "SmsActivity" -> "PackingListActivity" ->
        // "ExchangeActivity" -> "TranslationActivity" -> "PlacesActivity";
        Util.onSwipeChangeScreen(event, this, PlacesActivity.class, TwitterActivity.class);
        return super.onTouchEvent(event);
    }
}
