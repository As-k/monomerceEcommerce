package com.cioc.monomerce.options;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.R;
import com.cioc.monomerce.entites.Address;
import com.cioc.monomerce.payment.PaymentActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class NewAddressActivity extends AppCompatActivity {
    public static Context mContext;
    EditText city, street, landMark, state, pincode, country, name, mobile;
    TextView savedAdd, cityErr, streetErr, stateErr, pincodeErr, landMarkErr, countryErr, nameErr, mobileErr, saveLayoutAction, continuePayment;
    AsyncHttpClient client;
    CheckBox primaryAdd;
    RecyclerView recyclerViewAddress;
    ArrayList<Address> addressList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);
        mContext = NewAddressActivity.this;

        BackendServer backend = new BackendServer(mContext);
        client = backend.getHTTPClient();
        getSupportActionBar().hide();
        String s = getIntent().getStringExtra("newAdd");
        init();
        if (!(s==null)){
            addressList = new ArrayList<>();
            getAddress();
            continuePayment.setVisibility(View.GONE);
            recyclerViewAddress.setVisibility(View.VISIBLE);
            recyclerViewAddress.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (addressList.size()<=0) {
                        savedAdd.setVisibility(View.GONE);
                    }
                    recyclerViewAddress.setLayoutManager(new LinearLayoutManager(mContext));
                    recyclerViewAddress.setAdapter(new AddressAdapter(addressList));
                }
            }, 1000);
        }

        continuePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(true);
            }
        });

        saveLayoutAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(false);
            }
        });

    }

    public void init(){
        savedAdd = findViewById(R.id.saved_addresses);
        street = findViewById(R.id.address_street);
        city = findViewById(R.id.address_city);
        landMark = findViewById(R.id.address_landmark);
        state = findViewById(R.id.address_state);
        pincode = findViewById(R.id.address_pincode);
        country = findViewById(R.id.address_country);
        name = findViewById(R.id.address_name);
        mobile = findViewById(R.id.address_mob);
        primaryAdd = findViewById(R.id.primary_address);
        saveLayoutAction = findViewById(R.id.add_new_address);
        continuePayment = findViewById(R.id.continue_payment);
        streetErr = findViewById(R.id.streetErrTxt);
        landMarkErr = findViewById(R.id.landmarkErrTxt);
        cityErr = findViewById(R.id.cityErrTxt);
        stateErr = findViewById(R.id.stateErrTxt);
        pincodeErr = findViewById(R.id.pincodeErrTxt);
        countryErr = findViewById(R.id.countryErrTxt);
        nameErr = findViewById(R.id.nameErrTxt);
        mobileErr = findViewById(R.id.mobileErrTxt);
        recyclerViewAddress = findViewById(R.id.recycler_view_address);
        savedAdd.setVisibility(View.GONE);
        recyclerViewAddress.setVisibility(View.GONE);
    }

    public void getAddress() {
        client.get(BackendServer.url+"/api/ecommerce/address/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                for (int i = 0; i < response.length(); i++) {
                    JSONObject object = null;
                    try {
                        object = response.getJSONObject(i);
                        Address address = new Address(object);
                        addressList.add(address);
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

    public void save(final boolean res){
        String streetStr = street.getText().toString().trim();
        String landMarkStr = landMark.getText().toString().trim();
        String cityStr = city.getText().toString().trim();
        String stateStr = state.getText().toString().trim();
        String pincodeStr = pincode.getText().toString().trim();
        String countryStr = country.getText().toString().trim();
        String nameStr = name.getText().toString().trim();
        String mobStr = mobile.getText().toString().trim();

        if (cityStr.isEmpty()){
            cityErr.setVisibility(View.VISIBLE);
            cityErr.setText("Please provide the necessary details.");
        } else {
            cityErr.setVisibility(View.GONE);
        }

        if (stateStr.isEmpty()){
            streetErr.setVisibility(View.VISIBLE);
            streetErr.setText("Please provide the necessary details.");
        } else {
            streetErr.setVisibility(View.GONE);
        }

        if (streetStr.isEmpty()){
            streetErr.setVisibility(View.VISIBLE);
            streetErr.setText("Please provide the necessary details.");
        } else {
            streetErr.setVisibility(View.GONE);
        }

        if (pincodeStr.isEmpty()){
            pincodeErr.setVisibility(View.VISIBLE);
            pincodeErr.setText("Please provide the necessary details.");
        } else {
            pincodeErr.setVisibility(View.GONE);
        }
        if (countryStr.isEmpty()){
            countryErr.setVisibility(View.VISIBLE);
            countryErr.setText("Please provide the necessary details.");
        } else {
            countryErr.setVisibility(View.GONE);
        }
        if (nameStr.isEmpty()){
            nameErr.setVisibility(View.VISIBLE);
            nameErr.setText("Please provide the necessary details.");
        } else {
            nameErr.setVisibility(View.GONE);
        }
        if (mobStr.isEmpty()){
            mobileErr.setVisibility(View.VISIBLE);
            mobileErr.setText("Please provide the necessary details.");
        } else {
            mobileErr.setVisibility(View.GONE);
        }
        if (cityStr.length()==0){
            Toast.makeText(getApplicationContext(), "Please provide the necessary details.", Toast.LENGTH_SHORT).show();
            city.requestFocus();
            return;
        }
        if (streetStr.length()==0){
            Toast.makeText(getApplicationContext(), "Please provide the necessary details.", Toast.LENGTH_SHORT).show();
            street.requestFocus();
            return;
        }if (pincodeStr.length()==0){
            Toast.makeText(getApplicationContext(), "Please provide the necessary details.", Toast.LENGTH_SHORT).show();
            pincode.requestFocus();
            return;
        }if (stateStr.length()==0){
            Toast.makeText(getApplicationContext(), "Please provide the necessary details.", Toast.LENGTH_SHORT).show();
            state.requestFocus();
            return;
        }if (countryStr.length()==0){
            Toast.makeText(getApplicationContext(), "Please provide the necessary details.", Toast.LENGTH_SHORT).show();
            country.requestFocus();
            return;
        }if (nameStr.length()==0){
            Toast.makeText(getApplicationContext(), "Please provide the necessary details.", Toast.LENGTH_SHORT).show();
            name.requestFocus();
            return;
        }if (mobStr.length()==0){
            Toast.makeText(getApplicationContext(), "Please provide the necessary details.", Toast.LENGTH_SHORT).show();
            mobile.requestFocus();
            return;
        }

        RequestParams params = new RequestParams();
        params.put("title", nameStr);
        params.put("city", cityStr);
        params.put("state", stateStr);
        params.put("street", streetStr);
        params.put("pincode", pincodeStr);
        params.put("primary", primaryAdd.isChecked());
        params.put("landMark", landMarkStr);
        params.put("country", countryStr);
        params.put("mobileNo", mobStr);
        params.put("user", "1");
        params.put("lat", "");
        params.put("lon", "");

        client.post(BackendServer.url+"/api/ecommerce/address/", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String pk = response.getString("pk");
                    String title = response.getString("title");
                    String city = response.getString("city");
                    String landMark = response.getString("landMark");
                    String street = response.getString("street");
                    String pincode = response.getString("pincode");
                    String mobile = response.getString("mobileNo");
                    String state = response.getString("state");
                    boolean primary = response.getBoolean("primary");
                    String country = response.getString("country");

                    String addressStr = title+"\n"+street+"\n"+landMark+"\n"+city+", "+state+" "+pincode+"\n"+country+" "+mobile;

                    if (res){
                        startActivity(new Intent(mContext, PaymentActivity.class)
                                .putExtra("pk",pk)
                                .putExtra("totalPrice", getIntent().getStringExtra("totalPrice"))
                                .putExtra("address",addressStr));
                    } else {
                        finish();
                        Toast.makeText(getApplicationContext(), "Saved" +addressStr, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });



    }


    private class AddressAdapter extends RecyclerView.Adapter<MyViewHolder> {
        ArrayList<Address> addresses;

        public AddressAdapter(ArrayList<Address> addressList) {
            this.addresses = addressList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_address_list, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Address address = addresses.get(position);
            holder.addresstxt.setText(address.getTitle()+"\n"+address.getStreet()+"\n"+
                    address.getLandMark()+"\n"+address.getCity()+", "+address.getState()+" "+address.getPincode()+"\n"+address.getCountry()+" "+address.getMobile());
        }

        @Override
        public int getItemCount() {
            return addresses.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView addresstxt;
        LinearLayout deliveryAction;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            addresstxt =  itemView.findViewById(R.id.address_text);
            deliveryAction =  itemView.findViewById(R.id.delivery_action);
            deliveryAction.setVisibility(View.GONE);
        }
    }
}
