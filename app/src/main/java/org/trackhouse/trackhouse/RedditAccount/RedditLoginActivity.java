package org.trackhouse.trackhouse.RedditAccount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.trackhouse.trackhouse.Comments.CommentsActivity;
import org.trackhouse.trackhouse.FeedAPI;
import org.trackhouse.trackhouse.R;
import org.trackhouse.trackhouse.URLS;
import org.trackhouse.trackhouse.WebViewActivity;
import org.trackhouse.trackhouse.model.Feed;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Reddit login activity allows user to sign into Reddit and then redirects back to the previous activity.
 */

public class RedditLoginActivity extends AppCompatActivity{

    private static final String TAG = "RedditLoginActivity";

    private ProgressBar mProgressBar;
    private EditText mUsername;
    private EditText mPassword;
    private TextView mRegister;
    private String registerURL;
    private URLS urls = new URLS();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_login);
        Log.d(TAG, "onCreate: started.");

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        mPassword = (EditText) findViewById(R.id.input_password);
        mUsername = (EditText) findViewById(R.id.input_username);
        mRegister = (TextView) findViewById(R.id.link_signup);
        mProgressBar = (ProgressBar) findViewById(R.id.login_request_loading);
        mProgressBar.setVisibility(View.GONE);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Attempting to log in.");
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                if(!username.equals("") && !password.equals("")){
                    mProgressBar.setVisibility(View.VISIBLE);
                    //call method and pass username and password
                    redditSignIn(username, password);
                }

            }
        });

        //loads web view if user needs to create a Reddit account
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerURL = "https://www.reddit.com/register";
                Log.d(TAG, "onClick: Opening Reddit user page" + registerURL);
                Intent intent = new Intent (RedditLoginActivity.this, WebViewActivity.class);
                intent.putExtra("url", registerURL);
                startActivity(intent);
            }
        });
    }

    /**
     * Creates method to sign in to Reddit using HashMap and passing username and password strings.
     * @param username
     * @param password
     */
    private void redditSignIn(final String username, final String password){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urls.LOGIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");

        Call<CheckLogin> call = feedAPI.redditSignIn(headerMap, username, username, password, "json");

        call.enqueue(new Callback<CheckLogin>() {
            @Override
            public void onResponse(Call<CheckLogin> call, Response<CheckLogin> response) {
                try {
                    //Log.d(TAG, "onResponse: feed: " + response.body().toString());

                    //shows server response code. If OK will show 200
                    Log.d(TAG, "onResponse: Server Response: " + response.toString());

                    String modhash = response.body().getJson().getData().getModhash();
                    String cookie = response.body().getJson().getData().getCookie();
                    Log.d(TAG, "onResponse: modhash: " + modhash);
                    Log.d(TAG, "onResponse: cookie: " + cookie);

                    if (!modhash.equals("")) {
                        setSessionParams(username, modhash, cookie);
                        mProgressBar.setVisibility(View.GONE);
                        mUsername.setText("");
                        mPassword.setText("");
                        Toast.makeText(RedditLoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        //navigate back to previous activity when done
                        finish();
                    }
                } catch(NullPointerException e){
                    Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage());

                }

            }

            @Override
            public void onFailure(Call<CheckLogin> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to log in: " + t.getMessage());
                Toast.makeText(RedditLoginActivity.this, "An error occurred while logging in" + t.getMessage(), Toast.LENGTH_LONG).show();
                mProgressBar.setVisibility(View.GONE);

            }
        });
    }

    /**
     * Save session parameters to Shared Preferences for use throughout the app.
     * @param username
     * @param modhash
     * @param cookie
     */
    private void setSessionParams(String username, String modhash, String cookie){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RedditLoginActivity.this);
        SharedPreferences.Editor editor = preferences.edit();

        Log.d(TAG, "setSessionParams: Storing session variables:  \n" +
                        "username: " + username + "\n" +
                        "modhash: " + modhash + "\n" +
                        "cookie: " + cookie + "\n"
        );

        editor.putString("@string/session_username", username);
        editor.commit();

        editor.putString("@string/session_modhash", modhash);
        editor.commit();

        editor.putString("@string/session_cookie", cookie);
        editor.commit();

    }
}
