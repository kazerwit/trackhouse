package org.trackhouse.trackhouse.Comments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.trackhouse.trackhouse.ExtractXML;
import org.trackhouse.trackhouse.FeedAPI;
import org.trackhouse.trackhouse.R;
import org.trackhouse.trackhouse.RedditAccount.RedditLoginActivity;
import org.trackhouse.trackhouse.URLS;
import org.trackhouse.trackhouse.WebViewActivity;
import org.trackhouse.trackhouse.model.Feed;
import org.trackhouse.trackhouse.model.entry.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * CommentsActivity activity to show post details. This activity is triggered when a user clicks on a post from the HomeActivity page.
 */

public class CommentsActivity extends AppCompatActivity {

    URLS urls = new URLS();

    private static final String TAG = "CommentsActivity";
    private static String postURL, postThumbnailURL, postTitle, postAuthor, postUpdated, postId;
    private int defaultImage;
    private String currentFeed;
    private String postAuthorName;
    private String authorName;
    private String authorURL;
    private ListView mListView;
    private TextView loadingText;
    private ArrayList<Comment> mComments;
    private ProgressBar mProgressBar;
    private FloatingActionButton mCommentsFAB;

    //strings for shared preferences to store user variables
    private String modhash;
    private String cookie;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Log.d(TAG, "onCreate: Started");

        setupToolbar();

        getSessionParams();

        mProgressBar = (ProgressBar) findViewById(R.id.commentsLoading);
        mProgressBar.setVisibility(View.VISIBLE);
        loadingText = (TextView) findViewById(R.id.commentsLoadingText);

        setupImageLoader();

        initPost();

        init();

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

                switch (item.getItemId()){
                    case R.id.navigation_reddit_login:
                        Intent intent = new Intent(CommentsActivity.this, RedditLoginActivity.class);
                        startActivity(intent);
                }

