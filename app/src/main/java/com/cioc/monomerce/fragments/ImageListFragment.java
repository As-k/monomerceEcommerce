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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.R;
import com.cioc.monomerce.entites.GenericProduct;
import com.cioc.monomerce.entites.ListingParent;
import com.cioc.monomerce.notification.NotificationCountSetClass;
import com.cioc.monomerce.product.ItemDetailsActivity;
import com.cioc.monomerce.startup.LoginPageActivity;
import com.cioc.monomerce.startup.MainActivity;
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


public class ImageListFragment extends Fragment {
//    public static final String STRING_IMAGE_URI = "ImageUri";
//    public static final String STRING_IMAGE_POSITION = "ImagePosition";
    private static MainActivity mActivity;
    TextView moreItems;
    ProgressBar progressBar;
    RecyclerView recyclerViewList;
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
        recyclerViewList = view.findViewById(R.id.recyclerview_list);
        moreItems = view.findViewById(R.id.more_items);
        progressBar = view.findViewById(R.id.progressBar);
        setRecycler(recyclerViewList);
    }

    String fragmentName = "";
    private void setRecycler(final RecyclerView recyclerView) {
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
                    fragmentName = product.getName();
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
                if (listingParents.size() <= 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    moreItems.setVisibility(View.GONE);
                } else if (listingParents.size() > 0) {
                    recyclerViewList.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    recyclerViewList.setLayoutManager(layoutManager);
                    CategoriesRecyclerViewAdapter viewAdapter = new CategoriesRecyclerViewAdapter(listingParents, fragmentName);
                    recyclerViewList.setAdapter(viewAdapter);
                    viewAdapter.notifyDataSetChanged();
                }
                if (listingParents.size() > 10) {
                    moreItems.setVisibility(View.VISIBLE);
                    recyclerViewList.setVisibility(View.VISIBLE);
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

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(mActivity, "onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static class CategoriesRecyclerViewAdapter
            extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.ViewHolder> {
        BackendServer backendServer = new BackendServer(mActivity);
        AsyncHttpClient client = backendServer.getHTTPClient();
        private ArrayList<ListingParent> mValues;
        String fname, sku;
        Toast toast;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final Spinner mItem;
            public final ImageView mImageView;
            public final LinearLayout mLayoutItem, mCart2;
            public final ImageView mWishlist, mCartBtn;
            TextView itemName, itemPrice, itemDiscount, itemDiscountPrice, itemsQuantity, itemsOut;
            boolean res = true;
            ArrayList spinnerlist = new ArrayList();
            String keys[] = {"str"};
            int ids[] = {R.id.weight_text};

            public ViewHolder(View view) {
                super(view);
                spinnerlist.clear();
                mImageView =  view.findViewById(R.id.image1);
                mLayoutItem = view.findViewById(R.id.layout_item);
                mCart2 = view.findViewById(R.id.layout_action2_cart);
                mWishlist = view.findViewById(R.id.ic_wishlist);
                itemName =  view.findViewById(R.id.item_name);
                mItem =  view.findViewById(R.id.item_variants_spinner);
                itemPrice =  view.findViewById(R.id.item_price);
                itemDiscountPrice =  view.findViewById(R.id.actual_price);
                itemDiscount =  view.findViewById(R.id.discount_percentage);
                itemsQuantity =  view.findViewById(R.id.item_added);
                itemsOut =  view.findViewById(R.id.out_of_stock);
                mCartBtn =  view.findViewById(R.id.card_item_quantity_add);
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
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final ListingParent parent = mValues.get(position);
            String link;
            if (parent.isInStock()) {
                holder.itemsOut.setVisibility(View.GONE);
                String qunt = parent.getAddedCart();
                int qntAdd = Integer.parseInt(qunt);
                if (qntAdd == 0) {
                    holder.mCart2.setVisibility(View.VISIBLE);
                    holder.itemsQuantity.setVisibility(View.GONE);
                    holder.mCartBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (MainActivity.username.equals("")) {
                                mActivity.startActivity(new Intent(mActivity, LoginPageActivity.class));
                            } else {
                                RequestParams params = new RequestParams();
                                params.put("prodSku", sku);
                                params.put("product", parent.getPk());
                                params.put("qty", "1");
                                params.put("typ", "cart");
                                params.put("user", MainActivity.userPK);
                                client.post(BackendServer.url + "/api/ecommerce/cart/", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        holder.mCart2.setVisibility(View.GONE);
                                        holder.itemsQuantity.setVisibility(View.VISIBLE);
                                        holder.mWishlist.setImageResource(R.drawable.ic_favorite_border_green_24dp);
                                        holder.res = true;
                                        if (toast != null) {
                                            toast.cancel();
                                        }
                                        toast = Toast.makeText(mActivity, "Item added to cart.", Toast.LENGTH_SHORT);
                                        toast.show();
                                        MainActivity.notificationCountCart++;
                                        NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        if (toast != null) {
                                            toast.cancel();
                                        }
                                        toast = Toast.makeText(mActivity, "This Product is already in card.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    holder.itemsQuantity.setVisibility(View.VISIBLE);
                    holder.mCart2.setVisibility(View.GONE);
                }
            } else {
                holder.itemsOut.setVisibility(View.VISIBLE);
                holder.itemsQuantity.setVisibility(View.GONE);
                holder.mCart2.setVisibility(View.GONE);
            }

            if (parent.getFilesAttachment().equals("null")){
                link = BackendServer.url+"/static/images/ecommerce.jpg";
            } else
                link = parent.getFilesAttachment();
            Glide.with(mActivity)
                    .load(link)
                    .into(holder.mImageView);

            Double d = Double.parseDouble(parent.getProductPrice());
            final int price = (int) Math.round(d);
            Double d1 = Double.parseDouble(parent.getProductDiscountedPrice());
            final int price1 = (int) Math.round(d1);

            holder.itemName.setText(parent.getProductName());
            String spinnerstr = parent.getHowMuch()+" "+parent.getUnit();

            if (parent.getProductDiscount().equals("0")){
                holder.itemPrice.setText("\u20B9"+ price);
                spinnerstr += " - \u20B9"+ price;
                holder.itemDiscountPrice.setVisibility(View.GONE);
                holder.itemDiscount.setVisibility(View.GONE);
            } else {
                holder.itemPrice.setText("\u20B9"+price1);
                holder.itemDiscountPrice.setVisibility(View.VISIBLE);
                holder.itemDiscountPrice.setText("\u20B9"+price);
                spinnerstr += " - \u20B9"+ price1;
                holder.itemDiscountPrice.setPaintFlags(holder.itemDiscountPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                holder.itemDiscount.setVisibility(View.VISIBLE);
                holder.itemDiscount.setText(parent.getProductDiscount()+"% OFF");
            }
            HashMap map = new HashMap();
            map.put(holder.keys[0], spinnerstr);
            map.put("sku", parent.getSerialNo());
            holder.spinnerlist.add(map);
            JSONArray array = parent.getItemArray();
            if (array.length()>0) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = array.getJSONObject(i);
                        String sku = jsonObj.getString("sku");
                        String updated = jsonObj.getString("updated");
                        String unitPerpack = jsonObj.getString("unitPerpack");
                        String created = jsonObj.getString("created");
                        String pricearray = jsonObj.getString("price");
                        String parent_id = jsonObj.getString("parent_id");
                        String id = jsonObj.getString("id");
                        Double rspoint = Double.parseDouble(pricearray);
                        final int rs = (int) Math.round(rspoint);
                        String  strvalue = (Double.parseDouble(parent.getHowMuch())*Integer.parseInt(unitPerpack))+" "+ parent.getUnit()+" - \u20B9"+ rs;
                        HashMap map1 = new HashMap();
                        map1.put(holder.keys[0], strvalue);
                        map1.put("sku", sku);
                        holder.spinnerlist.add(map1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MainActivity.username.equals("")) {
                        mActivity.startActivity(new Intent(mActivity, LoginPageActivity.class));
                    } else {
                        String itemPrice = holder.itemPrice.getText().toString();
                        Intent intent = new Intent(mActivity, ItemDetailsActivity.class);
                        intent.putExtra("listingLitePk", parent.getPk());
//                        intent.putExtra(STRING_IMAGE_URI, parent.getFilesAttachment());
//                        intent.putExtra(STRING_IMAGE_POSITION, position);
                        mActivity.startActivity(intent);
                    }
                }
            });

            SimpleAdapter adapter = new SimpleAdapter(mActivity, holder.spinnerlist, R.layout.layout_spinner_list, holder.keys,holder.ids);
            holder.mItem.setAdapter(adapter);

            holder.mItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    HashMap map = (HashMap) holder.spinnerlist.get(position);
                    String spinnerValue = (String) map.get(holder.keys[0]);
                    sku = (String) map.get("sku");
                    Log.e("onItemClick",(String) map.get("sku")+" "+spinnerValue);
                    String arrSplit[] = spinnerValue.split("-");
//                    if (parent.getProductDiscount().equals("0")){
                        holder.itemPrice.setText(arrSplit[1]);
//                        holder.itemDiscountPrice.setVisibility(View.GONE);
//                        holder.itemDiscount.setVisibility(View.GONE);
//                    } else {
//                        holder.itemPrice.setText("\u20B9"+price1);
//                        holder.itemDiscountPrice.setVisibility(View.VISIBLE);
//                        holder.itemDiscountPrice.setText("\u20B9"+price);
//                        holder.itemDiscountPrice.setPaintFlags(holder.itemDiscountPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
//                        holder.itemDiscount.setVisibility(View.VISIBLE);
//                        holder.itemDiscount.setText(parent.getProductDiscount()+"% OFF");
//                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            //Set click action for wishlist
            String quntWish = parent.getAddedWish();
            int qntWishAdd = Integer.parseInt(quntWish);
            if (qntWishAdd==0) {
                holder.mWishlist.setImageResource(R.drawable.ic_favorite_border_green_24dp);
                holder.res = true;
            }else {
                holder.mWishlist.setImageResource(R.drawable.ic_favorite_red_24dp);
                holder.res = false;
            }

            holder.mWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MainActivity.username.equals("")) {
                        mActivity.startActivity(new Intent(mActivity, LoginPageActivity.class));
                    } else {
                        if (holder.res) {
                            RequestParams params = new RequestParams();
                            params.put("product", parent.getPk());
                            params.put("qty", 1);
                            params.put("typ", "favourite");
                            params.put("user", MainActivity.userPK);
                            client.post(BackendServer.url + "/api/ecommerce/cart/", params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    holder.mWishlist.setImageResource(R.drawable.ic_favorite_red_24dp);
                                    holder.itemsQuantity.setVisibility(View.GONE);
                                    holder.mCart2.setVisibility(View.VISIBLE);
                                    MainActivity.notificationCountCart--;
                                    NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
                                    holder.res = false;
                                    if (toast != null) {
                                        toast.cancel();
                                    }
                                    toast = Toast.makeText(mActivity, "Item added to wishlist.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    Toast toast = null;
                                    if (toast != null) {
                                        toast.cancel();
                                    }
                                    toast = Toast.makeText(mActivity, "This Product is already in wishlist.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        } else {
                            RequestParams params = new RequestParams();
                            params.put("product", parent.getPk());
                            params.put("qty", 0);
                            params.put("typ", "favourite");
                            params.put("user", MainActivity.userPK);
                            client.post(BackendServer.url + "/api/ecommerce/cart/", params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    holder.mWishlist.setImageResource(R.drawable.ic_favorite_border_green_24dp);
                                    holder.mCart2.setVisibility(View.VISIBLE);
                                    holder.itemsQuantity.setVisibility(View.GONE);
                                    holder.res = true;
                                    if (toast != null) {
                                        toast.cancel();
                                    }
                                    toast = Toast.makeText(mActivity, "Item removed from wishlist.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    if (toast != null) {
                                        toast.cancel();
                                    }
                                    toast = Toast.makeText(mActivity, "Removing failure", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
                    }
                }
            });
        }
    }
}
