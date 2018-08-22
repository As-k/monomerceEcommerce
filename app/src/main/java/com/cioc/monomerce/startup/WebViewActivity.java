package com.cioc.monomerce.startup;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.webkit.WebView;
import android.widget.TextView;

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
        setTitleColor(getResources().getColor(R.color.text_color));

        TextView offerWebView = findViewById(R.id.offer_web_view);
        Spanned htmlAsSpanned = Html.fromHtml(pageurl);
        if (htmlAsSpanned.toString().equals("null")|| htmlAsSpanned.toString().equals("")|| htmlAsSpanned.toString() == null) {
            offerWebView.setText("");
        } else {
            offerWebView.setText("" +htmlAsSpanned);
        }

//        offerWebView.loadUrl(BackendServer.url+"/"+pageurl);
//        offerWebView.getSettings().setJavaScriptEnabled(true);

    }
}
