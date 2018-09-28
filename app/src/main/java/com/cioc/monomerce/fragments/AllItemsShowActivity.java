package com.cioc.monomerce.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


import com.bumptech.glide.Glide;
import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.R;
import com.cioc.monomerce.entites.ListingParent;
import com.cioc.monomerce.notification.NotificationCountSetClass;
import com.cioc.monomerce.product.ItemDetailsActivity;
import com.cioc.monomerce.startup.MainActivity;
import com.cioc.monomerce.utility.ImageUrlUtils;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.michaelbel.bottomsheet.BottomSheet;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.cioc.monomerce.fragments.ImageListFragment.STRING_IMAGE_POSITION;
import static com.cioc.monomerce.fragments.ImageListFragment.STRING_IMAGE_URI;

public class AllItemsShowActivity extends AppCompatActivity {
    public Context context;
    private RecyclerView recyclerViewList;
    private ProgressBar progressBar;
    Button sortBtn, filterBtn;
    AsyncHttpClient client;
    ArrayList<ListingParent> parents;
    String pk, name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items_show);
        context = AllItemsShowActivity.this;
        BackendServer backend = new BackendServer(this);
        client = backend.getHTTPClient();
        parents = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name = getIntent().getExtras().getString("fragmentName");
        pk = getIntent().getExtras().getString("pk");


        final DrawerLayout drawer = findViewById(R.id.drawer_layout_all_items);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
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

        init();
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewList.setVisibility(View.GONE);
        getItems(pk);
    }

    public void init(){
        recyclerViewList = findViewById(R.id.recyclerview_all_list);
        progressBar = findViewById(R.id.progressBar);
        sortBtn = findViewById(R.id.sort_action_button);
        filterBtn = findViewById(R.id.filter_action_button);
    }
    public void getItems(String pk) {
        client.get(BackendServer.url+"/api/ecommerce/listing/?parent="+pk+"&recursive=1", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        ListingParent parent = new ListingParent(object);
                        parents.add(parent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                getSupportActionBar().setTitle(name);
                progressBar.setVisibility(View.GONE);
                recyclerViewList.setVisibility(View.VISIBLE);
                clickBtn(parents);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(), "onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void clickBtn(ArrayList<ListingParent> strings){
        setupRecyclerView(recyclerViewList, strings);
        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView sortNew, sortPopular, sortLowToHigh, sortHighToLow;
                View lv = getLayoutInflater().inflate(R.layout.layout_sort_items, null, false);
                sortNew = lv.findViewById(R.id.sort_new);
                sortPopular = lv.findViewById(R.id.sort_popular);
                sortLowToHigh = lv.findViewById(R.id.sort_lowToHigh);
                sortHighToLow = lv.findViewById(R.id.sort_highToLow);

                final BottomSheet.Builder bsb = new BottomSheet.Builder(context);
                bsb.setView(lv);

                sortNew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sortNew.setTextColor(Color.parseColor("#f2563e"));
                        sortPopular.setTextColor(Color.parseColor("#213858"));
                        sortLowToHigh.setTextColor(Color.parseColor("#213858"));
                        sortHighToLow.setTextColor(Color.parseColor("#213858"));
                        bsb.dismiss();
                    }
                });
                sortPopular.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sortNew.setTextColor(Color.parseColor("#213858"));
                        sortPopular.setTextColor(Color.parseColor("#f2563e"));
                        sortLowToHigh.setTextColor(Color.parseColor("#213858"));
                        sortHighToLow.setTextColor(Color.parseColor("#213858"));
                        bsb.dismiss();
                    }
                });
                sortLowToHigh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sortNew.setTextColor(Color.parseColor("#213858"));
                        sortPopular.setTextColor(Color.parseColor("#213858"));
                        sortLowToHigh.setTextColor(Color.parseColor("#f2563e"));
                        sortHighToLow.setTextColor(Color.parseColor("#213858"));
                        bsb.dismiss();
                    }
                });
                sortHighToLow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sortNew.setTextColor(Color.parseColor("#213858"));
                        sortPopular.setTextColor(Color.parseColor("#213858"));
                        sortLowToHigh.setTextColor(Color.parseColor("#213858"));
                        sortHighToLow.setTextColor(Color.parseColor("#f2563e"));
                        bsb.dismiss();
                    }
                });
                bsb.show();
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button filterPrice;
                CrystalRangeSeekbar rangeSeekbar;
                View view = getLayoutInflater().inflate(R.layout.layout_filter_categories, null, false);
                rangeSeekbar = view.findViewById(R.id.range_seekbar);
                // get min and max text view
                final TextView tvMin = view.findViewById(R.id.min_price);
                final TextView tvMax = view.findViewById(R.id.max_price);
                filterPrice = view.findViewById(R.id.show_result);
//                set listener
                rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                    @Override
                    public void valueChanged(Number minValue, Number maxValue) {
                        tvMin.setText("\u20B9"+String.valueOf(minValue));
                        tvMax.setText("\u20B9"+String.valueOf(maxValue));
                    }
                });

