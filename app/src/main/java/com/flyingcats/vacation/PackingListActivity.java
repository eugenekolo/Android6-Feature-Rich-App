package com.flyingcats.vacation;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.flyingcats.vacation.db.ItemContract;
import com.flyingcats.vacation.db.ItemDBHelper;
import com.flyingcats.vacation.util.Util;

// Based off of http://www.sitepoint.com/starting-android-development-creating-todo-app/

public class PackingListActivity extends AppCompatActivity {

    private ItemDBHelper helper;
    private ListAdapter listAdapter;
    private View packing_fullscreen;
    private View task_fullscreen;
    private String chosen_dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieves the location of the vacation from the intent data
        chosen_dest = getIntent().getStringExtra("IntentData");
        setContentView(R.layout.activity_packing_list);

        packing_fullscreen = findViewById(R.id.packing_fullscreen);

        packing_fullscreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                // "MainActivity" -> "TwitterActivity" ->  "SmsActivity" -> "PackingListActivity" ->
                // "ExchangeActivity" -> "TranslationActivity" -> "PlacesActivity";
                Util.onSwipeChangeScreen(event, PackingListActivity.this, SmsActivity.class, ExchangeActivity.class);
                return true;
            }
        });

        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    // For the different menu options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Add item to packing list
            case R.id.action_add_item:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add an item");
                builder.setMessage("What item do you want to add?");

                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText numField = new EditText(this);
                numField.setHint("Num. of Items (or leave blank)");
                numField.setInputType(InputType.TYPE_CLASS_NUMBER);
                layout.addView(numField);

                final EditText itemField = new EditText(this);
                itemField.setHint("Item Name");
                layout.addView(itemField);
                builder.setView(layout);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Log.d("MainActivity", inputField.getText().toString());
                        String number = numField.getText().toString();
                        String item = itemField.getText().toString();
                        String input = number + " " + item;
                        Log.d("MainActivity", item);

                        ItemDBHelper helper = new ItemDBHelper(PackingListActivity.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        ContentValues values = new ContentValues();

                        values.clear();
                        values.put(ItemContract.Columns.ITEM, input);

                        db.insertWithOnConflict(ItemContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                        updateUI();
                    }
                });

                builder.setNegativeButton("Cancel", null);
                builder.create().show();
                return true;

            // Get information about how the app developers use the packing list
            case R.id.action_info:
                AlertDialog.Builder info = new AlertDialog.Builder(this);
                info.setTitle("How We Use the Packing List!");
                info.setMessage("Add the items you want to bring on your trip!\n" +
                        "Check the box when you pack it!\nWhen you pack your bags for the return trip, " +
                        "uncheck the boxes to make sure you didn't forget anything!");
                info.create().show();
                return true;

            // Recommendations of things to pack for the different vacation locations
            case R.id.action_recommendation:
                AlertDialog.Builder recommendations = new AlertDialog.Builder(this);
                recommendations.setTitle("Packing List Recommendations for " + chosen_dest);

                LinearLayout layoutRecommendation = new LinearLayout(this);
                layoutRecommendation.setOrientation(LinearLayout.VERTICAL);

                switch (chosen_dest) {
                    case "Paris":
                        recommendations.setMessage("We recommend that you pack:\n" +
                                "Walking Shoes\n" +
                                "Camera\n" +
                                "Euros\n" +
                                "Visa\n" +
                                "European Adapter");
                        break;
                    case "Cancun":
                        recommendations.setMessage("We recommend that you pack:\n" +
                                "Swimming Wear\n" +
                                "Beach Towels\n" +
                                "Sunglasses");
                        break;
                    case "Berlin":
                        recommendations.setMessage("We recommend that you pack:\n" +
                                "European Power Adapter\n" +
                                "Walking Shoes\n" +
                                "Appropriate Attire for Weather");
                        break;
                    case "Tokyo":
                        recommendations.setMessage("We recommend that you pack:\n" +
                                "Power Adapter\n" +
                                "Small Towel\n" +
                                "Mosquito Repellent (For the Summer)");
                        break;
                    case "Moscow":
                        recommendations.setMessage("We recommend that you pack:\n" +
                                "Umbrella\n" +
                                "Formal Attire\n" +
                                "Headscarf\n" +
                                "Rubles\n" +
                                "Hat\n" +
                                "Winter Clothing (Season Dependent)");
                        break;
                }

                recommendations.create().show();
                return true;
            default:
                return false;
        }
    }

    // Updates the packing list from what's stored in the database
    private void updateUI() {
        helper = new ItemDBHelper(PackingListActivity.this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        Cursor cursor = sqlDB.query(ItemContract.TABLE,
                new String[]{ItemContract.Columns._ID, ItemContract.Columns.ITEM},
                null, null, null, null, null);

        ListView listView = (ListView) findViewById(R.id.list);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                // "MainActivity" -> "TwitterActivity" ->  "SmsActivity" -> "PackingListActivity" ->
                // "ExchangeActivity" -> "TranslationActivity" -> "PlacesActivity";
                Util.onSwipeChangeScreen(event, PackingListActivity.this, SmsActivity.class, ExchangeActivity.class);
                return true;
            }
        });

        listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.task_view,
                cursor,
                new String[] { ItemContract.Columns.ITEM},
                new int[] { R.id.itemTextView},
                0
        );
        listView.setAdapter(listAdapter);
    }

    // Deletes the item from the packing list and from the database
    public void onDoneButtonClick (View view) {
        View v = (View) view.getParent();
        TextView itemTextView = (TextView) v.findViewById(R.id.itemTextView);
        String item = itemTextView.getText().toString();

        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                ItemContract.TABLE,
                ItemContract.Columns.ITEM,
                item);


        helper = new ItemDBHelper(PackingListActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        sqlDB.execSQL(sql);
        updateUI();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // "MainActivity" -> "TwitterActivity" ->  "SmsActivity" -> "PackingListActivity" ->
        // "ExchangeActivity" -> "TranslationActivity" -> "PlacesActivity";
        Util.onSwipeChangeScreen(event, this, SmsActivity.class, ExchangeActivity.class);
        return super.onTouchEvent(event);
    }
}
