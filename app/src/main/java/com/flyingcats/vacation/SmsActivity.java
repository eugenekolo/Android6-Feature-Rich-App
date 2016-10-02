package com.flyingcats.vacation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.SharedPreferences;


public class SmsActivity extends AppCompatActivity {
    private static final Uri CONTACTS_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    private static final int PICK_CONTACT = 1;
    ListView mylistview;
    public static final String TAG = "CS591";
    ArrayList<String> contactNumbers;
    ArrayList<String> contactNames;
    ArrayAdapter<String> listAdapter;
    Button btnContacts, btnSMS;
    String chosen_dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        chosen_dest = getIntent().getStringExtra("IntentData");
        mylistview = (ListView) findViewById(R.id.listViewName);
        btnContacts = (Button) findViewById(R.id.btnContacts);
        btnSMS = (Button) findViewById(R.id.btnSMS);
        contactNumbers = new ArrayList<String>();
        contactNames = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, R.layout.listitem, contactNames);
        mylistview.setAdapter(listAdapter);
        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SmsActivity.this);
                dialog.setTitle("Remove From List");
                dialog.setMessage("Are you sure you want to remove " + contactNames.get(position) + " from this messaging list?");
                final int positionToRemove = position;
                dialog.setNegativeButton("Cancel", null);
                dialog.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       listAdapter.remove(contactNames.get(positionToRemove));
                        contactNumbers.remove(positionToRemove);
                        listAdapter.notifyDataSetChanged();
                        saveSharedPreferenceInfo(); //save shared preference info each time a contact is removed
                    }
                });
                dialog.show();
            }
        });

        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent contactIntent = new Intent(Intent.ACTION_PICK, CONTACTS_CONTENT_URI);   // create Intent object for picking data from Contacts database
                    startActivityForResult(contactIntent, PICK_CONTACT);  //start Activity to open contacts list - result code is 1
                } catch (Exception e) {
                    Log.e(TAG, e.toString());  //errors are in red.
                }
            }
        });
        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String phoneNumbers = "";
                    for (int i = 0; i < contactNumbers.size(); i++) {
                        phoneNumbers += contactNumbers.get(i) + ";"; //write to one long string; semicolon delimited phone numbers
                    }
                    String message = "Hi, going on vacation to " + chosen_dest + "!"; //message to be send in SMS

                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("address", phoneNumbers); //set address to be sent to as string of all phone numbers
                    sendIntent.putExtra("sms_body", message); //set SMS body to message
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    startActivity(sendIntent);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());  //errors are in red.
                }
            }
        });
    }
    @Override
    public void onResume() { //set all variables here after onCreateView
        super.onResume();
        retrieveSharedPreferenceInfo(); //retrieve saved shared preference info
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor c = managedQuery(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (hasPhone.equalsIgnoreCase("1")) { //if selected contact had a phone number
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null, null);
                    phones.moveToFirst();
                    String cNumber = phones.getString(phones.getColumnIndex("data1"));

                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); //get display name and save to string
                    if (!contactNumbers.contains(cNumber)) { //if not a duplicate, add to list
                        listAdapter.add(name); //add name to listAdapter
                        contactNumbers.add(cNumber); //add phone number to contactNumbers ArrayList
                        saveSharedPreferenceInfo(); //save shared preference info each time a contact is chosen
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "This contact already exist in your list.", Toast.LENGTH_LONG).show();
                    }
                }
                else { //if does not have a phone number
                    Toast.makeText(getApplicationContext(), "This contact does not have a phone number. Contact not added.", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    void saveSharedPreferenceInfo(){
        SharedPreferences simpleAppInfo = getSharedPreferences("ActivityOneInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = simpleAppInfo.edit();
        String longNamesStr = "";
        if (!contactNames.isEmpty()) { //if not empty
            for (int i = 0; i < contactNames.size(); i++) {
                longNamesStr += listAdapter.getItem(i) + ","; //add string to array
            }
        }
        else { //if empty
            longNamesStr = "";
        }
        String longNumbersStr = "";
        if (!contactNames.isEmpty()) { //if not ampty
            longNamesStr.substring(0, longNamesStr.length() - 1); //remove the last char from the string (so no trailing comma)

            for (int i = 0; i < contactNumbers.size(); i++) {
                longNumbersStr += contactNumbers.get(i) + ","; //add string to array
            }
            longNumbersStr.substring(0, longNumbersStr.length() - 1); //remove the last char from the string (so no trailing comma)
        }
        else { //if empty
            longNamesStr = "";
        }

        editor.putString("NamesString", longNamesStr);
        editor.putString("NumbersString", longNumbersStr);
        editor.apply();

    }

    void retrieveSharedPreferenceInfo() {
        SharedPreferences simpleAppInfo = getSharedPreferences("ActivityOneInfo", Context.MODE_PRIVATE);
       //Retrieving data from shared preferences hashmap
        String retrieveNames;
        String retrieveNumbers;
        retrieveNames = simpleAppInfo.getString("NamesString", "");
        retrieveNumbers = simpleAppInfo.getString("NumbersString", "");
        if (retrieveNames!= "" && retrieveNumbers!= "") {
            listAdapter.clear();
            contactNumbers.clear();
            String[] itemsNames = retrieveNames.split(",");
            String[] itemsNumbers = retrieveNumbers.split(",");
            List<String> listNames = new ArrayList<String>(Arrays.asList(itemsNames));
            List<String> listNumbers = new ArrayList<String>(Arrays.asList(itemsNumbers));
           listAdapter.addAll(listNames);
            contactNumbers.addAll(listNumbers);
            listAdapter.notifyDataSetChanged();
        }

    }

}