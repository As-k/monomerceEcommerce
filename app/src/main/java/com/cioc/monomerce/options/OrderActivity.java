package com.cioc.monomerce.options;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cioc.monomerce.BackendServer;
import com.cioc.monomerce.R;
import com.cioc.monomerce.entites.Cart;
import com.cioc.monomerce.entites.Order;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class OrderActivity extends AppCompatActivity {
    Context context;
    AsyncHttpClient client;
    public static ArrayList<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = OrderActivity.this;
        setContentView(R.layout.activity_order);
        client = new AsyncHttpClient();
        orderList = new ArrayList<>();

        getOrderHistory();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_arrow_back_white_24dp, getApplicationContext().getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final RecyclerView orderRecyclerView = findViewById(R.id.order_list);
        final ProgressBar orderProgressBar = findViewById(R.id.progressBar_order);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                orderProgressBar.setVisibility(View.GONE);
                orderRecyclerView.setLayoutManager(new LinearLayoutManager(OrderActivity.this));

                OrderRecyclerViewAdapter adapter = new OrderRecyclerViewAdapter(orderList);
                orderRecyclerView.setAdapter(adapter);
            }
        },2000);
    }

    public void getOrderHistory(){
        client.get(BackendServer.url+"/api/ecommerce/order/?&Name__contains=&offset=0&user=1", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
//                    JSONArray response = response1.getJSONArray("results");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        Order order = new Order(object);
                        orderList.add(order);
                    }
                } catch (JSONException e) {
                        e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(OrderActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });


    }



//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    private class OrderRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder>{
        AsyncHttpClient client;
        ArrayList<Order> orderArrayList;

        public OrderRecyclerViewAdapter(ArrayList<Order> orders) {
            this.orderArrayList = orders;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_history, parent, false);
            client = new AsyncHttpClient();
            return new ViewHolder(view);
        }

//        @Override
//        public void onViewRecycled(ViewHolder holder) {
//            if (holder.mImageView.getController() != null) {
//                holder.mImageView.getController().onDetach();
//            }
//            if (holder.mImageView.getTopLevelDrawable() != null) {
//                holder.mImageView.getTopLevelDrawable().setCallback(null);
////                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
//            }
//        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
            final Order order = orderArrayList.get(i);
            viewHolder.orderId.setText("#"+order.getPk());
            viewHolder.totalAmount.setText("Rs. "+order.getTotalAmount());
            viewHolder.approved.setText(order.getApproved());
            viewHolder.status.setText(order.getStatus());
            viewHolder.paymentMode.setText(order.getPaymentMode());

            viewHolder.cardViewOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(context, OrderDetailsActivity.class)
                            .putExtra("address", order.getStreet()+"\n"+order.getLandMark()+"\n"+order.getCity()+", "
                                    +order.getState()+", "+order.getCountry()+", "+order.getPincode()+", "+order.getCountry()+"\n"+order.getMobileNo())
                    .putExtra("pos", i)
                    .putExtra("pk", order.getPk()));
                }
            });

            viewHolder.downloadInvoices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
                }
            });

        }

        @Override
        public int getItemCount() {
            return orderArrayList.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder{
        TextView orderId, totalAmount, approved, status, paymentMode;
        ImageView orderInfo, downloadInvoices;
        CardView cardViewOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_no_result);
            totalAmount = itemView.findViewById(R.id.total_amount_result);
            approved = itemView.findViewById(R.id.approved_result);
            status = itemView.findViewById(R.id.status_result);
            paymentMode = itemView.findViewById(R.id.payment_mode_result);
            cardViewOrder = itemView.findViewById(R.id.card_view_order);
            downloadInvoices = itemView.findViewById(R.id.downloads_invoice_action);

        }
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "testthreepdf");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }

}
