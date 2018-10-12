package com.cioc.monomerce.payment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cioc.monomerce.R;
import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.entites.Address;
import com.cioc.monomerce.entites.Cart;
import com.cioc.monomerce.entites.ListingParent;
import com.cioc.monomerce.options.CartListActivity;
import com.cioc.monomerce.options.CheckOutActivity;
import com.cioc.monomerce.options.OrderActivity;
import com.cioc.monomerce.product.ItemDetailsActivity;
import com.cioc.monomerce.startup.LoginActivity;
import com.cioc.monomerce.startup.MainActivity;
import com.cioc.monomerce.utility.ImageUrlUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.githang.stepview.StepView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.roger.catloadinglibrary.CatLoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;



public class PaymentActivity extends AppCompatActivity {
    private static Context mContext;
    TextView selectedAddress;
    TextView textAmount, paymentBtn;
    RecyclerView recyclerView;
    AsyncHttpClient client;
    RadioGroup radioGroup;
    RadioButton radioButtonCOD, radioButtonCard;
    ArrayList<Cart> cardlist = CartListActivity.cartList;
    String addressPk;
    JSONObject jsonObj;
    CatLoadingView mView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        BackendServer server = new BackendServer(this);
        client = server.getHTTPClient();
        String address = getIntent().getExtras().getString("address");
        addressPk = getIntent().getExtras().getString("pk");
        getAddress();
        mContext = PaymentActivity.this;
        BackendServer backend = new BackendServer(this);
        client = backend.getHTTPClient();
        mView = new CatLoadingView();
        init();
        //Show cart layout based on items
        StepView mStepView = (StepView) findViewById(R.id.step_view);
        List<String> steps = Arrays.asList(new String[]{"Selected Items", "Shipping Address", "Review Your Order"});
        mStepView.setSteps(steps);
        mStepView.selectedStep(3);

        RecyclerView.LayoutManager recylerViewLayoutManager = new LinearLayoutManager(mContext);

        recyclerView.setLayoutManager(recylerViewLayoutManager);
        recyclerView.setAdapter(new PaymentActivity.PaymentRecyclerViewAdapter(recyclerView, cardlist));

