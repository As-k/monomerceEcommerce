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
import android.widget.TextView;
import android.widget.Toast;

import com.cioc.monomerce.R;
import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.backend.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
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

public class GetOTPActivity extends AppCompatActivity {
    private EditText mobOTP, emailidOTP;
    Context context;
    TextView termCon;
    CheckBox termConCB;
    Button submitBtn;
    AsyncHttpClient client;
    String pk, created, token, mobileOTP, emailOTP, email, mobile;
    String[] email1;
    private CookieStore httpCookieStore;
    SessionManager sessionManager;
    String csrfId, sessionId;
    boolean isGettingIntent = true;

    public static File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_otp);
        this.context = GetOTPActivity.this;
        sessionManager = new SessionManager(this);

        httpCookieStore = new PersistentCookieStore(this);
        httpCookieStore.clear();
        client = new AsyncHttpClient();
        client.setCookieStore(httpCookieStore);

        Bundle b = getIntent().getExtras();
        if (b!=null) {
            JSONObject response = RegistrationActivity.object;
            try {
                pk = response.getString("pk");
                created = response.getString("created");
                token = response.getString("token");
                mobileOTP = response.getString("mobileOTP");
                emailOTP = response.getString("emailOTP");
                email = response.getString("email");
                mobile = response.getString("mobile");
                email1 = email.split("@");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mobOTP = findViewById(R.id.mobile_otp);
        emailidOTP = findViewById(R.id.email_otp);
        termConCB = findViewById(R.id.terms_check);
        termCon = findViewById(R.id.terms_text);
        submitBtn = findViewById(R.id.submit_button);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
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
                if (termCon.isPressed() && termCon.getSelectionStart() != -1 && termCon.getText()
                        .toString()
                        .substring(termCon.getSelectionStart(), termCon.getSelectionEnd())
                        .equals("Terms and Conditions")) {
                    termCon.invalidate();
                    ds.setColor(Color.rgb(200,50,50)); // need put invalidate here to make text change to RED when pressed on Highlight Link
                } else {
                    ds.setColor(getResources().getColor(R.color.orange_light));
                }
                // dont put invalidate here because if you put invalidate here `updateDrawState` will called forever
            }
        };

        makeLinks(termCon, new String[] {
                "Terms and Conditions"
        }, new ClickableSpan[] {
                termsAndConditions
        });

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


    void submit() {
        String mobOtpStr = mobOTP.getText().toString().trim();
        String emailOtpStr = emailidOTP.getText().toString().trim();
        if (mobOtpStr.isEmpty()){
            mobOTP.setError("Invalid mobile otp.");
            mobOTP.requestFocus();
        } else if (emailOtpStr.isEmpty()){
            emailidOTP.setError("Invalid email otp.");
            emailidOTP.requestFocus();
        } else if (!termConCB.isChecked()) {
            Toast.makeText(this, "Please agree the term and condition.", Toast.LENGTH_SHORT).show();
        } else {
            RequestParams params = new RequestParams();
            params.put("username", email1[0]);
            params.put("firstName", RegistrationActivity.fstname);
            params.put("lastName", RegistrationActivity.lstname);
            params.put("mobile", RegistrationActivity.mobStr);
            params.put("email", RegistrationActivity.emailStr);
            params.put("password", RegistrationActivity.passStr);
            params.put("rePassword", RegistrationActivity.confirmPassStr);
            params.put("token", token);
            params.put("reg", pk);
            params.put("agree", true);
            params.put("emailOTP", emailOtpStr);
            params.put("mobileOTP", mobOtpStr);
            client.patch(BackendServer.url + "/api/homepage/registration/"+pk+"/", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(GetOTPActivity.this, "submitted", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(GetOTPActivity.this, "failure", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    List<Cookie> lst = httpCookieStore.getCookies();
                    if (lst.isEmpty()) {
                        Toast.makeText(context, String.format("Error , Empty cookie store"), Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity", "Empty cookie store");
                    } else {
                        if (lst.size() < 2) {
                            String msg = String.format("Error while logining, fetal error!");
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            Log.e("LoginActivity", ""+msg);
                            return;
                        }

                        Cookie csrfCookie = lst.get(0);
                        Cookie sessionCookie = lst.get(1);

                        String csrf_token = csrfCookie.getValue();
                        String session_id = sessionCookie.getValue();

                        File dir = new File(Environment.getExternalStorageDirectory() + "/"+getString(R.string.app_name1));
                        Log.e("MyAccountActivity", "" + Environment.getExternalStorageDirectory() + "/"+getString(R.string.app_name1));
                        if (dir.exists())
                            if (dir.isDirectory()) {
                                String[] children = dir.list();
                                for (int i = 0; i < children.length; i++) {
                                    new File(dir, children[i]).delete();
                                }
                                dir.delete();
                            }
                        file = new File(Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name1));
                        Log.e("directory",""+file.getAbsolutePath());
                        if (file.mkdir()) {
                            sessionManager.setCsrfId(csrf_token);
                            sessionManager.setSessionId(session_id);
                            sessionManager.setUsername(email1[0]);
                            Toast.makeText(context, "Dir created", Toast.LENGTH_SHORT).show();
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
                            Log.e("isExternalStorageWritable", "" + context.getFilesDir().getAbsoluteFile().getPath());

                            if (!isGettingIntent) {
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                            } else
                                startActivity(new Intent(context, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(context, "Dir not created", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Log.e("LoginActivity", "  finished");
                }
            });
        }
    }

}
