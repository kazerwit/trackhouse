package org.trackhouse.trackhouse;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import org.trackhouse.trackhouse.Comments.CommentsActivity;
import org.trackhouse.trackhouse.RedditAccount.RedditLoginActivity;
import org.trackhouse.trackhouse.model.Feed;
import org.trackhouse.trackhouse.model.entry.Entry;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import android.view.inputmethod.InputMethodManager;

/**
 * Activity class which displays a Home page with Reddit Front Page feed. User can enter a
 * subreddit name in the search to load a different feed.
 */

public class HomeActivity extends AppCompatActivity {

    URLS urls = new URLS();

    private static final String TAG = "HomeActivity";

    private Button btnRefreshFeed;
    private EditText mFeedName;
    private String currentFeed;
    private String defaultFeedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Log.d(TAG, "onCreate: starting.");

        ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);

        //TODO: uncomment this later
        //btnRefreshFeed = (Button) findViewById(R.id.btnRefresh);

        mFeedName = (EditText) findViewById(R.id.feedName);

        setupToolbar();

        //this method loads Reddit's Earthporn subreddit as the home page
        initDefault();

        //TODO: There is a null pointer exception to catch here if getText.toString is null. Add logic to catch
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedName = mFeedName.getText().toString();
                if(!feedName.equals("")){
                    currentFeed = feedName;
                    init();
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    initDefault();  //loads Reddit Earthporn
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }

    //set up toolbar menu with switch statement to handle navigation items
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "onMenuItemClick: clicked menu item: " + item);

                switch (item.getItemId()) {
                    case R.id.navigation_reddit_login:
                        //navigates to Reddit Login Activity
                        Intent intent = new Intent(HomeActivity.this, RedditLoginActivity.class);
                        startActivity(intent);
                        return true;

                    default:
                        //if we got here, the user's action was not recognized
                        //invoke the superclass to handle it
                        return HomeActivity.super.onOptionsItemSelected(item);
                }
            }
        });

    }


    /**
     * Uses Retrofit to get feeds based on the subreddit text entry in search field
     */
    private void init(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urls.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

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
                //Toast.makeText(HomeActivity.this, "Server response " + response.toString(), Toast.LENGTH_SHORT).show();

                List<Entry> entries = response.body().getEntries();


                //List to hold card view details for posts to display in recycler view
                final ArrayList<Post> posts = new ArrayList<Post>();

                for(int i = 0; i < entries.size(); i++) {
                    ExtractXML extractXML1 = new ExtractXML("<a href=", entries.get(i).getContent());
                    List<String> postContent = extractXML1.start();

                    ExtractXML extractXML2 = new ExtractXML("<img src=", entries.get(i).getContent());

                    try {
                        postContent.add(extractXML2.start().get(0));
                    } catch (NullPointerException e){
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
                                postContent.get(lastPosition), //image
                                entries.get(i).getId()

                        ));

                    }catch (NullPointerException e){
                        posts.add(new Post(
                                entries.get(i).getTitle(),
                                "None",
                                entries.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(lastPosition),  //image
                                entries.get(i).getId()
                        ));

                        Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage());

                    }
                }

                /*test to print out post details in log for card view
                for(int j = 0; j < posts.size(); j++){

                    Log.d(TAG, "onResponse: \n " +
                            "PostURL: " + posts.get(j).getPostURL() + "\n" +
                            "ThumbnailURL: " + posts.get(j).getThumbnailURL() + "\n" +
                            "Title: " + posts.get(j).getTitle() + "\n" +
                            "Author: " + posts.get(j).getAuthor() + "\n" +
                            "Updated: " + posts.get(j).getDate_updated() + "\n" +
                            "Id: " + posts.get(j).getId() + "\n");
                }
                 */

                ListView listView = (ListView) findViewById(R.id.listView);
                CustomListAdapter customListAdapter = new CustomListAdapter(HomeActivity.this, R.layout.card_layout_posts, posts);
                listView.setAdapter(customListAdapter);

                //navigates to Comments Activity and passes post information to that activity
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "onItemClick: Clicked: " + posts.get(position).toString());
                        Intent intent = new Intent(HomeActivity.this, CommentsActivity.class);
                        intent.putExtra("@string/post_url", posts.get(position).getPostURL());
                        intent.putExtra("@string/post_thumbnail", posts.get(position).getThumbnailURL());
                        intent.putExtra("@string/post_title", posts.get(position).getTitle());
                        intent.putExtra("@string/post_author", posts.get(position).getAuthor());
                        intent.putExtra("@string/post_updated", posts.get(position).getDate_updated());
                        intent.putExtra("@string/post_id", posts.get(position).getId());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(HomeActivity.this, "An error occurred while retrieving feed. Please enter a subreddit " + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

    /**
     * Gets default feed for activity. Currently gets Earthporn subreddit as default feed.
     */
    private void initDefault(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urls.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        currentFeed="earthporn";

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

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

                //Toast.makeText(HomeActivity.this, "Server response " + response.toString(), Toast.LENGTH_SHORT).show();

                List<Entry> entries = response.body().getEntries();


                //List to hold card view details for posts to display in recycler view
                final ArrayList<Post> posts = new ArrayList<Post>();

                for(int i = 0; i < entries.size(); i++) {
                    ExtractXML extractXML1 = new ExtractXML("<a href=", entries.get(i).getContent());
                    List<String> postContent = extractXML1.start();

                    ExtractXML extractXML2 = new ExtractXML("<img src=", entries.get(i).getContent());

                    try {
                        postContent.add(extractXML2.start().get(0));
                    } catch (NullPointerException e){
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
                                postContent.get(lastPosition), //image
                                entries.get(i).getId()

                        ));

                    }catch (NullPointerException e){
                        posts.add(new Post(
                                entries.get(i).getTitle(),
                                "None",
                                entries.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(lastPosition),  //image
                                entries.get(i).getId()
                        ));

                        Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage());

                    }
                }

                /*test to print out post details in log for card view
                for(int j = 0; j < posts.size(); j++){

                    Log.d(TAG, "onResponse: \n " +
                            "PostURL: " + posts.get(j).getPostURL() + "\n" +
                            "ThumbnailURL: " + posts.get(j).getThumbnailURL() + "\n" +
                            "Title: " + posts.get(j).getTitle() + "\n" +
                            "Author: " + posts.get(j).getAuthor() + "\n" +
                            "Updated: " + posts.get(j).getDate_updated() + "\n" +
                            "Id: " + posts.get(j).getId() + "\n");
                }
                */

                ListView listView = (ListView) findViewById(R.id.listView);
                CustomListAdapter customListAdapter = new CustomListAdapter(HomeActivity.this, R.layout.card_layout_posts, posts);
                listView.setAdapter(customListAdapter);

                //navigates to Comments Activity and passes post information to that activity
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "onItemClick: Clicked: " + posts.get(position).toString());
                        Intent intent = new Intent(HomeActivity.this, CommentsActivity.class);
                        intent.putExtra("@string/post_url", posts.get(position).getPostURL());
                        intent.putExtra("@string/post_thumbnail", posts.get(position).getThumbnailURL());
                        intent.putExtra("@string/post_title", posts.get(position).getTitle());
                        intent.putExtra("@string/post_author", posts.get(position).getAuthor());
                        intent.putExtra("@string/post_updated", posts.get(position).getDate_updated());
                        intent.putExtra("@string/post_id", posts.get(position).getId());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(HomeActivity.this, "An error occurred while retrieving feed. Please enter a subreddit " + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }
}