        selectedAddress.setText(address);
        textAmount.setText(getIntent().getStringExtra("totalPrice"));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i==R.id.radio_cod){
                    paymentBtn.setText("ORDER");
                }
                if (i==R.id.radio_card){
                    paymentBtn.setText("PAYMENT");
                }
            }
        });

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment(paymentBtn.getText().toString().equals("ORDER"));
            }
        });
    }

    public void init(){
        selectedAddress = findViewById(R.id.selected_address);
        paymentBtn = findViewById(R.id.payment_text_button);
        textAmount = findViewById(R.id.text_amount);
        recyclerView = findViewById(R.id.recyclerview_payment);
        radioGroup = findViewById(R.id.radio_payment);
        radioButtonCard = findViewById(R.id.radio_card);
        radioButtonCOD = findViewById(R.id.radio_cod);
    }

    public void getAddress() {
        client.get(BackendServer.url+"/api/ecommerce/address/"+addressPk+"/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                jsonObj =  response;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void payment(boolean res) {
        JSONObject object = new JSONObject();
        try {
            object.put("city", jsonObj.getString("city"));
            object.put("country", jsonObj.getString("country"));
            object.put("landMark", jsonObj.getString("landMark"));
            String mobile = jsonObj.getString("mobileNo");
            if (mobile.equals("null") || mobile==null){
                object.put("mobile", "");
            } else object.put("mobile", mobile);

            object.put("mobileNo", "");
            object.put("lat", "");
            object.put("lon", "");
            object.put("pincode", jsonObj.getString("pincode"));
            object.put("pk", jsonObj.getString("pk"));
            object.put("primary", jsonObj.getBoolean("primary"));
            object.put("state", jsonObj.getString("state"));
            object.put("street", jsonObj.getString("street"));
            object.put("title", jsonObj.getString("title"));
            object.put("user", jsonObj.getString("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < cardlist.size(); i++) {
                Cart cart = cardlist.get(i);
                JSONObject product = new JSONObject();
                product.put("pk", Integer.parseInt(cart.getListingParent().getPk()));
                product.put("prodSku", cart.getProdSku());
                product.put("qty", Integer.parseInt(cart.getQuantity()));
                array.put(product);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject params = new JSONObject();
        try {
            params.put("address", object);
            params.put("products", array);
            params.put("modeOfShopping", "online");
            params.put("promoCode", "");
            params.put("promoCodeDiscount", "0");

            if (res) {
                params.put("modeOfPayment", "COD");
                params.put("paidAmount", "0");
                StringEntity entity = null;

                try{
                    entity = new StringEntity(params.toString());
                }catch(Exception e){

                }
//                mView.show(getSupportFragmentManager(), "");
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please Wait...");
                progressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (progressDialog.getProgress() <= progressDialog
                                    .getMax()) {
                                Thread.sleep(200);
                                handle.sendMessage(handle.obtainMessage());
                                if (progressDialog.getProgress() == progressDialog.getMax()) {
                                    progressDialog.dismiss();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                client.post(mContext,BackendServer.url+"/api/ecommerce/createOrder/", entity,"application/json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            MainActivity.notificationCountCart=0;
                            String ordno = response.getString("odnumber");
                            Toast.makeText(PaymentActivity.this, "Order No. "+ordno, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PaymentActivity.this, OrderActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(PaymentActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(PaymentActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.incrementProgressBy(0);
        }
    };


    public static class PaymentRecyclerViewAdapter
            extends RecyclerView.Adapter<PaymentActivity.PaymentRecyclerViewAdapter.ViewHolder> {

        private ArrayList<Cart> mPaymentlist;
        private RecyclerView mRecyclerView;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem;
            TextView productName, itemPrice, actualPrice, discountPercentage;//, itemsQuantity;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image_cartlist);
                mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item_desc);
                productName =  view.findViewById(R.id.product_name);
                itemPrice =  view.findViewById(R.id.item_price);
                actualPrice =  view.findViewById(R.id.actual_price);
                actualPrice.setPaintFlags(actualPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                discountPercentage =  view.findViewById(R.id.discount_percentage);
            }
        }

        public PaymentRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<Cart> paymentlist) {
            mPaymentlist = paymentlist;
            mRecyclerView = recyclerView;
        }

        @Override
        public PaymentActivity.PaymentRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cartlist_payment, parent, false);
            return new PaymentActivity.PaymentRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(PaymentActivity.PaymentRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final PaymentActivity.PaymentRecyclerViewAdapter.ViewHolder holder, final int position) {
            final Cart cart = mPaymentlist.get(position);
            final ListingParent parent = cart.getParents().get(0);
            final Uri uri = Uri.parse(parent.getFilesAttachment());
            holder.mImageView.setImageURI(uri);

            holder.productName.setText(parent.getProductName()+" "+cart.getProd_howMuch()+" "+parent.getUnit());
//            holder.itemsQuantity.setText(cart.getQuantity());
            if (cart.getProdVarPrice().equals("null")) {
                if (parent.getProductDiscount().equals("0")) {
                    holder.itemPrice.setText("\u20B9" + parent.getProductPrice());
                    holder.actualPrice.setVisibility(View.GONE);
                    holder.discountPercentage.setVisibility(View.GONE);
//                    mPrice = mPrice + (parent.getProductIntPrice() * Integer.parseInt(cart.getQuantity()));
                } else {
                    holder.itemPrice.setText("\u20B9" + parent.getProductDiscountedPrice());
//                    mPrice = mPrice + (parent.getProductIntDiscountedPrice() * Integer.parseInt(cart.getQuantity()));
                    holder.discountPercentage.setVisibility(View.VISIBLE);
                    holder.discountPercentage.setText("(" + parent.getProductDiscount() + "% OFF)");
                    holder.actualPrice.setVisibility(View.VISIBLE);
                    holder.actualPrice.setText("\u20B9" + parent.getProductPrice());
                    holder.actualPrice.setPaintFlags(holder.actualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            } else {
                if (parent.getProductDiscount().equals("0")) {
                    holder.itemPrice.setText("\u20B9" + cart.getProdVarPrice());
                    holder.actualPrice.setVisibility(View.GONE);
                    holder.discountPercentage.setVisibility(View.GONE);
//                    mPrice = mPrice + (Integer.parseInt(cart.getProdVarPrice()) * Integer.parseInt(cart.getQuantity()));
                } else {
                    holder.itemPrice.setText("\u20B9" + cart.getProdVarPrice());
//                    mPrice = mPrice + (Integer.parseInt(cart.getProdVarPrice()) * Integer.parseInt(cart.getQuantity()));
                    holder.discountPercentage.setVisibility(View.VISIBLE);
                    holder.discountPercentage.setText("(" + parent.getProductDiscount() + "% OFF)");
                    holder.actualPrice.setVisibility(View.VISIBLE);
                    JSONArray array = parent.getItemArray();
                    if (array.length()>0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObj = null;
                            try {
                                jsonObj = array.getJSONObject(i);
                                String sku = jsonObj.getString("sku");
                                String pricearray = jsonObj.getString("price");
                                String discountedPrice = jsonObj.getString("discountedPrice");
                                if (cart.getProdSku().equals(sku)) {
                                    holder.actualPrice.setText("\u20B9" + pricearray);
                                    holder.actualPrice.setPaintFlags(holder.actualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return mPaymentlist.size();
        }
    }



}
