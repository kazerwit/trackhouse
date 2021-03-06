package org.trackhouse.trackhouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Used to display a web page when a user clicks on a thumbnail image of a post.
 */

public class WebViewActivity extends AppCompatActivity{

    private static final String TAG = "WebViewActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        WebView webview = (WebView) findViewById(R.id.webview);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.webview_progress_bar);

        Log.d(TAG, "onCreate: Started.");

        progressBar.setVisibility(View.VISIBLE);
        final TextView loadingText = (TextView) findViewById(R.id.webpage_loading_text);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        Log.d(TAG, "Image URL: " + url);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url);

        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                loadingText.setText("");
            }
    });

    }
}
