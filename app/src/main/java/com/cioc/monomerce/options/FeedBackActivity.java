package com.cioc.monomerce.options;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.cioc.monomerce.R;

public class FeedBackActivity extends AppCompatActivity {

    EditText email, mobile, message;
    Button feedbackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        message = findViewById(R.id.message);
        feedbackBtn = findViewById(R.id.feedback_button);

    }
}
