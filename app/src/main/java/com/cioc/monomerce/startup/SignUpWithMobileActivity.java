package com.cioc.monomerce.startup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cioc.monomerce.R;
import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.backend.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.cookie.Cookie;

public class SignUpWithMobileActivity extends AppCompatActivity {
    private EditText mobileNo, mobileOtp;
    private TextView goBack, termCondition;
    private TextInputLayout tilMobile, tilOTP;
    private CheckBox termConCB;
    private Button getOTPBtn, verifyBtn;
    private LinearLayout signUpForm, otpVerifyForm;
    private AsyncHttpClient client;
    private Context mContext;
    private CookieStore httpCookieStore;
    private SessionManager sessionManager;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_mobile);
        getSupportActionBar().hide();
        mContext = SignUpWithMobileActivity.this;
        init();
        sessionManager = new SessionManager(this);
        client = new AsyncHttpClient();
        getOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOTP();
            }
        });

        ClickableSpan termsAndConditions = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BackendServer.url+"/terms")));
                view.invalidate(); // need put invalidate here to make text change to GREEN after clicked
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
                if (termCondition.isPressed() && termCondition.getSelectionStart() != -1 && termCondition.getText()
                        .toString()
                        .substring(termCondition.getSelectionStart(), termCondition.getSelectionEnd())
                        .equals("Terms and Conditions")) {
                    termCondition.invalidate();
                    ds.setColor(Color.rgb(200, 50, 50)); // need put invalidate here to make text change to RED when pressed on Highlight Link
                } else {
                    ds.setColor(Color.rgb(7, 125, 133));
                }
                // dont put invalidate here because if you put invalidate here `updateDrawState` will called forever
            }
        };

        makeLinks(termCondition, new String[] {
                "Terms and Conditions"
        }, new ClickableSpan[] {
                termsAndConditions
        });
    }

    void init() {
        mobileNo = findViewById(R.id.mobile_no);
        tilMobile = findViewById(R.id.til_mobile_no);
        getOTPBtn = findViewById(R.id.get_otp_button);
        signUpForm = findViewById(R.id.sign_up_layout);
        otpVerifyForm = findViewById(R.id.otp_verify_layout);
        otpVerifyForm.setVisibility(View.GONE);
        mobileOtp = findViewById(R.id.otpEdit);
        tilOTP = findViewById(R.id.til_otp);
        verifyBtn = findViewById(R.id.verify_button);
        goBack = findViewById(R.id.go_back);
        termConCB = findViewById(R.id.terms_check);
        termCondition = findViewById(R.id.terms_text);
    }

    public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink,
                    startIndexOfLink + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setHighlightColor(
                Color.TRANSPARENT); // prevent TextView change background when highlight
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    public void getOTP(){
        Toast toast = null;
        String mobile = mobileNo.getText().toString().trim();
        if (mobile.isEmpty()){
            tilMobile.setErrorEnabled(true);
            tilMobile.setError("Mobile no. is required.");
            mobileNo.requestFocus();
        } else {
            tilMobile.setErrorEnabled(false);
            if (!termConCB.isChecked()) {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(mContext,"Please agree the term and condition.", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                RequestParams params = new RequestParams();
                params.put("mobile", mobile);
                client.post(BackendServer.url + "/api/homepage/registration/", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                            String pk = response.getString("pk");
                            String created = response.getString("created");
                            String token = response.getString("token");
                            String mobileOTP = response.getString("mobileOTP");
                            String emailOTP = response.getString("emailOTP");
                            String email = response.getString("email");
                            String mobile = response.getString("mobile");
                            signUpForm.setVisibility(View.GONE);
                            otpVerifyForm.setVisibility(View.VISIBLE);
                            verifyBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    verify(pk, token, mobile);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);

                        Toast.makeText(mContext, "failure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void verify(String pk, String token, String mobile){
        httpCookieStore = new PersistentCookieStore(this);
        httpCookieStore.clear();
        client = new AsyncHttpClient();
        client.setCookieStore(httpCookieStore);

        String mobOtp = mobileOtp.getText().toString().trim();
        if (mobOtp.isEmpty()){
            tilOTP.setErrorEnabled(true);
            tilOTP.setError("Mobile OTP is required.");
            mobileOtp.requestFocus();
        } else {
            tilOTP.setErrorEnabled(false);
            RequestParams params = new RequestParams();
            params.put("mobile", mobile);
            params.put("token", token);
            params.put("agree", true);
            params.put("mobileOTP", mobOtp);
            client.patch(BackendServer.url + "/api/homepage/registration/" + pk + "/", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Toast.makeText(mContext, "submitted", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);

                    Toast.makeText(mContext, "failure", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    List<Cookie> lst = httpCookieStore.getCookies();
                    if (lst.isEmpty()) {
                        Toast.makeText(mContext, String.format("Error , Empty cookie store"), Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity", "Empty cookie store");
                    } else {
                        if (lst.size() < 2) {
                            String msg = String.format("Error while logining, fetal error!");
                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                            Log.e("LoginActivity", "" + msg);
                            return;
                        }
                        Cookie csrfCookie = lst.get(0);
                        Cookie sessionCookie = lst.get(1);
                        String csrf_token = csrfCookie.getValue();
                        String session_id = sessionCookie.getValue();
                        File dir = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name1));
                        Log.e("MyAccountActivity", "" + Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name1));
                        if (dir.exists())
                            if (dir.isDirectory()) {
                                String[] children = dir.list();
                                for (int i = 0; i < children.length; i++) {
                                    new File(dir, children[i]).delete();
                                }
                                dir.delete();
                            }
                        file = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name1));
                        if (file.mkdir()) {
                            sessionManager.setCsrfId(csrf_token);
                            sessionManager.setSessionId(session_id);
                            Toast.makeText(mContext, "Dir created", Toast.LENGTH_SHORT).show();
                            String fileContents = "csrf_token " + sessionManager.getCsrfId() + " session_id " + sessionManager.getSessionId();
                            FileOutputStream outputStream;
                            try {
                                String path = file.getAbsolutePath() + "/libre.txt";
                                outputStream = new FileOutputStream(path);
                                outputStream.write(fileContents.getBytes());
                                outputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            startActivity(new Intent(mContext, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(mContext, "Dir not created", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Log.e("LoginActivity", "  finished");
                }
            });
        }
    }

    public void signUpPage(View view){
        signUpForm.setVisibility(View.VISIBLE);
        otpVerifyForm.setVisibility(View.GONE);
    }


}
