package org.trackhouse.trackhouse.RedditAccount;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.trackhouse.trackhouse.R;

/**
 * Class for Reddit Login Activity.
 */

public class RedditLoginActivity extends AppCompatActivity{

    private static final String TAG = "RedditLoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_login);
        Log.d(TAG, "onCreate: started.");
    }
}
