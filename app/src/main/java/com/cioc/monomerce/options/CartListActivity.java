package com.cioc.monomerce.options;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.R;
import com.cioc.monomerce.communicator.CartUpdate;
import com.cioc.monomerce.communicator.RecyclerItemClickListener;
import com.cioc.monomerce.entites.Cart;
import com.cioc.monomerce.entites.ListingParent;
import com.cioc.monomerce.product.ItemDetailsActivity;
import com.cioc.monomerce.startup.MainActivity;
import com.cioc.monomerce.utility.ImageUrlUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.githang.stepview.StepView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.cioc.monomerce.fragments.ImageListFragment.STRING_IMAGE_POSITION;
import static com.cioc.monomerce.fragments.ImageListFragment.STRING_IMAGE_URI;


public class CartListActivity extends AppCompatActivity implements CartUpdate {
    private static Context mContext;
    TextView textTotalPrice;
    Button bStartShopping, checkOutAction;
    private static StepView mStepView;
    ProgressBar progressBar;
    public static LinearLayout layoutCartItems, layoutCartPayments, layoutCartNoItems;
    public AsyncHttpClient client;
    public static int price=0;
    RecyclerView recyclerView;
    Toast toast;
//    public static ArrayList<Cart> cartList = MainActivity.cartList;
    public static ArrayList<Cart> cartList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        mContext = CartListActivity.this;
        BackendServer backend = new BackendServer(this);
        client = backend.getHTTPClient();
        cartList = new ArrayList<>();

        getCardItem();
        ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
        final ArrayList<String> cartlistImageUri = imageUrlUtils.getCartListImageUri();
        //Show cart layout based on items
        setCartLayout();


        List<String> steps = Arrays.asList(new String[]{"Selected Items", "Shipping Address", "Review Your Order"});
        mStepView.setSteps(steps);
        mStepView.selectedStep(1);

        recyclerView = findViewById(R.id.recyclerview);
        textTotalPrice = findViewById(R.id.text_action_bottom1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               setCartListRecyclerView();
            }
        },1000);
    }

    void setCartListRecyclerView(){
        progressBar.setVisibility(View.GONE);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        CartListRecyclerViewAdapter adapter = new CartListRecyclerViewAdapter(cartList, price, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textTotalPrice.setText("\u20B9"+CartListRecyclerViewAdapter.mPrice);
            }
        },500);
    }

    public void getCardItem() {
        client.get(BackendServer.url+"/api/ecommerce/cart/?&Name__contains=&user=1&typ=cart",
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        for (int i=0; i<response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Cart cart = new Cart(object);
                                cartList.add(cart);
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

    @SuppressLint("NewApi")
    public static final void recreateActivityCompat(final Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            a.recreate();
        } else {
            final Intent intent = a.getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            a.finish();
            a.overridePendingTransition(0, 0);
            a.startActivity(intent);
            a.overridePendingTransition(0, 0);
        }
    }

    @Override
    public void setValue(String value) {
        textTotalPrice.setText("\u20B9"+value);
    }

    public static class CartListRecyclerViewAdapter
            extends RecyclerView.Adapter<CartListRecyclerViewAdapter.ViewHolder> {

        private ArrayList<Cart> mCartlist;
        BackendServer backendServer = new BackendServer(mContext);
        AsyncHttpClient client = backendServer.getHTTPClient();
        CartListActivity activity;
        public static int mPrice;
        Toast toast;
        CartUpdate cartUpdate;
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem, mLayoutRemove;
            TextView productName, itemPrice, actualPrice, discountPercentage, itemsQuantity;
            ImageView itemsQuantityAdd, itemsQuantityRemove, cardWishList;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image_cartlist);
                mLayoutItem =  view.findViewById(R.id.layout_item_desc);
                productName =  view.findViewById(R.id.product_name);
                itemPrice =  view.findViewById(R.id.item_price);
                actualPrice =  view.findViewById(R.id.actual_price);
                actualPrice.setPaintFlags(actualPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                discountPercentage =  view.findViewById(R.id.discount_percentage);
                mLayoutRemove =  view.findViewById(R.id.layout_action1);
                itemsQuantity =  view.findViewById(R.id.items_quantity);
                itemsQuantityAdd =  view.findViewById(R.id.items_quantity_add);
                itemsQuantityRemove =  view.findViewById(R.id.items_quantity_remove);
                cardWishList =  view.findViewById(R.id.card_wishlist);
            }
        }

        public CartListRecyclerViewAdapter(ArrayList<Cart> carts, int price, CartUpdate listener) {
            mCartlist = carts;
            mPrice = price;
            cartUpdate = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cartlist_item, parent, false);
            client = new AsyncHttpClient();
            return new CartListRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(CartListActivity.CartListRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final CartListActivity.CartListRecyclerViewAdapter.ViewHolder holder, final int position) {
            final Cart cart = mCartlist.get(position);
            final ListingParent parent = cart.getParents().get(0);
            final Uri uri = Uri.parse(parent.getFilesAttachment());
            holder.mImageView.setImageURI(uri);

            holder.productName.setText(parent.getProductName());
            holder.itemsQuantity.setText(cart.getQuantity());
            if (parent.getProductDiscount().equals("0")){
                holder.itemPrice.setText("\u20B9"+parent.getProductPrice());
                holder.actualPrice.setVisibility(View.GONE);
                holder.discountPercentage.setVisibility(View.GONE);
                mPrice = mPrice + (parent.getProductIntPrice()*Integer.parseInt(cart.getQuantity()));
            } else {
                holder.itemPrice.setText("\u20B9"+parent.getProductDiscountedPrice());
                mPrice = mPrice + (parent.getProductIntDiscountedPrice()*Integer.parseInt(cart.getQuantity()));
                holder.discountPercentage.setVisibility(View.VISIBLE);
                holder.discountPercentage.setText("("+parent.getProductDiscount()+"% OFF)");
                holder.actualPrice.setVisibility(View.VISIBLE);
                holder.actualPrice.setText("\u20B9"+parent.getProductPrice());
                holder.actualPrice.setPaintFlags(holder.actualPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            }


            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ItemDetailsActivity.class);
                    intent.putExtra(STRING_IMAGE_URI, cart.getListingParent().getFilesAttachment());
                    intent.putExtra(STRING_IMAGE_POSITION, position);
                    intent.putExtra("listingLitePk", parent.getPk());
                    mContext.startActivity(intent);
                }
            });

           //Set click action
            holder.mLayoutRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteItem(cart, position);
                    notifyDataSetChanged();
                }
            });


            holder.cardWishList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveWishList(cart);
                    holder.cardWishList.setImageResource(R.drawable.ic_favorite_red_24dp);
                    notifyDataSetChanged();