                return false;
            }
        });

    }


    private void init(){

        Log.d(TAG, "init method started");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urls.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        Call<Feed> call = feedAPI.getFeed(currentFeed);

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                Log.d(TAG, "onResponse: feed: " + response.body().toString());

                //shows server response code. If OK will show 200
                Log.d(TAG, "onResponse: Server Response: " + response.toString());

                Toast.makeText(CommentsActivity.this, "Server response " + response.toString(), Toast.LENGTH_SHORT).show();

                mComments = new ArrayList<Comment>();
                List<Entry> entries = response.body().getEntries();
                for(int i = 0; i < entries.size(); i ++){
                    ExtractXML extract = new ExtractXML("<div class=\"md\"><p>", entries.get(i).getContent(), "</p>" );
                    //creates a list to store comment details for the card view
                    List<String> commentDetails = extract.start();

                    try{
                        mComments.add(new Comment(
                                commentDetails.get(0),
                                entries.get(i).getAuthor().getName(),
                                entries.get(i).getUpdated(),
                                entries.get(i).getId()
                        ));

                    }catch (IndexOutOfBoundsException e) {
                        mComments.add(new Comment(
                                "Error reading comment",
                                "None",
                                "None",
                                "None"
                        ));
                        Log.e(TAG, "onResponse: IndexOutOfBoundsException: " + e.getMessage());

                    } catch (NullPointerException e){
                            mComments.add(new Comment(
                                    commentDetails.get(0),
                                    "None",
                                    entries.get(i).getUpdated(),
                                    entries.get(i).getId()
                            ));
                            Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage());
                    }

                    //TODO: Right now the comments are showing &39; instead of ' where people use plurals - fix it.

                }
                mListView = (ListView) findViewById(R.id.commentsListView);
                CommentsListAdapter adapter = new CommentsListAdapter(CommentsActivity.this, R.layout.comments_layout, mComments);
                mListView.setAdapter(adapter);

                //TODO: this should get the comment ID and post the reply to that comment, but it isn't working
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                     @Override
                                                     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                         getUserComment(mComments.get(position).getId());
                                                     }
                                                 });

                        mProgressBar.setVisibility(View.GONE);
                loadingText.setText("");
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS within call.enqueue: " + t.getMessage());
                Toast.makeText(CommentsActivity.this, "An error occurred while retrieving feed " + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void initPost() {

        Log.d(TAG, "initPost method started.");

        Intent incomingIntent = getIntent();
        postURL = incomingIntent.getStringExtra("@string/post_url");
        postThumbnailURL = incomingIntent.getStringExtra("@string/post_thumbnail");
        postTitle = incomingIntent.getStringExtra("@string/post_title");
        postAuthor = incomingIntent.getStringExtra("@string/post_author");
        postUpdated = incomingIntent.getStringExtra("@string/post_updated");
        postId = incomingIntent.getStringExtra("@string/post_id");

        TextView title = (TextView) findViewById(R.id.postTitle);
        TextView author = (TextView) findViewById(R.id.postAuthor);
        TextView updated = (TextView) findViewById(R.id.postUpdated);
        ImageView thumbnail = (ImageView) findViewById(R.id.post_thumbnail);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.postsLoading);
        mCommentsFAB = (FloatingActionButton) findViewById(R.id.fab_comment);

        title.setText(postTitle);
        author.setText(postAuthor);
        updated.setText(postUpdated);
        displayImage(postThumbnailURL, thumbnail, progressBar);

        //this splits the URL to get the comments for a particular post. NSFW posts will throw an error
        //so we catch it here.
        try {
            Log.d(TAG, "postURL: " + postURL);
            String[] splitURL = postURL.split(urls.BASE_URL);
            currentFeed = splitURL[1];
            Log.d(TAG, "initPost: current feed: " + currentFeed);
            String [] postAuthorName = postAuthor.split("u/");
            authorName = postAuthorName[1];
            Log.d(TAG, "author name: " + authorName);
            authorURL = "https://www.reddit.com/user/" + authorName;
            Log.d(TAG, "author URL: " + authorURL);

        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "initPost: ArrayIndexOutOfBoundsException: " + e.getMessage());
        }

        //when "reply" button is clicked, it will call getUserComment, which opens a dialog/text entry
        //for comment.
        mCommentsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: reply.");
                getUserComment(postId);
            }
        });


        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Opening URL in web view" + postURL);
                Intent intent = new Intent (CommentsActivity.this, WebViewActivity.class);
                intent.putExtra("url", postURL);
                startActivity(intent);
            }
        });

        author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Opening Reddit user page" + authorURL);
                Intent intent = new Intent (CommentsActivity.this, WebViewActivity.class);
                intent.putExtra("url", authorURL);
                startActivity(intent);
            }
        });
    }

    //creates dialog box for user comment, which will show when a user clicks a comment or
    //clicks the "reply" button
    private void getUserComment(String post_id){
        final Dialog dialog = new Dialog(CommentsActivity.this);
        dialog.setTitle("dialog");
        dialog.setContentView(R.layout.comment_input_dialog);

        //this sets dialog box parameters so that dialog doesn't take up full screen. Change
        //later if full screen desired.

        int width = (int)(getResources().getDisplayMetrics().widthPixels*.95);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*.55);

        dialog.getWindow().setLayout(width, height);
        dialog.show();

        ImageButton btnPostComment = (ImageButton) dialog.findViewById(R.id.post_button);
        ImageButton btnPostBack = (ImageButton) dialog.findViewById(R.id.post_back_button);
        final EditText comment = (EditText) dialog.findViewById(R.id.dialog_comment);

        Toolbar mReplyToolbar = (Toolbar) dialog.findViewById(R.id.toolbar_reply);
        //TODO: fix below code, currently includes action bar and text is black
        //setSupportActionBar(mReplyToolbar);
        //getSupportActionBar().setTitle("Reply");

        dialog.setCanceledOnTouchOutside(true);

        btnPostComment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "onCLick: Attempting to post comment");

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(urls.COMMENT_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                FeedAPI feedAPI = retrofit.create(FeedAPI.class);

                HashMap<String, String> headerMap = new HashMap<>();
                headerMap.put("User-Agent", username);
                headerMap.put("X-Modhash", modhash);
                headerMap.put("cookie", "reddit_session=" + cookie);

                Log.d(TAG, "btnPostComment  \n" +
                        "username: " + username + "\n" +
                        "modhash: " + modhash + "\n" +
                        "cookie: " + cookie + "\n"
                );

                String theComment = comment.getText().toString();

                //TODO: comment is currently posting under the main entry, but not as a reply to the correct
                //TODO: comment. Need to maybe add comment URL details for the comment we're replying to in the object??
                Call<CheckComment> call = feedAPI.submitComment(headerMap, "comment", postId, theComment);
                call.enqueue(new Callback<CheckComment>() {
                    @Override
                    public void onResponse(Call<CheckComment> call, Response<CheckComment> response) {
                        try {
                            //Log.d(TAG, "onResponse: feed: " + response.body().toString());

                            //shows server response code. If OK will show 200
                            Log.d(TAG, "onResponse: Server Response: " + response.toString());

                            //retrieve success response from server. success value will be true or false
                            String postSuccess = response.body().getSuccess();

                            if(postSuccess.equals("true")){
                                dialog.dismiss();
                                Toast.makeText(CommentsActivity.this, "Post Successful", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(CommentsActivity.this, "An error occurred. Did you sign in? Server response: " + postSuccess, Toast.LENGTH_LONG).show();
                            }

                        } catch (NullPointerException e) {
                            Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage());

                        }
                    }

                    @Override
                    public void onFailure(Call<CheckComment> call, Throwable t) {
                        Log.e(TAG, "onFailure: Unable to post comment: " + t.getMessage());
                        Toast.makeText(CommentsActivity.this, "An error occurred while posting comment" + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

            }
        });
    }

    /**
     * Retrieves shared preferences from login activity and saves the strings to local variables
     */
    private void getSessionParams(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CommentsActivity.this);

        username = preferences.getString("@string/session_username", "");
        modhash = preferences.getString("@string/session_modhash", "");
        cookie = preferences.getString("@string/session_cookie", "");

        Log.d(TAG, "getSessionParams: Storing session variables:  \n" +
                "username: " + username + "\n" +
                "modhash: " + modhash + "\n" +
                "cookie: " + cookie + "\n"
        );
    }


    private void displayImage(String imageURL, ImageView imageView, final ProgressBar progressBar){

        //create the imageloader object
        ImageLoader imageLoader = ImageLoader.getInstance();

        int defaultImage = CommentsActivity.this.getResources().getIdentifier("@drawable/reddit_alien",null,CommentsActivity.this.getPackageName());

        //create display options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();

        //download and display image from url
        imageLoader.displayImage(imageURL, imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(view.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(view.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(view.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(view.GONE);
            }

        });
    }

    /**
     * Required for setting up the Universal Image loader Library
     */
    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                CommentsActivity.this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP

        defaultImage = CommentsActivity.this.getResources().getIdentifier("@drawable/reddit_alien",null,CommentsActivity.this.getPackageName());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    /**
     * If user is redirected to Comments Activity, this will call the getSessionParams method
     * in order to get the Shared Preferences from the login info.
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume: Resuming Comments Activity");
        getSessionParams();
    }
}
