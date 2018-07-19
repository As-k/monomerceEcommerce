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
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.cioc.monomerce.R;
import com.cioc.monomerce.product.ItemDetailsActivity;
import com.cioc.monomerce.startup.MainActivity;
import com.cioc.monomerce.utility.ImageUrlUtils;
import com.facebook.drawee.view.SimpleDraweeView;


public class ImageListFragment extends Fragment {

    public static final String STRING_IMAGE_URI = "ImageUri";
    public static final String STRING_IMAGE_POSITION = "ImagePosition";
    private static MainActivity mActivity;
    TextView moreItems;
//    Button sortBtn, filterBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
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
//        sortBtn = view.findViewById(R.id.sort_action_button);
//        filterBtn = view.findViewById(R.id.filter_action_button);
        setupRecyclerView(recyclerViewList);
//        sortBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final TextView sortNew, sortPopular, sortLowToHigh, sortHighToLow;
//                View lv = getLayoutInflater().inflate(R.layout.layout_sort_items, null, false);
//                sortNew = lv.findViewById(R.id.sort_new);
//                sortPopular = lv.findViewById(R.id.sort_popular);
//                sortLowToHigh = lv.findViewById(R.id.sort_lowToHigh);
//                sortHighToLow = lv.findViewById(R.id.sort_highToLow);
//
//                final BottomSheet.Builder bsb = new BottomSheet.Builder(getContext());
//                bsb.setView(lv);
//
//                sortNew.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        sortNew.setTextColor(Color.parseColor("#f2563e"));
//                        sortPopular.setTextColor(Color.parseColor("#213858"));
//                        sortLowToHigh.setTextColor(Color.parseColor("#213858"));
//                        sortHighToLow.setTextColor(Color.parseColor("#213858"));
//                        bsb.dismiss();
//                    }
//                });
//                sortPopular.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        sortNew.setTextColor(Color.parseColor("#213858"));
//                        sortPopular.setTextColor(Color.parseColor("#f2563e"));
//                        sortLowToHigh.setTextColor(Color.parseColor("#213858"));
//                        sortHighToLow.setTextColor(Color.parseColor("#213858"));
//                        bsb.dismiss();
//                    }
//                });
//                sortLowToHigh.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        sortNew.setTextColor(Color.parseColor("#213858"));
//                        sortPopular.setTextColor(Color.parseColor("#213858"));
//                        sortLowToHigh.setTextColor(Color.parseColor("#f2563e"));
//                        sortHighToLow.setTextColor(Color.parseColor("#213858"));
//                        bsb.dismiss();
//                    }
//                });
//                sortHighToLow.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        sortNew.setTextColor(Color.parseColor("#213858"));
//                        sortPopular.setTextColor(Color.parseColor("#213858"));
//                        sortLowToHigh.setTextColor(Color.parseColor("#213858"));
//                        sortHighToLow.setTextColor(Color.parseColor("#f2563e"));
//                        bsb.dismiss();
//                    }
//                });
//                bsb.show();
//            }
//        });
    }



    String[] items = null;
    String fragmentName = "";
    private void setupRecyclerView(RecyclerView recyclerView) {
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

        if (ImageListFragment.this.getArguments().getInt("type") == 1){
            items =ImageUrlUtils.getOffersUrls();
            fragmentName = getString(R.string.item_1);
        }else if (ImageListFragment.this.getArguments().getInt("type") == 2){
            items =ImageUrlUtils.getElectronicsUrls();
            fragmentName = getString(R.string.item_2);
        }else if (ImageListFragment.this.getArguments().getInt("type") == 3){
            items =ImageUrlUtils.getLifeStyleUrls();
            fragmentName = getString(R.string.item_3);
        }else if (ImageListFragment.this.getArguments().getInt("type") == 4){
            items = ImageUrlUtils.getHomeApplianceUrls();
            fragmentName = getString(R.string.item_4);
        }else if (ImageListFragment.this.getArguments().getInt("type") == 5){
            items =ImageUrlUtils.getBooksUrls();
            fragmentName = getString(R.string.item_5);
        }else {
            items = ImageUrlUtils.getImageUrls();
            fragmentName = getString(R.string.item_6);
        }
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(recyclerView, items));

        moreItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), AllItemsShowActivity.class)
                        .putExtra("items", items)
                        .putExtra("fragmentName", fragmentName));
            }
        });
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private String[] mValues;
        private RecyclerView mRecyclerView;

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

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, String[] items) {
            mValues = items;
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
            final Uri uri = Uri.parse(mValues[position]);
            holder.mImageView.setImageURI(uri);
            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ItemDetailsActivity.class);
                    intent.putExtra(STRING_IMAGE_URI, mValues[position]);
                    intent.putExtra(STRING_IMAGE_POSITION, position);
                    mActivity.startActivity(intent);

                }
            });

            //Set click action for wishlist
            holder.mImageViewWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
                    imageUrlUtils.addWishlistImageUri(mValues[position]);
                    holder.mImageViewWishlist.setImageResource(R.drawable.ic_favorite_black_18dp);
                    notifyDataSetChanged();
                    Toast.makeText(mActivity,"Item added to wishlist.", Toast.LENGTH_SHORT).show();

                }
            });

        }

        @Override
        public int getItemCount() {
            return mValues.length>10 ? 10 : mValues.length;
        }
    }
}
