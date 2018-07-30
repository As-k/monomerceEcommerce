package com.cioc.monomerce.options;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cioc.monomerce.R;
import com.cioc.monomerce.entites.Address;
import com.cioc.monomerce.payment.PaymentActivity;
import com.githang.stepview.StepView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckOutActivity extends AppCompatActivity {
    private static Context mContext;
    TextView textAmount, newAddressBtn;
    RecyclerView recyclerView;
    ArrayList<String> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        mContext = CheckOutActivity.this;

        Address newAddress = new Address();
        addresses = newAddress.getAddressList();
        init();
        StepView mStepView = (StepView) findViewById(R.id.step_view);
        List<String> steps = Arrays.asList(new String[]{"Selected Items", "Shipping Address", "Review Your Order"});
        mStepView.setSteps(steps);
        mStepView.selectedStep(2);

        textAmount.setText("Rs. "+getIntent().getExtras().getInt("totalPrice"));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(new CheckOutActivity.AddressRecyclerViewAdapter(recyclerView, addresses));
        newAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, NewAddressActivity.class));
            }
        });


    }

    public void init(){
        newAddressBtn = findViewById(R.id.text_action_continue);
        textAmount = findViewById(R.id.text_action_amount);
        recyclerView = findViewById(R.id.recyclerview_address);
    }


    public class AddressRecyclerViewAdapter extends RecyclerView.Adapter<CheckOutActivity.AddressRecyclerViewAdapter.ViewHolder> {

        private ArrayList<String> mAddresslist;
        private RecyclerView mRecyclerView;

        String[] add = {"4th Floor, Venkateshwara Heritage, Kudlu Hosa Road, Opp Sai Purna Premium Apartment, Sai Meadows, Kudlu, Bengaluru, Karnataka 560068",
                "HYVA Primus, #45/155, 5th Main, Road Next to IBM Bannerghatta Main, Bengaluru, Karnataka 560029"};



        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            TextView addresstxt;
            LinearLayout deliveryAction;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                addresstxt =  view.findViewById(R.id.address_text);
                deliveryAction =  view.findViewById(R.id.delivery_action);
            }
        }

        public AddressRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<String> addresses) {
            mRecyclerView = recyclerView;
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
//            String address = (String)mAddresslist.get(position);
////            holder.addresstxt.setText(address.getBuyerName()+"\n"+address.getStreet()+"\n"+address.getCity()+", "+address.getState()+" "+address.getPincode()+"\n"+address.getMobile());
//            holder.addresstxt.setText(address);
            holder.addresstxt.setText(add[position]);
            holder.deliveryAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(mContext, PaymentActivity.class)
                            .putExtra("address", holder.addresstxt.getText().toString())
                            .putExtra("totalPrice", textAmount.getText().toString()));

                }
            });

        }

        @Override
        public void onViewRecycled(CheckOutActivity.AddressRecyclerViewAdapter.ViewHolder holder) {

        }

        @Override
        public int getItemCount() {
            return add.length;
        }
    }
}
