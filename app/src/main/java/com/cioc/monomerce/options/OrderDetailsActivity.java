package com.cioc.monomerce.options;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.R;
import com.cioc.monomerce.entites.Order;
import com.cioc.monomerce.entites.OrderQtyMap;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class OrderDetailsActivity extends AppCompatActivity {
    TextView orderShipping, linearLayoutReturn, linearLayoutCancel;
    RecyclerView recyclerViewOrder;
    Context mContext;
    AsyncHttpClient client;
    ArrayList<Order> orders;
    ArrayList<OrderQtyMap> qtyMaps;
    boolean res = false;
    public static List<Boolean> isSelected = new ArrayList<>();
    ArrayList<Integer> position = new ArrayList<>();
    LinearLayout orderDetailsLL;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        mContext = OrderDetailsActivity.this;
        BackendServer backend = new BackendServer(mContext);
        client = backend.getHTTPClient();
        orders = new ArrayList<>();
        final String address = getIntent().getExtras().getString("address");
        int pos = getIntent().getExtras().getInt("pos");
        final String pk = getIntent().getExtras().getString("pk");
        getOrderDetails(pk);

        init();
        progressBar.setVisibility(View.VISIBLE);
        orderDetailsLL.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Order order = orders.get(0);
                qtyMaps = order.getOrderQtyMaps();
                progressBar.setVisibility(View.GONE);
                orderDetailsLL.setVisibility(View.VISIBLE);
                RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(mContext);
                recyclerViewOrder.setLayoutManager(recyclerViewLayoutManager);
                recyclerViewOrder.setAdapter(new OrderRecyclerViewAdapter(qtyMaps));
                orderShipping.setText(address);
            }
        },1000);
    }

    public void getOrderDetails(String pk) {
        client.get(BackendServer.url+"/api/ecommerce/order/"+pk+"/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Order order = new Order(response);
                    orders.add(order);
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

    public void init(){
        progressBar = findViewById(R.id.progressBar_orderCancel);
        orderDetailsLL = findViewById(R.id.order_details);
        orderShipping = findViewById(R.id.selected_address);
        linearLayoutCancel = findViewById(R.id.text_cancel_order);
        linearLayoutReturn = findViewById(R.id.text_return_order);
        recyclerViewOrder = findViewById(R.id.recycler_view_order);

        linearLayoutCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (position.size()>0) {
                    orderCancelOrReturn(true);
//                } else Toast.makeText(mContext, "Please select one item.", Toast.LENGTH_SHORT).show();
            }
        });

        linearLayoutReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (position.size()>0) {
                    orderCancelOrReturn(false);
//                } else Toast.makeText(mContext, "Please select one item.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void orderCancelOrReturn(boolean isClick) {
        RecyclerView recyclerViewCancel;
        Button cancelBtn;
        position.clear();
        View v = getLayoutInflater().inflate(R.layout.layout_order_cancel_style, null, false);
        for (int p=0; p<qtyMaps.size(); p++) {
            if (isSelected.get(p) == true) {

//                View v = getLayoutInflater().inflate(R.layout.layout_order_cancel_style, null, false);
                position.add(p);
                final OrderQtyMap map = qtyMaps.get(p);
                if (isClick) {
                    if (map.getOrderStatus().equals("created") || map.getOrderStatus().equals("packed")) {
                        recyclerViewCancel = v.findViewById(R.id.recycler_view_cancelOrReturn);
                        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(mContext);
                        recyclerViewCancel.setLayoutManager(recyclerViewLayoutManager);
                        final CancelRecyclerViewAdapter adapter = new CancelRecyclerViewAdapter(position);
                        recyclerViewCancel.setAdapter(adapter);
                        cancelBtn = v.findViewById(R.id.cancel_btn);
                        final AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                        adb.setView(v);
                        final AlertDialog dialog = adb.create();
                        dialog.show();
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                RequestParams params = new RequestParams();
                                params.put("status", "cancelled");
                                client.patch(BackendServer.url + "/api/ecommerce/orderQtyMap/" + map.getOrderQtyPk() + "/", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        Toast.makeText(OrderDetailsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Toast.makeText(OrderDetailsActivity.this, "failure", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(mContext, "selected items can't be cancelled", Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else {
                    if ((map.getOrderStatus().equals("delivered"))) {
                        recyclerViewCancel = v.findViewById(R.id.recycler_view_cancelOrReturn);
                        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(mContext);
                        recyclerViewCancel.setLayoutManager(recyclerViewLayoutManager);
                        final CancelRecyclerViewAdapter adapter = new CancelRecyclerViewAdapter(position);
                        recyclerViewCancel.setAdapter(adapter);
                        cancelBtn = v.findViewById(R.id.cancel_btn);
                        final AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                        adb.setView(v);
                        final AlertDialog dialog = adb.create();
                        dialog.show();
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                RequestParams params = new RequestParams();
                                params.put("status", "returned");
                                client.patch(BackendServer.url + "/api/ecommerce/orderQtyMap/" + map.getOrderQtyPk() + "/", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        Toast.makeText(OrderDetailsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Toast.makeText(OrderDetailsActivity.this, "failure", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(mContext, "selected items can't be returned", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            } else {
                Toast.makeText(mContext, "Please select one item.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    private class OrderRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {
        ArrayList<OrderQtyMap> mQtyMaps;
        AsyncHttpClient client;

        public OrderRecyclerViewAdapter(ArrayList<OrderQtyMap> qtyMaps) {
            this.mQtyMaps = qtyMaps;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_order_details_item, viewGroup, false);
            client = new AsyncHttpClient();
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
            final OrderQtyMap qtyMap = mQtyMaps.get(i);
            isSelected.add(i, false);
            isSelected.set(i, false);
            viewHolder.productName.setText(""+qtyMap.getOrderProductName());
            viewHolder.qty.setText(""+qtyMap.getOrderQty());
            viewHolder.amount.setText(""+qtyMap.getOrderPpAfterDiscount());
            viewHolder.status.setText(""+qtyMap.getOrderStatus());
            viewHolder.selectOrderItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                }
//            });
//                    setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    boolean bol = position.contains(i);
                    if (!viewHolder.selectOrderItem.isChecked()) {
                        isSelected.set(i, false);
//                        position.remove(i);
                    } else {
                        isSelected.set(i, true);
                        viewHolder.selectOrderItem.setChecked(isSelected.get(i));
//                        position.add(i);
//                        res = !bol;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mQtyMaps.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder{
        TextView  productName, qty, amount, status;
        CheckBox selectOrderItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name_result);
            qty = itemView.findViewById(R.id.qty_result);
            amount = itemView.findViewById(R.id.approved_detail_result);
            status = itemView.findViewById(R.id.status_detail_result);
            selectOrderItem = itemView.findViewById(R.id.selected_product_cb);
        }
    }

    private class CancelRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {
        ArrayList<Integer> mPosition;
        public CancelRecyclerViewAdapter(ArrayList<Integer> position) {
            mPosition = position;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_order_cancel, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
            int pos = mPosition.get(i);
            OrderQtyMap map = qtyMaps.get(pos);
            viewHolder.cancelProductName.setText(map.getOrderProductName());
            viewHolder.cancelQty.setText(map.getOrderQty());
            viewHolder.cancelAmount.setText(map.getOrderPpAfterDiscount());
            viewHolder.cancelRefundedAmount.setText(map.getOrderRefundAmount());
        }

        @Override
        public int getItemCount() {
            return mPosition.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        TextView cancelProductName, cancelQty, cancelAmount, cancelRefundedAmount;
        public MyViewHolder(@NonNull View v) {
            super(v);
            cancelProductName = v.findViewById(R.id.cancel_product_name);
            cancelQty = v.findViewById(R.id.cancel_qty_result);
            cancelAmount = v.findViewById(R.id.amount_result);
            cancelRefundedAmount = v.findViewById(R.id.refunded_amount_result);
        }
    }
}
