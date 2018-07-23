package com.cioc.monomerce.product;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.cioc.monomerce.R;
import com.cioc.monomerce.fragments.ImageListFragment;
import com.cioc.monomerce.fragments.ViewPagerActivity;
import com.cioc.monomerce.notification.NotificationCountSetClass;
import com.cioc.monomerce.options.CartListActivity;
import com.cioc.monomerce.options.WishlistActivity;
import com.cioc.monomerce.startup.MainActivity;
import com.cioc.monomerce.utility.ImageUrlUtils;
import com.facebook.drawee.view.SimpleDraweeView;

public class ItemDetailsActivity extends AppCompatActivity {
    int imagePosition;
    String itemName, itemPrice, itemDiscountPrice, itemDiscount, stringImageUri;
    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String name = getIntent().getExtras().getString("fragmentName");
        getSupportActionBar().setTitle(name);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_items_detail);
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



        SimpleDraweeView mImageView = (SimpleDraweeView)findViewById(R.id.image1);
        TextView textViewItemName = (TextView)findViewById(R.id.item_name);
        TextView textViewItemPrice = (TextView)findViewById(R.id.item_price);
        TextView textViewItemDiscountPrice = (TextView)findViewById(R.id.actual_price);
        TextView textViewItemDiscount = (TextView)findViewById(R.id.discount_percentage);
        TextView textViewAddToCart = (TextView)findViewById(R.id.text_action_bottom1);
        TextView textViewBuyNow = (TextView)findViewById(R.id.text_action_bottom2);

        //Getting image uri from previous screen
        if (getIntent() != null) {
            itemName = getIntent().getStringExtra("itemName");
            itemPrice = getIntent().getStringExtra("itemPrice");
            itemDiscountPrice = getIntent().getStringExtra("itemDiscountPrice");
            itemDiscount = getIntent().getStringExtra("itemDiscount");
            stringImageUri = getIntent().getStringExtra(ImageListFragment.STRING_IMAGE_URI);
            imagePosition = getIntent().getIntExtra(ImageListFragment.STRING_IMAGE_URI,0);
        }
        Uri uri = Uri.parse(stringImageUri);
        mImageView.setImageURI(uri);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(ItemDetailsActivity.this, ViewPagerActivity.class);
                    intent.putExtra("position", imagePosition);
                    startActivity(intent);

            }
        });

        textViewItemName.setText(itemName);
        if (itemDiscount.equals("0")){
            textViewItemPrice.setText("Rs. "+itemPrice);
            textViewItemDiscount.setVisibility(View.GONE);
            textViewItemDiscountPrice.setVisibility(View.GONE);
        } else {
            textViewItemPrice.setText("Rs. "+itemDiscountPrice);
            textViewItemDiscount.setVisibility(View.VISIBLE);
            textViewItemDiscount.setText("("+itemDiscount+" %)");
            textViewItemDiscountPrice.setVisibility(View.VISIBLE);
            textViewItemDiscountPrice.setText(itemPrice);
            textViewItemDiscountPrice.setPaintFlags(textViewItemDiscountPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }

        textViewAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
                imageUrlUtils.addCartListImageUri(stringImageUri);
                Toast.makeText(ItemDetailsActivity.this,"Item added to cart.", Toast.LENGTH_SHORT).show();
                MainActivity.notificationCountCart++;
                NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
            }
        });

        textViewBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
                imageUrlUtils.addCartListImageUri(stringImageUri);
                MainActivity.notificationCountCart++;
                NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
                startActivity(new Intent(ItemDetailsActivity.this, CartListActivity.class));

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Get the notifications MenuItem and
        // its LayerDrawable (layer-list)
        MenuItem item = menu.findItem(R.id.action_cart);
        NotificationCountSetClass.setAddToCart(ItemDetailsActivity.this, item, MainActivity.notificationCountCart);
        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        } else if (id == R.id.action_cart) {

           /* NotificationCountSetClass.setAddToCart(MainActivity.this, item, notificationCount);
            invalidateOptionsMenu();*/
            startActivity(new Intent(ItemDetailsActivity.this, CartListActivity.class));

           /* notificationCount=0;//clear notification count
            invalidateOptionsMenu();*/
            return true;
        }else if (id == R.id.action_wishlist) {
            ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
            imageUrlUtils.addWishlistImageUri(stringImageUri);
            item.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
            Toast.makeText(getApplicationContext(),"Item added to wishlist.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
