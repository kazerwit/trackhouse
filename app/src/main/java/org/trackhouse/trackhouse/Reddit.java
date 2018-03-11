package org.trackhouse.trackhouse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.trackhouse.trackhouse.model.Feed;
import org.trackhouse.trackhouse.model.entry.Entry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

//TODO: Currently pulls from a static subreddit to get information on entries (posts). Change later
//to be able to pull dynamic URLS.

public class Reddit extends AppCompatActivity {

    private static final String TAG = "RedditEntries";

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

                List<Entry> entries = response.body().getEntries();

                Log.d(TAG, "onResponse: entries: " + response.body().getEntries());

                //Log.d(TAG, "onResponse: author: " + entries.get(1).getAuthor().getName());

                //Log.d(TAG, "onResponse: updated: " + entries.get(1).getUpdated());

                //Log.d(TAG, "onResponse: title: " + entries.get(1).getTitle());


                //List to hold card view details for posts to display in recycler view
                ArrayList<Post> posts = new ArrayList<Post>();

                for(int i = 0; i < entries.size(); i++) {
                    ExtractXML extractXML1 = new ExtractXML("<a href=", entries.get(i).getContent());
                    List<String> postContent = extractXML1.start();

                    ExtractXML extractXML2 = new ExtractXML("<img src=", entries.get(i).getContent());

                    try {
                        postContent.add(extractXML2.start().get(0));
                    } catch (NullPointerException e){
                        //TODO: add default image for posts without an image
                        postContent.add(null);
                        Log.e(TAG, "onResponse: NullPointerException(thumbnail)" + e.getMessage());
                    } catch (IndexOutOfBoundsException e){
                        postContent.add(null);
                        Log.e(TAG, "onResponse: IndexOutOfBoundsException(thumbnail)" + e.getMessage());
                    }
                    int lastPosition = postContent.size() - 1;
                    posts.add(new Post(
                            entries.get(i).getTitle(),
                            entries.get(i).getAuthor().getName(),
                            entries.get(i).getUpdated(),
                            postContent.get(0),
                            postContent.get(lastPosition)

                    ));
                }

                //test to print out post details in log for card view
                //TODO: delete this test later
                for(int j = 0; j < posts.size(); j++){

                    Log.d(TAG, "onResponse: \n " +
                        "PostURL: " + posts.get(j).getPostURL() + "\n" +
                            "ThumbnailURL: " + posts.get(j).getThumbnailURL() + "\n" +
                            "Title: " + posts.get(j).getTitle() + "\n" +
                            "Author: " + posts.get(j).getAuthor() + "\n" +
                            "Updated: " + posts.get(j).getDate_updated() + "\n");
                }
                ListView listView = (ListView) findViewById(R.id.listView);
                CustomListAdapter customListAdapter = new CustomListAdapter(Reddit.this, R.layout.card_layout_posts, posts);
                listView.setAdapter(customListAdapter);

            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(Reddit.this, "An error occurred while retrieving RSS" + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}
