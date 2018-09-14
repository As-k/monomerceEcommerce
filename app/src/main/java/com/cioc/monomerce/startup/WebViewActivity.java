package com.cioc.monomerce.startup;


import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cioc.monomerce.R;
import com.cioc.monomerce.backend.BackendServer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class WebViewActivity extends AppCompatActivity {
    private boolean term;
    private String body, title;
    private AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        BackendServer server = new BackendServer(this);
        client = server.getHTTPClient();
        TextView offerWebView = findViewById(R.id.offer_web_view);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        offerWebView.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle b = getIntent().getExtras();
        if (b!=null) {
            if (b.getBoolean("term")) {
                body = b.getString("body");
                title = b.getString("title");
            }else {
                getTerm();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    offerWebView.setVisibility(View.VISIBLE);
                    setTitle(title);
                    setTitleColor(getResources().getColor(R.color.text_color));
                    Spanned htmlAsSpanned = Html.fromHtml(body);
                    if (htmlAsSpanned.toString().equals("null") || htmlAsSpanned.toString().equals("") || htmlAsSpanned.toString() == null) {
                        offerWebView.setText("");
                    } else {
                        offerWebView.setText("" + htmlAsSpanned);
                    }
                }
            }, 2000);
        }

    }

    private void getTerm() {
        client.get(BackendServer.url+"/api/ecommerce/pages/?pageurl__icontains=terms-&-condition", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                for (int i=0; i<response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        body = object.getString("body");
                        title = object.getString("title");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
