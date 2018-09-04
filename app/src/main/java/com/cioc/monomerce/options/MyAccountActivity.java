package com.cioc.monomerce.options;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cioc.monomerce.R;
import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.backend.BackgroundService;
import com.cioc.monomerce.backend.SessionManager;
import com.cioc.monomerce.startup.LoginActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import cz.msebera.android.httpclient.Header;

public class MyAccountActivity extends AppCompatActivity {
    Context context;
    CardView cartView, orderView, settingView, supportView, wishListView;
    Button logoutBtn;
    TextView profileName, emailId, mobileNo;
    ImageView profileImage;
    SessionManager sessionManager;
    BackendServer backend;
    AsyncHttpClient client;
    static int pk;
    File file1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        this.context = MyAccountActivity.this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionManager = new SessionManager(context);
        backend = new BackendServer(context);
        client = backend.getHTTPClient();
        init();

        client.get(BackendServer.url + "/api/HR/users/?mode=mySelf&format=json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("MainActivity","onSuccess");
                 try {
                    JSONObject usrObj = response.getJSONObject(0);
                    pk = usrObj.getInt("pk");
                    String username = usrObj.getString("username");
                    String firstName = usrObj.getString("first_name");
                    String lastName = usrObj.getString("last_name");
                    String email = usrObj.getString("email");
                    JSONObject profileObj = usrObj.getJSONObject("profile");
                    String mobile = profileObj.getString("mobile");
                    profileName.setText(firstName+" "+lastName);

                    String dpLink = profileObj.getString("displayPicture");
                    if (dpLink==null||dpLink.equals("null")){
                        dpLink = BackendServer.url+"/static/images/userIcon.png";
                    }

//                    emailId.setText(email);
//                    if (!mobile.equals("null"))
//                        mobileNo.setText(mobile);

                    String[] image = dpLink.split("/"); //Backend.serverUrl+"/media/HR/images/DP/"
                    String dp;
                    if (dpLink.equals(BackendServer.url+"/static/images/userIcon.png")){
                        dp = image[5];
                    } else {
                        dp = image[7];
                        Log.e("image "+dpLink,""+dp);
                    }


                    client.get(dpLink, new FileAsyncHttpResponseHandler(context) {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File file) {
                            // Do something with the file `response`

                            FileOutputStream outputStream;
                            try {
                                file1 = new File(Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name1)+ "/" + dp);
                                if (file1.exists())
                                    file1.delete();
                                outputStream = new FileOutputStream(file1);
                                outputStream.write(dp.getBytes());
                                outputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.e("image",""+file1.getAbsolutePath());
                            Bitmap pp = BitmapFactory.decodeFile(file.getAbsolutePath());
                            profileImage.setImageBitmap(pp);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers,Throwable e, File file) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            Log.e("failure-image",""+file.getAbsolutePath());
                            System.out.println("failure");
                            System.out.println(statusCode);
                        }
                    });
                } catch (JSONException e){
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("MainActivity","onFailure");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.e("MainActivity","onFinish");
            }
        });


        cartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAccountActivity.this, CartListActivity.class));
            }
        });
        orderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAccountActivity.this, OrderActivity.class));
            }
        });
        settingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAccountActivity.this, NewAddressActivity.class)
                .putExtra("newAdd", "set" ));
            }
        });
        supportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAccountActivity.this, HelpCenterActivity.class));
            }
        });
        wishListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAccountActivity.this, WishlistActivity.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Logout ?")
                        .setMessage("Are you sure you want to logout ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sessionManager.clearAll();
                                File dir = new File(Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name));
                                Log.e("MyAccountActivity",""+Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name));
                                if (dir.exists())
                                    if (dir.isDirectory()) {
                                        String[] children = dir.list();
                                        for (int i = 0; i < children.length; i++) {
                                            new File(dir, children[i]).delete();
                                        }
                                        dir.delete();
                                    }
                                stopService(new Intent(context, BackgroundService.class));
                                startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                        Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
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

    public void init() {
        cartView = findViewById(R.id.card_view_cart);
        orderView = findViewById(R.id.card_view_order);
        settingView = findViewById(R.id.card_view_setting);
        supportView = findViewById(R.id.card_view_support);
        wishListView = findViewById(R.id.card_view_wishlist);
        profileName = findViewById(R.id.profile_name);
        profileImage = findViewById(R.id.profile_image);
        logoutBtn = findViewById(R.id.logout_button);
    }


}
