package org.trackhouse.trackhouse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

public class Home extends AppCompatActivity {

    private static final String TAG = "RedditEntries";

    private static final String BASE_URL = "https://www.reddit.com/r/";

    private Button btnRefreshFeed;
    private EditText mFeedName;
    private String currentFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "onCreate: starting.");

        btnRefreshFeed = (Button) findViewById(R.id.btnRefresh);
        mFeedName = (EditText) findViewById(R.id.feedName);

        init();

        //TODO: There is a null pointer exception to catch here if getText.toString is null. Add logic to catch
        btnRefreshFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedName = mFeedName.getText().toString();
                if(!feedName.equals("")){
                    currentFeed = feedName;
                    init();
                } else {
                    init();
                    //defaultView();  //doesn't currently work
                }
            }
        });
    }

    //TODO: This doesn't work yet. Should get Reddit Front Page if no subreddit is entered in search.
    //TODO: Currently the app crashes if "Refresh" is hit without a subreddit in the search. Add all of the
    //TODO: code from the init() method here within defaultView method, but the feed call will call static defaultFeed instead.
    // private void defaultView()


    //uses Retrofit to get feeds based on the subreddit text entry on Home page
    private void init(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        //TODO: Add code for if current feed != "", we proceed here. Else, we get the static url reddit.com (Front Page).
        //TODO: Look at Feed API class to add static url.
        Call<Feed> call = feedAPI.getFeed(currentFeed);

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

                Toast.makeText(Home.this, "Server response " + response.toString(), Toast.LENGTH_SHORT).show();

                List<Entry> entries = response.body().getEntries();

                Log.d(TAG, "onResponse: entries: " + response.body().getEntries());

                //Log.d(TAG, "onResponse: author: " + entries.get(1).getAuthor().getName());

                //Log.d(TAG, "onResponse: updated: " + entries.get(1).getUpdated());

                //Log.d(TAG, "onResponse: title: " + entries.get(1).getTitle());


                //List to hold card view details for posts to display in recycler view
                final ArrayList<Post> posts = new ArrayList<Post>();

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

                    //handles NullPointerException errors when retrieving post data - specifically for null author
                    try{
                        posts.add(new Post(
                                entries.get(i).getTitle(),
                                entries.get(i).getAuthor().getName(),
                                entries.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(lastPosition)  //image

                        ));

                    }catch (NullPointerException e){
                        posts.add(new Post(
                                entries.get(i).getTitle(),
                                "None",
                                entries.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(lastPosition)  //image

                        ));

                        Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage());

                    }
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
                CustomListAdapter customListAdapter = new CustomListAdapter(Home.this, R.layout.card_layout_posts, posts);
                listView.setAdapter(customListAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "onItemClick: Clicked: " + posts.get(position).toString());
                        Intent intent = new Intent(Home.this, Comments.class);
                        intent.putExtra("@string/post_url", posts.get(position).getPostURL());
                        intent.putExtra("@string/post_thumbnail", posts.get(position).getThumbnailURL());
                        intent.putExtra("@string/post_title", posts.get(position).getTitle());
                        intent.putExtra("@string/post_author", posts.get(position).getAuthor());
                        intent.putExtra("@string/post_updated", posts.get(position).getDate_updated());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(Home.this, "An error occurred while retrieving RSS" + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }
}
