package org.trackhouse.trackhouse;

import org.trackhouse.trackhouse.model.Feed;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Create FeedAPI Interface for use with Retrofit. The base URL is set here and
 * for test purposes we use earthporn/.rss.
 */

public interface FeedAPI {

    String BASE_URL = "https://www.reddit.com/r/";

    //TODO: For now this looks at a static url extension, change this later so
    //other subreddits can be searched.
    @GET("earthporn/.rss")
    Call<Feed> getFeed();

}
