package org.trackhouse.trackhouse;

import org.trackhouse.trackhouse.Comments.CheckComment;
import org.trackhouse.trackhouse.RedditAccount.CheckLogin;
import org.trackhouse.trackhouse.model.Feed;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Create FeedAPI Interface for use with Retrofit. The base URL is set here and
 * the non-static feed path is specified. See comment for static feed option.
 */

public interface FeedAPI {

    String BASE_URL = "https://www.reddit.com";

    //non-static feed name
    @GET("/r/{feed_name}/.rss")
    Call<Feed> getFeed(@Path("feed_name") String feed_name);

    @POST("{user}")
    Call<CheckLogin> redditSignIn(
            //header map allows for a list of keys and values
            @HeaderMap Map<String, String> headers,
            @Path("user") String username,
            @Query("user") String user,
            @Query("passwd") String password,
            @Query("api_type") String type
            );

    //reddit API requires both the user modhash and the cookie to post comments
    @POST("{comment}")
    Call<CheckComment> submitComment(
            //header map allows for a list of keys and values
            @HeaderMap Map<String, String> headers,
            @Path("comment") String comment,
            @Query("parent") String parent,
            @Query("amp;text") String text
    );


}
