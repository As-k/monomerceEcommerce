package com.cioc.monomerce.startup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cioc.monomerce.R;
import com.cioc.monomerce.backend.BackendServer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class RegistrationActivity extends AppCompatActivity {
    EditText firstName, lastName, emailId, mobileNo, password, confPassword, mobileNoform2;
    Button registrationBtn1, registrationBtn2;
    LinearLayout form1, form2;
    AsyncHttpClient client;
    private Pattern pattern;
    private Matcher matcher;
    public static JSONObject object;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    String password_pattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,14}$";
    public static String fstname, lstname, emailStr, mobStr, passStr, confirmPassStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        client = new AsyncHttpClient();
        init();
        pattern = Pattern.compile(EMAIL_PATTERN);
        registrationBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

    }

    void init() {
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.lst_name);
        emailId = findViewById(R.id.email_id);
        mobileNo = findViewById(R.id.mobile_no);
        password = findViewById(R.id.password_txt);
        confPassword = findViewById(R.id.confirm_password);
        registrationBtn1 = findViewById(R.id.registration_button);
        form1 = findViewById(R.id.full_form1);
        form2 = findViewById(R.id.full_form2);
        registrationBtn2 = findViewById(R.id.registration_button2);
        mobileNoform2 = findViewById(R.id.mobile_no_txt);
    }

    public void submit() {
        fstname = firstName.getText().toString().trim();
        lstname = lastName.getText().toString().trim();
        emailStr = emailId.getText().toString().trim();
        mobStr = mobileNo.getText().toString().trim();
        passStr = password.getText().toString().trim();
        confirmPassStr = confPassword.getText().toString().trim();

        if (fstname.isEmpty()){
            firstName.setError("First name is required.");
            firstName.requestFocus();
        } else {
            if (lstname.isEmpty()){
                lastName.setError("Last name is required.");
                lastName.requestFocus();
            } else {
                if (emailStr.isEmpty()){
                    emailId.setError("Email is required.");
                    emailId.requestFocus();
                } else {
                    if (validateEmail(emailStr)) {
                        if (mobStr.isEmpty()) {
                            mobileNo.setError("Mobile number is required.");
                            mobileNo.requestFocus();
                        } else {
                            if (passStr.isEmpty()) {
                                password.setError("Password is required (minimum length is 6)");
                                password.requestFocus();
                            } else {
                                if (passStr.length() >= 6) {
                                    if (confirmPassStr.isEmpty()) {
                                        confPassword.setError("Confirm password does not match");
                                        confPassword.requestFocus();
                                    } else {
                                        if (confirmPassStr.equals(passStr)) {
                                            RequestParams params = new RequestParams();
                                            params.put("email", emailStr);
                                            params.put("mobile", mobStr);
                                            client.post(BackendServer.url + "/api/homepage/registration/", params, new JsonHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                    super.onSuccess(statusCode, headers, response);
                                                    object = response;
                                                    try {
                                                        String pk = response.getString("pk");
                                                        String created = response.getString("created");
                                                        String token = response.getString("token");
                                                        String mobileOTP = response.getString("mobileOTP");
                                                        String emailOTP = response.getString("emailOTP");
                                                        String email = response.getString("email");
                                                        String mobile = response.getString("mobile");
//
                                                        Toast.makeText(RegistrationActivity.this, "success", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(RegistrationActivity.this, GetOTPActivity.class)
                                                        .putExtra("pk", pk)
                                                        .putExtra("created", created)
                                                        .putExtra("token", token)
                                                        .putExtra("mobileOTP", mobileOTP)
                                                        .putExtra("emailOTP", emailOTP)
                                                        .putExtra("email", email)
                                                        .putExtra("mobile", mobile)
                                                        );
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }


                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                    super.onFailure(statusCode, headers, throwable, errorResponse);

                                                    Toast.makeText(RegistrationActivity.this, "failure", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            confPassword.setError("Confirm password does not match");
                                            confPassword.requestFocus();
                                        }
                                    }
                                } else {
                                    password.setError("Password is required (minimum length is 6)");
                                    password.requestFocus();
                                }
                            }
                        }
                    } else {
                        emailId.setError("Invalid Email-Id");
                        emailId.requestFocus();
                    }
                }
            }
        }
    }

    public boolean validateEmail(final String hex) {
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }

}