//                set final value listener
                final String[] min = {""};
                final String[] max = {""};
                rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
                    @Override
                    public void finalValue(Number minValue, Number maxValue) {
                        min[0] = String.valueOf(minValue);
                        max[0] = String.valueOf(maxValue);
                        Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
                    }
                });
                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                adb.setView(view);
                AlertDialog ad = adb.create();
                filterPrice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filterBy(min[0], max[0], ad);
                    }
                });
                ad.show();
            }
        });
    }

    public void filterBy(String min, String max, AlertDialog ad) {
        client.get(BackendServer.url+"/api/ecommerce/listing/?parent="+pk+"&recursive=1&fields={}&maxPrice="+max+"&minPrice="+min,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        recyclerViewList.setVisibility(View.GONE);
                        parents.clear();
                        for (int i=0; i<response.length(); i++){
                            try {
                                JSONObject object = response.getJSONObject(i);
                                ListingParent parent = new ListingParent(object);
                                parents.add(parent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerViewList.setVisibility(View.VISIBLE);
                                setupRecyclerView(recyclerViewList, parents);
                                ad.dismiss();
                            }
                        },200);


                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });
    }

    public void setupRecyclerView(RecyclerView recyclerView, ArrayList<ListingParent> items) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        AllItemsRecyclerViewAdapter adapter = new AllItemsShowActivity.AllItemsRecyclerViewAdapter(context, items);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public static class AllItemsRecyclerViewAdapter extends RecyclerView.Adapter<AllItemsShowActivity.AllItemsRecyclerViewAdapter.ViewHolder> {
        Context mContext;
        BackendServer backendServer;
        AsyncHttpClient client;
        private ArrayList<ListingParent> mValues;
        Toast toast;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mImageView;
            public final LinearLayout mLayoutItem, mLayoutItemCart2;
            public final ImageView mImageViewWishlist, mCartImageBtn;
            TextView itemName, itemPrice, itemDiscount, itemDiscountPrice, itemsQuantity, itemsOutOfStock;
            boolean res;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = view.findViewById(R.id.image1);
                mLayoutItem = view.findViewById(R.id.layout_item);
                mLayoutItemCart2 = view.findViewById(R.id.layout_action2_cart);
                mImageViewWishlist = view.findViewById(R.id.ic_wishlist);
                itemName =  view.findViewById(R.id.item_name);
                itemPrice =  view.findViewById(R.id.item_price);
                itemDiscountPrice =  view.findViewById(R.id.actual_price);
                itemDiscount =  view.findViewById(R.id.discount_percentage);
                itemsQuantity =  view.findViewById(R.id.item_added);
                itemsOutOfStock =  view.findViewById(R.id.out_of_stock);
                mCartImageBtn =  view.findViewById(R.id.card_item_quantity_add);
            }
        }

        public AllItemsRecyclerViewAdapter(Context context, ArrayList<ListingParent> items) {
            this.mContext = context;
            mValues = items;
            backendServer = new BackendServer(mContext);
            client = backendServer.getHTTPClient();
        }

        @Override
        public AllItemsShowActivity.AllItemsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new AllItemsShowActivity.AllItemsRecyclerViewAdapter.ViewHolder(view);
        }

