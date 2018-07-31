package com.cioc.monomerce.options;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cioc.monomerce.BackendServer;
import com.cioc.monomerce.R;
import com.cioc.monomerce.entites.FrequentlyQuestions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HelpCenterActivity extends AppCompatActivity {
    AsyncHttpClient client;
    ArrayList<FrequentlyQuestions> questions;
    ListView queriesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);
        client = new AsyncHttpClient();
        questions = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getFrequentlyQuestions();
        queriesList = findViewById(R.id.queries_list);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                QueriesAdapter adapter = new QueriesAdapter();
                queriesList.setAdapter(adapter);
            }
        },500);

    }

    public void getFrequentlyQuestions() {
        client.get(BackendServer.url+"/api/ecommerce/frequentlyQuestions/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        FrequentlyQuestions frequently = new FrequentlyQuestions(object);
                        questions.add(frequently);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSONObject", "Json parsing error: " + e.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private class QueriesAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return questions.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View v = getLayoutInflater().inflate(R.layout.layout_queries_list, viewGroup, false);
            TextView textQues = v.findViewById(R.id.text_ques);
            TextView textAns = v.findViewById(R.id.text_ans);

            FrequentlyQuestions frequently = questions.get(position);

            textQues.setText("Q. "+frequently.getQuestions());
            textAns.setText("A. "+frequently.getAnswer());

            return v;
        }
    }
}
