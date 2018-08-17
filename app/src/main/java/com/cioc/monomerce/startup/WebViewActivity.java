package com.cioc.monomerce.startup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.cioc.monomerce.R;
import com.cioc.monomerce.backend.BackendServer;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        String pageurl = getIntent().getStringExtra("web");
        String title = getIntent().getStringExtra("title");
        setTitle(title);

        WebView offerWebView = findViewById(R.id.offer_web_view);

        offerWebView.loadUrl(BackendServer.url+"/"+pageurl);
        offerWebView.getSettings().setJavaScriptEnabled(true);

    }
}
