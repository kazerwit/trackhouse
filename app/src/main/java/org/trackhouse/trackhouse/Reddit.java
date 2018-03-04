package org.trackhouse.trackhouse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.trackhouse.trackhouse.model.Feed;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

//TODO: Currently pulls from a static subreddit to get information on entries (posts). Change later
//to be able to pull dynamic URLS.

public class Reddit extends AppCompatActivity {

    private static final String TAG = "RedditActivity";

    private static final String BASE_URL = "https://www.reddit.com/r/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        Call<Feed> call = feedAPI.getFeed();

        call.enqueue(new Callback<Feed>() {
            /**
             * If call is successful, gets the objects specified within onResponse and handles the strings.
             * Can modify later to get more info., such as info. on the subreddit rather than just
             * the entries.
             * @param call
             * @param response
             */
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                Log.d(TAG, "onResponse: feed: " + response.body().toString());

                //shows server response code. If OK will show 200
                Log.d(TAG, "onResponse: Server Response: " + response.toString());

                Toast.makeText(Reddit.this, "Server response " + response.toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(Reddit.this, "An error occurred while retrieving RSS" + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}
