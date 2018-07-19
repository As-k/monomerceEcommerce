package com.cioc.monomerce.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.cioc.monomerce.R;
import com.cioc.monomerce.product.ItemDetailsActivity;
import com.cioc.monomerce.utility.ImageUrlUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.michaelbel.bottomsheet.BottomSheet;

import static com.cioc.monomerce.fragments.ImageListFragment.STRING_IMAGE_POSITION;
import static com.cioc.monomerce.fragments.ImageListFragment.STRING_IMAGE_URI;


public class AllItemsShowActivity extends AppCompatActivity {
    public Context context;
    Button sortBtn, filterBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items_show);
        context = AllItemsShowActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] stringArray = getIntent().getExtras().getStringArray("items");
        String name = getIntent().getExtras().getString("fragmentName");
        getSupportActionBar().setTitle(name);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_all_items);
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

        clickBtn(stringArray);
    }
    public void clickBtn(String[] strings){
        RecyclerView recyclerViewList = findViewById(R.id.recyclerview_all_list);
        sortBtn = findViewById(R.id.sort_action_button);
        filterBtn = findViewById(R.id.filter_action_button);
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
                startActivity(new Intent(context, FilterItemsActivity.class));
            }
        });

    }

    public void setupRecyclerView(RecyclerView recyclerView, String[] items) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new AllItemsShowActivity.AllItemsRecyclerViewAdapter(context, recyclerView, items));
    }

    public static class AllItemsRecyclerViewAdapter
            extends RecyclerView.Adapter<AllItemsShowActivity.AllItemsRecyclerViewAdapter.ViewHolder> {

        private String[] mValues;
        private RecyclerView mRecyclerView;
        Context mContext;

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

        public AllItemsRecyclerViewAdapter(Context context, RecyclerView recyclerView, String[] items) {
            mValues = items;
            mRecyclerView = recyclerView;
            this.mContext = context;
        }

        @Override
        public AllItemsShowActivity.AllItemsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new AllItemsShowActivity.AllItemsRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(AllItemsShowActivity.AllItemsRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final AllItemsShowActivity.AllItemsRecyclerViewAdapter.ViewHolder holder, final int position) {
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
                    Intent intent = new Intent(mContext, ItemDetailsActivity.class);
                    intent.putExtra(STRING_IMAGE_URI, mValues[position]);
                    intent.putExtra(STRING_IMAGE_POSITION, position);
                    mContext.startActivity(intent);

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
                    Toast.makeText(mContext,"Item added to wishlist.", Toast.LENGTH_SHORT).show();

                }
            });

        }

        @Override
        public int getItemCount() {
            return mValues.length;
        }
    }

}
