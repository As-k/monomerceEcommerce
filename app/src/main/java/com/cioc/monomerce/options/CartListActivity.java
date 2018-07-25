package com.cioc.monomerce.options;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.cioc.monomerce.startup.MainActivity;
import com.cioc.monomerce.utility.ImageUrlUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.githang.stepview.StepView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cioc.monomerce.fragments.ImageListFragment.STRING_IMAGE_POSITION;
import static com.cioc.monomerce.fragments.ImageListFragment.STRING_IMAGE_URI;


public class CartListActivity extends AppCompatActivity {
    private static Context mContext;
    TextView checkOutAction;
    private static StepView mStepView;
    public static LinearLayout layoutCartItems, layoutCartPayments, layoutCartNoItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        mContext = CartListActivity.this;

        ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
        ArrayList<String> cartlistImageUri = imageUrlUtils.getCartListImageUri();
        //Show cart layout based on items
        setCartLayout();


        List<String> steps = Arrays.asList(new String[]{"Selected Items", "Shipping Address", "Review Your Order"});
        mStepView.setSteps(steps);
        mStepView.selectedStep(1);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager recylerViewLayoutManager = new LinearLayoutManager(mContext);

        recyclerView.setLayoutManager(recylerViewLayoutManager);
        recyclerView.setAdapter(new CartListActivity.SimpleStringRecyclerViewAdapter(recyclerView, cartlistImageUri));
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder> {

        private ArrayList<String> mCartlistImageUri;
        private RecyclerView mRecyclerView;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem, mLayoutRemove;
            TextView actualPrice, discountPercentage, itemsQuantity;
            ImageView itemsQuantityAdd, itemsQuantityRemove, cardWishList;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image_cartlist);
                mLayoutItem =  view.findViewById(R.id.layout_item_desc);
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

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<String> wishlistImageUri) {
            mCartlistImageUri = wishlistImageUri;
            mRecyclerView = recyclerView;
        }

        @Override
        public CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cartlist_item, parent, false);
            return new CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder holder, final int position) {
            final Uri uri = Uri.parse(mCartlistImageUri.get(position));
            holder.mImageView.setImageURI(uri);
            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ItemDetailsActivity.class);
                    intent.putExtra(STRING_IMAGE_URI,mCartlistImageUri.get(position));
                    intent.putExtra(STRING_IMAGE_POSITION, position);
                    mContext.startActivity(intent);
                }
            });

           //Set click action
            holder.mLayoutRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
                    imageUrlUtils.removeCartListImageUri(position);
                    notifyDataSetChanged();
                    //Decrease notification count
                    MainActivity.notificationCountCart--;
                    if (MainActivity.notificationCountCart==0) {
                        layoutCartNoItems.setVisibility(View.VISIBLE);
                        layoutCartItems.setVisibility(View.GONE);
                        layoutCartPayments.setVisibility(View.GONE);
                        mStepView.setVisibility(View.GONE);
                    }

                }
            });


            holder.cardWishList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
                    imageUrlUtils.addWishlistImageUri(mCartlistImageUri.get(position));
                    holder.cardWishList.setImageResource(R.drawable.ic_favorite_black_18dp);
                    notifyDataSetChanged();
                    Toast.makeText(mContext,"Item added to wishlist.", Toast.LENGTH_SHORT).show();
                }
            });

            //Set click action
            holder.itemsQuantityRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String quan = holder.itemsQuantity.getText().toString();
                    int quantRemove = Integer.parseInt(quan);
                    if (quantRemove<=1) {
                        ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
                        imageUrlUtils.removeCartListImageUri(position);
                        notifyDataSetChanged();
                        //Decrease notification count
                        MainActivity.notificationCountCart--;
                        layoutCartNoItems.setVisibility(View.VISIBLE);
                        layoutCartItems.setVisibility(View.GONE);
                        layoutCartPayments.setVisibility(View.GONE);
                        mStepView.setVisibility(View.GONE);
                    } else
                        quantRemove--;
                    holder.itemsQuantity.setText(quantRemove+"");
                }
            });

            holder.itemsQuantityAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String quan = holder.itemsQuantity.getText().toString();
                    int quantAdd = Integer.parseInt(quan);
                    quantAdd++;
                    holder.itemsQuantity.setText(quantAdd+"");
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCartlistImageUri.size();
        }
    }

    protected void setCartLayout(){
        layoutCartItems = (LinearLayout) findViewById(R.id.layout_items);
        layoutCartPayments = (LinearLayout) findViewById(R.id.layout_payment);
        layoutCartNoItems = (LinearLayout) findViewById(R.id.layout_cart_empty);
        mStepView = (StepView) findViewById(R.id.step_view);
        checkOutAction = findViewById(R.id.text_action_bottom2);

        if (MainActivity.notificationCountCart >0) {
            layoutCartNoItems.setVisibility(View.GONE);
            layoutCartItems.setVisibility(View.VISIBLE);
            layoutCartPayments.setVisibility(View.VISIBLE);
            mStepView.setVisibility(View.VISIBLE);
            checkOutAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), CheckOutActivity.class));
                }
            });

        } else {
            layoutCartNoItems.setVisibility(View.VISIBLE);
            layoutCartItems.setVisibility(View.GONE);
            layoutCartPayments.setVisibility(View.GONE);
            mStepView.setVisibility(View.GONE);

            Button bStartShopping = (Button) findViewById(R.id.bAddNew);
            bStartShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }
}
