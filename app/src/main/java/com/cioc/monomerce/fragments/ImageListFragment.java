/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cioc.monomerce.fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.R;
import com.cioc.monomerce.entites.Cart;
import com.cioc.monomerce.entites.GenericProduct;
import com.cioc.monomerce.entites.ListingParent;
import com.cioc.monomerce.notification.NotificationCountSetClass;
import com.cioc.monomerce.options.CartListActivity;
import com.cioc.monomerce.options.WishlistActivity;
import com.cioc.monomerce.product.ItemDetailsActivity;
import com.cioc.monomerce.startup.MainActivity;
import com.cioc.monomerce.utility.ImageUrlUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

import static com.cioc.monomerce.options.NewAddressActivity.mContext;

public class ImageListFragment extends Fragment {
    public static final String STRING_IMAGE_URI = "ImageUri";
    public static final String STRING_IMAGE_POSITION = "ImagePosition";
    private static MainActivity mActivity;
    TextView moreItems;
    ProgressBar progressBar;
    public static ArrayList<ListingParent> listingParents;
    AsyncHttpClient client;
    String pk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        BackendServer backendServer = new BackendServer(mActivity);
        client = backendServer.getHTTPClient();
        listingParents = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_recylerview_list, container, false);
        clickBtn(v);
        return v;
    }

    public void clickBtn(View view){
        RecyclerView recyclerViewList = view.findViewById(R.id.recyclerview_list);
        moreItems = view.findViewById(R.id.more_items);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        setupRecyclerView(recyclerViewList);
    }

