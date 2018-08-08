package com.cioc.monomerce.startup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
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

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.cookie.Cookie;

public class LoginActivity extends AppCompatActivity {
    AutoCompleteTextView username, password;//, otpEdit;
    Button loginButton;//, getOTP;
    LinearLayout llUsername, llPassword;//, llotpEdit;
//    TextView forgot, goBack;
    BackendServer backend = new BackendServer(this);
    Context context;
    private CookieStore httpCookieStore;
    private AsyncHttpClient client;
    SessionManager sessionManager;
//    public static boolean res;
    String csrfId, sessionId;

    public static File file;
    String TAG = "status";
    boolean isGettingIntent = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this.getApplicationContext();
        getSupportActionBar().hide();

        Bundle b = getIntent().getExtras();
        if (b!=null){
            isGettingIntent = b.getBoolean("boolean");
        }

        sessionManager = new SessionManager(this);

        httpCookieStore = new PersistentCookieStore(this);
        httpCookieStore.clear();
        client = new AsyncHttpClient();
        client.setCookieStore(httpCookieStore);

        isStoragePermissionGranted();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
//        otpEdit = findViewById(R.id.otpEdit);

//        forgot= findViewById(R.id.forgot_password);
//        goBack= findViewById(R.id.go_back);
//        goBack.setVisibility(View.GONE);

        llUsername = findViewById(R.id.llUsername);
        llPassword = findViewById(R.id.llPassword);
//        llotpEdit = findViewById(R.id.llOtp);
//        llotpEdit.setVisibility(View.GONE);

        loginButton = findViewById(R.id.sign_in_button);
//        getOTP = findViewById(R.id.get_otp);
//        getOTP.setVisibility(View.GONE);

        if(!(sessionManager.getCsrfId() == "" && sessionManager.getCsrfId() == "")){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE , Manifest.permission.READ_PHONE_STATE , Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.SEND_SMS}, 1);
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
        for (int i = 1; i < 6; i++) {
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

    public void register(View view) {
        Intent i = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(i);
    }

//    public void forgotPassword(View v){
//        llPassword.setVisibility(View.GONE);
//        loginButton.setVisibility(View.GONE);
//        llotpEdit.setVisibility(View.GONE);
//        forgot.setVisibility(View.GONE);
//        getOTP.setVisibility(View.VISIBLE);
//        goBack.setVisibility(View.VISIBLE);
//
//        goBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                llPassword.setVisibility(View.VISIBLE);
//                llUsername.setVisibility(View.VISIBLE);
//                loginButton.setVisibility(View.VISIBLE);
//                llotpEdit.setVisibility(View.GONE);
//                forgot.setVisibility(View.VISIBLE);
//                getOTP.setVisibility(View.GONE);
//                goBack.setVisibility(View.GONE);
//
//            }
//        });
//
//        getOTP.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                llPassword.setVisibility(View.GONE);
//                llUsername.setVisibility(View.GONE);
//                loginButton.setVisibility(View.GONE);
//                forgot.setVisibility(View.GONE);
//                llotpEdit.setVisibility(View.VISIBLE);
//                getOTP.setVisibility(View.VISIBLE);
//                goBack.setVisibility(View.VISIBLE);
//            }
//        });
//
//    }

    public void login(){
        Toast.makeText(this, BackendServer.url, Toast.LENGTH_LONG).show();
        String userName = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        if (userName.isEmpty()){
            username.setError("Empty");
            username.requestFocus();
        } else {
            if (pass.isEmpty()){
                password.setError("Empty");
                password.requestFocus();
            } else {
                csrfId = sessionManager.getCsrfId();
                sessionId = sessionManager.getSessionId();
                if (csrfId.equals("") && sessionId.equals("")) {
                    RequestParams params = new RequestParams();
                    params.put("username", userName);
                    params.put("password", pass);
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
                                Toast.makeText(LoginActivity.this, "un success", Toast.LENGTH_SHORT).show();
                                Log.e("LoginActivity", "  onFailure");
                            }
                        }

                        @Override
                        public void onFinish() {
                            List<Cookie> lst = httpCookieStore.getCookies();
                            if (lst.isEmpty()) {
                                Toast.makeText(LoginActivity.this, String.format("Error , Empty cookie store"), Toast.LENGTH_SHORT).show();
                                Log.e("LoginActivity", "Empty cookie store");
                            } else {
                                if (lst.size() < 2) {
                                    String msg = String.format("Error while logining, fetal error!");
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    Log.e("LoginActivity", ""+msg);
                                    return;
                                }

                                Cookie csrfCookie = lst.get(0);
                                Cookie sessionCookie = lst.get(1);

                                String csrf_token = csrfCookie.getValue();
                                String session_id = sessionCookie.getValue();

                                file = new File(Environment.getExternalStorageDirectory()+"/Monomerce");
                                Log.e("directory",""+file.getAbsolutePath());
                                if (file.mkdir()) {
                                    sessionManager.setCsrfId(csrf_token);
                                    sessionManager.setSessionId(session_id);
                                    sessionManager.setUsername(userName);
                                    Toast.makeText(LoginActivity.this, "Dir created", Toast.LENGTH_SHORT).show();
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
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Dir not created", Toast.LENGTH_SHORT).show();
                                }
                            }
                            Log.e("LoginActivity", "  finished");
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent("com.cioc.libreerp.backendservice");
        sendBroadcast(intent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }
}
