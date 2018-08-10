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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.R;
import com.cioc.monomerce.entites.Address;
import com.cioc.monomerce.payment.PaymentActivity;
import com.githang.stepview.StepView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CheckOutActivity extends AppCompatActivity {
    private static Context mContext;
    TextView textAmount, newAddressBtn;
    RecyclerView recyclerView;
    AsyncHttpClient client;
    public static ArrayList<Address> addresses;
    public static int pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        mContext = CheckOutActivity.this;
        BackendServer backend = new BackendServer(this);
        client = backend.getHTTPClient();
        addresses = new ArrayList<>();
        getAddress();

        StepView mStepView = (StepView) findViewById(R.id.step_view);
        List<String> steps = Arrays.asList(new String[]{"Selected Items", "Shipping Address", "Review Your Order"});
        mStepView.setSteps(steps);
        mStepView.selectedStep(2);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
                textAmount.setText("\u20B9"+getIntent().getExtras().getInt("totalPrice"));
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerView.setAdapter(new CheckOutActivity.AddressRecyclerViewAdapter(addresses));
                newAddressBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(mContext, NewAddressActivity.class)
                        .putExtra("totalPrice", textAmount.getText().toString()));
                    }
                });
            }
        },1000);
    }

    public void init(){
        newAddressBtn = findViewById(R.id.text_action_continue);
        textAmount = findViewById(R.id.text_action_amount);
        recyclerView = findViewById(R.id.recyclerview_address);
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
                        addresses.add(address);
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


    public class AddressRecyclerViewAdapter extends RecyclerView.Adapter<CheckOutActivity.AddressRecyclerViewAdapter.ViewHolder> {

        private ArrayList<Address> mAddresslist;
//        private RecyclerView mRecyclerView;

//        String[] add = {"4th Floor, Venkateshwara Heritage, Kudlu Hosa Road, Opp Sai Purna Premium Apartment, Sai Meadows, Kudlu, Bengaluru, Karnataka 560068",
//                "HYVA Primus, #45/155, 5th Main, Road Next to IBM Bannerghatta Main, Bengaluru, Karnataka 560029"};



        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            TextView addresstxt;
            LinearLayout deliveryAction;
            ImageView delAdd;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                addresstxt =  view.findViewById(R.id.address_text);
                deliveryAction =  view.findViewById(R.id.delivery_action);
                delAdd =  itemView.findViewById(R.id.delete_address);
                delAdd.setVisibility(View.GONE);
            }
        }

        public AddressRecyclerViewAdapter(ArrayList<Address> addresses) {
            mAddresslist = addresses;
        }

        @NonNull
        @Override
        public CheckOutActivity.AddressRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_address_list, parent, false);
            return new CheckOutActivity.AddressRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

            Address address = mAddresslist.get(position);
            holder.addresstxt.setText(address.getTitle()+"\n"+address.getStreet()+"\n"+address.getLandMark()+"\n"+address.getCity()+", "+address.getState()+" "+address.getPincode()+"\n"+address.getCountry());
//            holder.addresstxt.setText(address);
//            holder.addresstxt.setText(add[position]);
            holder.deliveryAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos=position;
                    startActivity(new Intent(mContext, PaymentActivity.class)
                            .putExtra("address", holder.addresstxt.getText().toString())
                            .putExtra("pk", address.getPk())
                            .putExtra("totalPrice", textAmount.getText().toString()));

                }
            });

        }

        @Override
        public void onViewRecycled(CheckOutActivity.AddressRecyclerViewAdapter.ViewHolder holder) {

        }

        @Override
        public int getItemCount() {
            return mAddresslist.size();
        }
    }
}
