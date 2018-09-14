package com.cioc.monomerce.startup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.cookie.Cookie;

public class LoginPageActivity extends AppCompatActivity {
    private EditText mobile, mobileOtp;
    private Button loginButton, verifyBtn;
    private TextView goBack;
    private LinearLayout loginForm, otpVerifyForm;
    private TextInputLayout tilMobile, tilOTP;
    private Context mContext;
    private CookieStore httpCookieStore;
    private AsyncHttpClient client;
    private SessionManager sessionManager;
    private String csrfId, sessionId, mobileStr;
    private File file;
    String TAG = LoginPageActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        mContext = LoginPageActivity.this;
        getSupportActionBar().hide();

        init();
        sessionManager = new SessionManager(this);
        httpCookieStore = new PersistentCookieStore(this);
        httpCookieStore.clear();
        BackendServer server =  new BackendServer(this);
        client = new AsyncHttpClient(true, 80, 443);
        client.setCookieStore(httpCookieStore);
        sessionManager.clearAll();
//        if(!(sessionManager.getCsrfId() == "" && sessionManager.getSessionId() == "")){
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//        }
        isStoragePermissionGranted();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();

            }
        });
    }

    void init() {
        mobile = findViewById(R.id.mobile_no);
        tilMobile = findViewById(R.id.til_mobile);
        loginButton = findViewById(R.id.sign_in_button);
        loginForm = findViewById(R.id.login_form);
        otpVerifyForm = findViewById(R.id.otp_verify_layout);
        otpVerifyForm.setVisibility(View.GONE);
        mobileOtp = findViewById(R.id.otpEdit);
        tilOTP = findViewById(R.id.til_otp);
        verifyBtn = findViewById(R.id.verify_button);
        goBack = findViewById(R.id.go_back);
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE ,
                        Manifest.permission.READ_PHONE_STATE , Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 1; i < 7; i++) {
            if (requestCode == i){
                if (grantResults.length > 0
                        && grantResults[i-1] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[i-1] + "was " + grantResults[i-1]);
                    //resume tasks needing this permission
                }
                return;
            }
        }
    }

    public void newRegistration(View view) {
        Intent i = new Intent(getApplicationContext(), SignUpWithMobileActivity.class);
        startActivity(i);
    }

    public void signUpPage(View view){
        loginForm.setVisibility(View.VISIBLE);
        otpVerifyForm.setVisibility(View.GONE);
    }

    public void login(){
        Toast.makeText(this, BackendServer.url, Toast.LENGTH_LONG).show();
        mobileStr = mobile.getText().toString();
        if (mobileStr.isEmpty()){
            tilMobile.setErrorEnabled(true);
            tilMobile.setError("Mobile no. is required.");
            mobile.requestFocus();
        } else {
            tilMobile.setErrorEnabled(false);
            RequestParams params = new RequestParams();
            params.put("id", mobileStr);

            client.post(mContext,BackendServer.url+"/generateOTP/", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    otpVerifyForm.setVisibility(View.VISIBLE);
                    loginForm.setVisibility(View.GONE);
                    getSmsOTP();
                    verifyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loginFromOTP(mobileStr);
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(LoginPageActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void loginFromOTP(String mob){
        Toast.makeText(this, BackendServer.url, Toast.LENGTH_LONG).show();
        String mobOtp = mobileOtp.getText().toString();
        if (mobOtp.isEmpty()){
            tilOTP.setErrorEnabled(true);
            tilOTP.setError("Mobile OTP is required.");
            mobileOtp.requestFocus();
        } else {
            tilOTP.setErrorEnabled(false);
            csrfId = sessionManager.getCsrfId();
            sessionId = sessionManager.getSessionId();
            if (csrfId.equals("") && sessionId.equals("")) {
                RequestParams params = new RequestParams();
                params.put("username", mob);
                params.put("otp", mobOtp);
                client.post(BackendServer.url + "/login/?mode=api", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                        Log.e("LoginActivity", "  onSuccess");
                        super.onSuccess(statusCode, headers, c);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject c) {
                        super.onFailure(statusCode, headers, e, c);
                        if (statusCode == 401) {
                            Toast.makeText(mContext, "un success", Toast.LENGTH_SHORT).show();
                            Log.e("LoginActivity", "  onFailure");
                        }
                    }

                    @Override
                    public void onFinish() {
                        List<Cookie> lst = httpCookieStore.getCookies();
                        if (lst.isEmpty()) {
                            Toast.makeText(mContext, String.format("Error , Empty cookie store"), Toast.LENGTH_SHORT).show();
                            Log.e("LoginActivity", "Empty cookie store");
                        } else {
                            if (lst.size() < 2) {
                                String msg = String.format("Error while logining, fetal error!");
                                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
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
                                sessionManager.setUsername(mobile.getText().toString());
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
                                Log.e("isExternalStorageWritable", "" + mContext.getFilesDir().getAbsoluteFile().getPath());
                                startActivity(new Intent(mContext, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                        Intent.FLAG_ACTIVITY_NEW_TASK));
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
    }

    private void getSmsOTP() {
        IncomingSMS.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                String otp = parseCode(messageText);
                mobileOtp.setText(otp);
                loginFromOTP(mobileStr);
            }
        });
    }


    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }
}