//    public ArrayList<ListingParent> items = null;
    String fragmentName = "";
    private void setupRecyclerView(final RecyclerView recyclerView) {
        for (int i=0; i<MainActivity.genericProducts.size(); i++) {
            if (ImageListFragment.this.getArguments().getInt("type") == i + 1) {
                GenericProduct product = MainActivity.genericProducts.get(i);
                pk = product.getPk();
                listingParents.clear();
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                moreItems.setVisibility(View.GONE);
                if (ImageListFragment.this.getArguments().getString("pk").equals(pk)) {
                    Log.e("pk", "" + pk);
                    getItems(pk);
//                    items = listingParents;
                    fragmentName = product.getName();


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (listingParents.size() == 0) {
                        progressBar.setVisibility(View.VISIBLE);
                        moreItems.setVisibility(View.GONE);
                    } else if (listingParents.size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);
                        CategoriesRecyclerViewAdapter viewAdapter = new CategoriesRecyclerViewAdapter(listingParents, fragmentName);
                        recyclerView.setAdapter(viewAdapter);
                        viewAdapter.notifyDataSetChanged();
                    }
                    if (listingParents.size() > 10) {
                        moreItems.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        moreItems.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getContext().startActivity(new Intent(getContext(), AllItemsShowActivity.class)
                                        .putExtra("pk", pk)
                                        .putExtra("fragmentName", fragmentName.toUpperCase()));
                            }
                        });
                    } else
                        moreItems.setVisibility(View.GONE);
                }
            }, 3 * 1000);
                }
            }
        }
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
                        listingParents.add(parent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(mActivity, "onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class CategoriesRecyclerViewAdapter
            extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.ViewHolder> {
        BackendServer backendServer = new BackendServer(mActivity);
        AsyncHttpClient client = backendServer.getHTTPClient();
        private ArrayList<ListingParent> mValues;
        String fname;
        Toast toast;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem, mLayoutItemCart2;//, mLayoutItemCart2;
            public final ImageView mImageViewWishlist, mCartImageBtn;//itemsQuantityAdd, itemsQuantityRemove,
            TextView itemName, itemPrice, itemDiscount, itemDiscountPrice, itemsQuantity;
            boolean res = true;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image1);
                mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item);
//                mLayoutItemCart1 = (LinearLayout) view.findViewById(R.id.layout_action1_cart);
                mLayoutItemCart2 = (LinearLayout) view.findViewById(R.id.layout_action2_cart);
                mImageViewWishlist = (ImageView) view.findViewById(R.id.ic_wishlist);
                itemName =  view.findViewById(R.id.item_name);
                itemPrice =  view.findViewById(R.id.item_price);
                itemDiscountPrice =  view.findViewById(R.id.actual_price);
                itemDiscount =  view.findViewById(R.id.discount_percentage);
                itemsQuantity =  view.findViewById(R.id.item_added);
//                itemsQuantityAdd =  view.findViewById(R.id.items_quantity_add);
//                itemsQuantityRemove =  view.findViewById(R.id.items_quantity_remove);
                mCartImageBtn =  view.findViewById(R.id.card_item_quantity_add);
            }
        }

        public CategoriesRecyclerViewAdapter(ArrayList<ListingParent> items, String name) {
            mValues = items;
            fname = name;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return mValues.size()>10 ? 10 : mValues.size();
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final ListingParent parent = mValues.get(position);
            final Uri uri;
            String qunt = parent.getAddedCart();
            int qntAdd = Integer.parseInt(qunt);
            if (qntAdd==0){
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
                                if (toast!= null) {
                                    toast.cancel();
                                }
                                toast = Toast.makeText(mActivity, "Item added to cart.", Toast.LENGTH_SHORT);
                                toast.show();
                                MainActivity.notificationCountCart++;
                                NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast toast = null;
                                if (toast!= null) {
                                    toast.cancel();
                                }
                                toast = Toast.makeText(mActivity, "This Product is already in card.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                });
            } else {
                holder.itemsQuantity.setVisibility(View.VISIBLE);
                holder.mLayoutItemCart2.setVisibility(View.GONE);
            }

            if (parent.getFilesAttachment().equals("null")){
                uri = Uri.parse(BackendServer.url+"/static/images/ecommerce.jpg");
            } else uri = Uri.parse(parent.getFilesAttachment());
            holder.mImageView.setImageURI(uri);
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
                    Intent intent = new Intent(mActivity, ItemDetailsActivity.class);
                    intent.putExtra(STRING_IMAGE_URI, parent.getFilesAttachment());
                    intent.putExtra(STRING_IMAGE_POSITION, position);
                    intent.putExtra("listingLitePk", parent.getPk());
                    mActivity.startActivity(intent);
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
                        params.put("qty", 1);
                        params.put("typ", "favourite");
                        params.put("user", parent.getUser());
                        client.post(BackendServer.url + "/api/ecommerce/cart/", params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                holder.mImageViewWishlist.setImageResource(R.drawable.ic_favorite_red_24dp);
                                holder.itemsQuantity.setVisibility(View.GONE);
                                holder.mLayoutItemCart2.setVisibility(View.VISIBLE);
                                MainActivity.notificationCountCart--;
                                NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
                                holder.res = false;
                                Toast toast = null;
                                if (toast!= null) {
                                    toast.cancel();
                                }
                                toast = Toast.makeText(mActivity, "Item added to wishlist.", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast toast = null;
                                if (toast!= null) {
                                    toast.cancel();
                                }
                                toast = Toast.makeText(mActivity, "This Product is already in card.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    } else {
                        RequestParams params = new RequestParams();
                        params.put("product", parent.getPk());
                        params.put("qty", 0);
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
                                toast = Toast.makeText(mActivity, "Item removed from wishlist.", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                if (toast!= null) {
                                    toast.cancel();
                                }
                                toast = Toast.makeText(mActivity, "Removing failure", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }
            });
        }


        public void updateItem(String qty, final ListingParent parent, ViewHolder holder) {
            RequestParams params = new RequestParams();
            params.put("product", parent.getPk());
            params.put("qty", qty);
            params.put("typ", "cart");
            params.put("user", parent.getUser());
            client.post(BackendServer.url + "/api/ecommerce/cart/", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(mActivity, "updated cart"+ parent.getPk(), Toast.LENGTH_SHORT).show();
                    holder.itemsQuantity.setText(qty+"");
//                    mActivity.startActivity(new Intent(mActivity, CartListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(mActivity, "failure cart"+ parent.getPk(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