//        @Override
//        public void onViewRecycled(AllItemsShowActivity.AllItemsRecyclerViewAdapter.ViewHolder holder) {
//            if (holder.mImageView.getController() != null) {
//                holder.mImageView.getController().onDetach();
//            }
//            if (holder.mImageView.getTopLevelDrawable() != null) {
//                holder.mImageView.getTopLevelDrawable().setCallback(null);
////                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
//            }
//        }

        @Override
        public void onBindViewHolder(final AllItemsShowActivity.AllItemsRecyclerViewAdapter.ViewHolder holder, final int position) {
            final ListingParent parent = mValues.get(position);
            String link;
            if (parent.isInStock()) {
                holder.itemsOutOfStock.setVisibility(View.GONE);
                String qunt = parent.getAddedCart();
                int qntAdd = Integer.parseInt(qunt);
                if (qntAdd <= 0) {
                    holder.mLayoutItemCart2.setVisibility(View.VISIBLE);
                    holder.itemsQuantity.setVisibility(View.GONE);
                    holder.mCartImageBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RequestParams params = new RequestParams();
                            params.put("product", parent.getPk());
                            params.put("qty", "1");
                            params.put("typ", "cart");
                            params.put("user", parent.getUser());
                            client.post(BackendServer.url + "/api/ecommerce/cart/", params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    holder.mLayoutItemCart2.setVisibility(View.GONE);
                                    holder.itemsQuantity.setVisibility(View.VISIBLE);
                                    holder.mImageViewWishlist.setImageResource(R.drawable.ic_favorite_border_green_24dp);
                                    holder.res = true;
                                    Toast toast = null;
                                    if (toast != null) {
                                        toast.cancel();
                                    }
                                    toast = Toast.makeText(mContext, "Item added to cart.", Toast.LENGTH_SHORT);
                                    toast.show();
                                    MainActivity.notificationCountCart++;
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    Toast toast = null;
                                    if (toast != null) {
                                        toast.cancel();
                                    }
                                    toast = Toast.makeText(mContext, "This Product is already in card.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
                    });
                } else {
                    holder.mLayoutItemCart2.setVisibility(View.GONE);
                    holder.itemsQuantity.setVisibility(View.VISIBLE);
                }
            } else {
                holder.itemsOutOfStock.setVisibility(View.VISIBLE);
                holder.itemsQuantity.setVisibility(View.GONE);
                holder.mLayoutItemCart2.setVisibility(View.GONE);
            }
            if (parent.getFilesAttachment().equals("null")){
                link = BackendServer.url+"/static/images/ecommerce.jpg";
            } else
                link = parent.getFilesAttachment();
            Glide.with(mContext)
                    .load(link)
                    .into(holder.mImageView);

            Double d = Double.parseDouble(parent.getProductPrice());
            final int price = (int) Math.round(d);
            Double d1 = Double.parseDouble(parent.getProductDiscountedPrice());
            final int price1 = (int) Math.round(d1);

            holder.itemName.setText(parent.getProductName());
            if (parent.getProductDiscount().equals("0")){
                holder.itemPrice.setText("\u20B9"+ price);
                holder.itemDiscountPrice.setVisibility(View.GONE);
                holder.itemDiscount.setVisibility(View.GONE);
            } else {
                holder.itemPrice.setText("\u20B9"+price1);
                holder.itemDiscountPrice.setVisibility(View.VISIBLE);
                holder.itemDiscountPrice.setText("\u20B9"+price);
                holder.itemDiscountPrice.setPaintFlags(holder.itemDiscountPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                holder.itemDiscount.setVisibility(View.VISIBLE);
                holder.itemDiscount.setText(parent.getProductDiscount()+"% OFF");
            }

            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ItemDetailsActivity.class);
                    intent.putExtra(STRING_IMAGE_URI, parent.getFilesAttachment());
                    intent.putExtra(STRING_IMAGE_POSITION, position);
                    intent.putExtra("listingLitePk", parent.getPk());
                    mContext.startActivity(intent);
                }
            });

            //Set click action for wishlist
            String quntWish = parent.getAddedWish();
            int qntWishAdd = Integer.parseInt(quntWish);
            if (qntWishAdd==0) {
                holder.mImageViewWishlist.setImageResource(R.drawable.ic_favorite_border_green_24dp);
                holder.res = true;
            }else {
                holder.mImageViewWishlist.setImageResource(R.drawable.ic_favorite_red_24dp);
                holder.res = false;
            }

            holder.mImageViewWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.res) {
                        RequestParams params = new RequestParams();
                        params.put("product", parent.getPk());
                        params.put("qty", "1");
                        params.put("typ", "favourite");
                        params.put("user", parent.getUser());
                        client.post(BackendServer.url + "/api/ecommerce/cart/", params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                holder.mImageViewWishlist.setImageResource(R.drawable.ic_favorite_red_24dp);
                                holder.itemsQuantity.setVisibility(View.GONE);
                                holder.mLayoutItemCart2.setVisibility(View.VISIBLE);
                                MainActivity.notificationCountCart--;
//                                NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
                                holder.res = false;
                                Toast toast = null;
                                if (toast!= null) {
                                    toast.cancel();
                                }
                                toast = Toast.makeText(mContext, "Item added to wishlist.", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast toast = null;
                                if (toast!= null) {
                                    toast.cancel();
                                }
                                toast = Toast.makeText(mContext, "This Product is already in card.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    } else {
                        RequestParams params = new RequestParams();
                        params.put("product", parent.getPk());
                        params.put("qty", "0");
                        params.put("typ", "favourite");
                        params.put("user", parent.getUser());
                        client.post(BackendServer.url + "/api/ecommerce/cart/", params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                holder.mImageViewWishlist.setImageResource(R.drawable.ic_favorite_border_green_24dp);
                                holder.mLayoutItemCart2.setVisibility(View.VISIBLE);
                                holder.itemsQuantity.setVisibility(View.GONE);
                                holder.res = true;
                                if (toast!= null) {
                                    toast.cancel();
                                }
                                toast = Toast.makeText(mContext, "Item removed from wishlist.", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(mContext, "removing failure"+ parent.getPk(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void updateItem(String qty, final ListingParent parent, ViewHolder holder) {
            RequestParams params = new RequestParams();
            params.put("product", parent.getPk());
            params.put("qty", qty);
            params.put("typ", "cart");
            params.put("user", MainActivity.userPK);
            client.post(BackendServer.url + "/api/ecommerce/cart/", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(mContext, "updated cart"+ parent.getPk(), Toast.LENGTH_SHORT).show();
                    holder.itemsQuantity.setText(qty+"");
//                    mActivity.startActivity(new Intent(mActivity, CartListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(mContext, "failure cart"+ parent.getPk(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
