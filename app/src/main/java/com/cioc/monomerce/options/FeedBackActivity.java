package com.cioc.monomerce.options;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cioc.monomerce.R;

public class FeedBackActivity extends AppCompatActivity {

    EditText email, mobile, message;
    TextInputLayout tilEmail, tilMobile, tilMessage;
    Button feedbackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tilEmail = findViewById(R.id.til_email);
        tilMobile = findViewById(R.id.til_mobile);
        tilMessage = findViewById(R.id.til_message);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        message = findViewById(R.id.message);
        feedbackBtn = findViewById(R.id.feedback_button);

        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = email.getText().toString();
                String strMobile = mobile.getText().toString();
                String strMessage = message.getText().toString();
                if (strEmail.isEmpty()){
                    tilEmail.setErrorEnabled(true);
                    tilEmail.setError("Email-id is required.");
                    email.requestFocus();
                } else {
                    tilEmail.setErrorEnabled(false);
                    if (strMobile.isEmpty()){
                        tilMobile.setErrorEnabled(true);
                        tilMobile.setError("Mobile no is required.");
                        mobile.requestFocus();
                    } else {
                        tilMobile.setErrorEnabled(false);
                    }
                }

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
