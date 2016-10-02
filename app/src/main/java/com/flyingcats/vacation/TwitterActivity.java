package com.flyingcats.vacation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.flyingcats.vacation.util.Util;
import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.identity.*;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

public class TwitterActivity extends Activity {
    private String chosen_dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chosen_dest = getIntent().getStringExtra("IntentData");
        setupTweet();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupTweet() {
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text("Going on vacation to  " + chosen_dest + "!");
        builder.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // "MainActivity" -> "TwitterActivity" ->  "SmsActivity" -> "PackingListActivity" ->
        // "ExchangeActivity" -> "TranslationActivity" -> "PlacesActivity";
        Util.onSwipeChangeScreen(event, this, MainActivity.class, SmsActivity.class);
        return super.onTouchEvent(event);
    }
}
