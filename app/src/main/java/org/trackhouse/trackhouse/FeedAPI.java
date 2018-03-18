package org.trackhouse.trackhouse;

import org.trackhouse.trackhouse.model.Feed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Create FeedAPI Interface for use with Retrofit. The base URL is set here and
 * the non-static feed path is specified. See comment for static feed option.
 */

public interface FeedAPI {

    String BASE_URL = "https://www.reddit.com/r/";

    //non-static feed name
    @GET("{feed_name}/.rss")
    Call<Feed> getFeed(@Path("feed_name") String feed_name);

    //static feed name
    //@GET("earthporn/.rss")
    //Call<Feed> getFeed();

}
