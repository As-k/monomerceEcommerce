package com.cioc.monomerce.options;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.cioc.monomerce.R;

public class MyAccountActivity extends AppCompatActivity {
    CardView cartView, orderView, settingView, supportView, wishListView;
    Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        init();

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

    }

    public void init() {
        cartView = findViewById(R.id.card_view_cart);
        orderView = findViewById(R.id.card_view_order);
        settingView = findViewById(R.id.card_view_setting);
        supportView = findViewById(R.id.card_view_support);
        wishListView = findViewById(R.id.card_view_wishlist);
        logoutBtn = findViewById(R.id.logout_button);

    }


//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.card_view_cart :{
//                startActivity(new Intent(MyAccountActivity.this, CartListActivity.class));
//            }
//            case R.id.card_view_order :{
//                startActivity(new Intent(MyAccountActivity.this, OrderActivity.class));
//            }
//            case R.id.card_view_setting :{
//                startActivity(new Intent(MyAccountActivity.this, HelpCenterActivity.class));
//            }
//            case R.id.card_view_support :{
//                startActivity(new Intent(MyAccountActivity.this, FeedBackActivity.class));
//            }
//            case R.id.card_view_wishlist :{
//                startActivity(new Intent(MyAccountActivity.this, WishlistActivity.class));
//            }
//            default:
//                Toast.makeText(this, "Click on view", Toast.LENGTH_SHORT).show();
//        }
//    }
}