//                    Toast.makeText(mContext,"Item added to wishlist.", Toast.LENGTH_SHORT).show();
                }
            });

            //Set click action
            holder.itemsQuantityRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String quan = holder.itemsQuantity.getText().toString();
                    int quantRemove = Integer.parseInt(quan);
                    if (quantRemove <= 1) {
                        deleteItem(cart, position);
                    } else {
                        quantRemove--;
                        holder.itemsQuantity.setText(quantRemove + "");
                        if (parent.getProductDiscount().equals("0")) {
                            mPrice = mPrice - parent.getProductIntPrice();
                        } else mPrice = mPrice - parent.getProductIntDiscountedPrice();
                        updateItem(String.valueOf(quantRemove), cart, mPrice);
                    }
                    notifyDataSetChanged();
                    notifyItemChanged(position);
                }
            });

            holder.itemsQuantityAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String quan = holder.itemsQuantity.getText().toString();
                    int quantAdd = Integer.parseInt(quan);
                    quantAdd++;
                    if (parent.getProductDiscount().equals("0")) {
                        mPrice = mPrice + parent.getProductIntPrice();
                    } else mPrice = mPrice + parent.getProductIntDiscountedPrice();
                    holder.itemsQuantity.setText(quantAdd+"");
                    updateItem(String.valueOf(quantAdd), cart, mPrice);

                    notifyDataSetChanged();
                    notifyItemChanged(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCartlist.size();
        }

        public void deleteItem(final Cart cart, final int position) {
            client.delete(mContext, BackendServer.url + "/api/ecommerce/cart/"+ cart.getPk()+"/", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (toast!= null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(mContext,"removed "+ cart.getPk(), Toast.LENGTH_SHORT);
                    toast.show();
//                    ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
//                    imageUrlUtils.removeCartListImageUri(position);
                    //Decrease notification count
                    MainActivity.notificationCountCart--;
                    if (MainActivity.notificationCountCart==0) {
                        layoutCartNoItems.setVisibility(View.VISIBLE);
                        layoutCartItems.setVisibility(View.GONE);
                        layoutCartPayments.setVisibility(View.GONE);
                        mStepView.setVisibility(View.GONE);
                    }
                    mContext.startActivity(new Intent(mContext, CartListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    notifyItemChanged(position);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (toast!= null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(mContext,"removing failure "+ cart.getPk(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }

        public void updateItem(String qty, final Cart cart, int price) {
            RequestParams params = new RequestParams();
            params.put("qty", qty);
            client.patch(BackendServer.url + "/api/ecommerce/cart/" + cart.getPk()+ "/", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(mContext, "updated cart"+ cart.getPk(), Toast.LENGTH_SHORT).show();
                    cartUpdate.setValue(String.valueOf(price));
//                    mContext.startActivity(new Intent(mContext, CartListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                    activity.recreate();
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(mContext, "failure cart"+ cart.getPk(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void moveWishList(Cart cart) {
            RequestParams params = new RequestParams();
            params.put("typ", "favourite");
            client.patch(BackendServer.url+"/api/ecommerce/cart/"+ cart.getPk()+"/", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(mContext, "onSuccess", Toast.LENGTH_SHORT).show();
                    mContext.startActivity(new Intent(mContext, CartListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                    MainActivity.notificationCountCart--;
//                    recreateActivityCompat(activity);
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(mContext, "onFailure", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    protected void setCartLayout(){
        layoutCartItems = findViewById(R.id.layout_items);
        layoutCartPayments = findViewById(R.id.layout_payment);
        layoutCartNoItems = findViewById(R.id.layout_cart_empty);
        mStepView = (StepView) findViewById(R.id.step_view);
        progressBar =  findViewById(R.id.progressBar);
        checkOutAction = findViewById(R.id.text_action_bottom2);
        bStartShopping =  findViewById(R.id.bAddNew);

        if (MainActivity.notificationCountCart >0) {
            layoutCartNoItems.setVisibility(View.GONE);
            layoutCartItems.setVisibility(View.VISIBLE);
            layoutCartPayments.setVisibility(View.VISIBLE);
            mStepView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            checkOutAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CartListRecyclerViewAdapter.mPrice<=0){
                        Log.d("TAG", "price is zero");
                    } else {
                        startActivity(new Intent(getApplicationContext(), CheckOutActivity.class).putExtra("totalPrice", CartListRecyclerViewAdapter.mPrice));
                    }
                }
            });

        } else {
            layoutCartNoItems.setVisibility(View.VISIBLE);
            layoutCartItems.setVisibility(View.GONE);
            layoutCartPayments.setVisibility(View.GONE);
            mStepView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);


            bStartShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }
}
