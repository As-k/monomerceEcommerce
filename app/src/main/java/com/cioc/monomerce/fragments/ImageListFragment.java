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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.cioc.monomerce.BackendServer;
import com.cioc.monomerce.R;
import com.cioc.monomerce.entites.GenericProduct;
import com.cioc.monomerce.entites.ListingParent;
import com.cioc.monomerce.product.ItemDetailsActivity;
import com.cioc.monomerce.startup.MainActivity;
import com.cioc.monomerce.utility.ImageUrlUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class ImageListFragment extends Fragment {

    public static final String STRING_IMAGE_URI = "ImageUri";
    public static final String STRING_IMAGE_POSITION = "ImagePosition";
    private static MainActivity mActivity;
    TextView moreItems;
    ProgressBar progressBar;
    ArrayList<GenericProduct> genericProducts = MainActivity.genericProducts;
    ArrayList<ListingParent> listingParents;
    AsyncHttpClient client;
    String pk;

//    Button sortBtn, filterBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        client = new AsyncHttpClient();
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



    ArrayList<ListingParent> items = null;
    String fragmentName = "";
    private void setupRecyclerView(final RecyclerView recyclerView) {
      /*  if (ImageListFragment.this.getArguments().getInt("type") == 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        } else if (ImageListFragment.this.getArguments().getInt("type") == 2) {
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        } else {
            GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), 3);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        }*/

        for (int i=0; i<genericProducts.size(); i++) {
            if (ImageListFragment.this.getArguments().getInt("type") == i+1) {
                GenericProduct product = genericProducts.get(i);
                pk = product.getPk();
                if (ImageListFragment.this.getArguments().getString("pk").equals(pk)) {
                    listingParents.clear();
                    getItems(pk);
                    progressBar.setVisibility(View.VISIBLE);
                    items = listingParents;
                    fragmentName = product.getName();
                }
            }
        }
//        if (ImageListFragment.this.getArguments().getInt("type") == 1){
//            items = ImageUrlUtils.getOffersUrls();
//            fragmentName = ImageListFragment.this.getArguments().getString("fragmentName");
//        }else if (ImageListFragment.this.getArguments().getInt("type") == 2){
//            items = ImageUrlUtils.getElectronicsUrls();
//            fragmentName = ImageListFragment.this.getArguments().getString("fragmentName");
//        }else if (ImageListFragment.this.getArguments().getInt("type") == 3){
//            items = ImageUrlUtils.getLifeStyleUrls();
//            fragmentName = ImageListFragment.this.getArguments().getString("fragmentName");
//        }else if (ImageListFragment.this.getArguments().getInt("type") == 4){
//            items = ImageUrlUtils.getHomeApplianceUrls();
//            fragmentName = ImageListFragment.this.getArguments().getString("fragmentName");
//        }else if (ImageListFragment.this.getArguments().getInt("type") == 5){
//            items =ImageUrlUtils.getBooksUrls();
//            fragmentName = ImageListFragment.this.getArguments().getString("fragmentName");
//        }else {
//            items = ImageUrlUtils.getImageUrls();
//            fragmentName = ImageListFragment.this.getArguments().getString("fragmentName");
//        }

        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          if (items.size()>10)
                                              moreItems.setVisibility(View.VISIBLE);
                                          else
                                              moreItems.setVisibility(View.GONE);
                                          progressBar.setVisibility(View.GONE);
                                          StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                                          recyclerView.setLayoutManager(layoutManager);
                                          SimpleStringRecyclerViewAdapter viewAdapter = new SimpleStringRecyclerViewAdapter(recyclerView, items, fragmentName);
                                          recyclerView.setAdapter(viewAdapter);
//                                          viewAdapter.notifyDataSetChanged();
                                      }
                                  },1000);


            moreItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getContext().startActivity(new Intent(getContext(), AllItemsShowActivity.class)
                            .putExtra("items", items)
                            .putExtra("fragmentName", fragmentName.toUpperCase()));
                }
            });
    }

    public void getItems(String pk) {
//        RequestParams params = new RequestParams();
//        params.put("parent", pk);
//        params.put("recursive", "1");
        client.get(BackendServer.url+"/api/ecommerce/listing/?parent="+pk+"&recursive=1", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                for (int i=0; i<response.length(); i++){
                    try {
//                        Toast.makeText(mActivity, "onSuccess", Toast.LENGTH_SHORT).show();
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




    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private ArrayList<ListingParent> mValues;
        private RecyclerView mRecyclerView;
        String fname;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem;
            public final ImageView mImageViewWishlist;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image1);
                mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item);
                mImageViewWishlist = (ImageView) view.findViewById(R.id.ic_wishlist);
            }
        }

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<ListingParent> items, String name) {
            mValues = items;
            fname = name;
            mRecyclerView = recyclerView;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
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
            /* FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.mImageView.getLayoutParams();
            if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                layoutParams.height = 200;
            } else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                layoutParams.height = 600;
            } else {
                layoutParams.height = 800;
            }*/

            final ListingParent parent = mValues.get(position);
            final Uri uri = Uri.parse(parent.getFilesAttachment());
            holder.mImageView.setImageURI(uri);
            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ItemDetailsActivity.class);
                    intent.putExtra(STRING_IMAGE_URI, parent.getFilesAttachment());
                    intent.putExtra(STRING_IMAGE_POSITION, position);
                    intent.putExtra("fragmentName", fname.toUpperCase());
                    mActivity.startActivity(intent);

                }
            });

            //Set click action for wishlist
//            holder.mImageViewWishlist.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
//                    imageUrlUtils.addWishlistImageUri(parent.filesAttachment);
//                    holder.mImageViewWishlist.setImageResource(R.drawable.ic_favorite_black_18dp);
//                    notifyDataSetChanged();
//                    Toast.makeText(mActivity,"Item added to wishlist.", Toast.LENGTH_SHORT).show();
//
//                }
//            });

        }

        @Override
        public int getItemCount() {
            return mValues.size()>10 ? 10 : mValues.size();
        }
    }
}
