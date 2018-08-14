package com.cioc.monomerce.product;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.R;
import com.cioc.monomerce.entites.ListingLite;
import com.cioc.monomerce.fragments.ViewPagerActivity;
import com.cioc.monomerce.notification.NotificationCountSetClass;
import com.cioc.monomerce.options.CartListActivity;
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

import cz.msebera.android.httpclient.Header;

public class ItemDetailsActivity extends AppCompatActivity {
    SimpleDraweeView mImageView;
    TextView textViewItemName, textViewItemPrice, textViewItemDiscountPrice, textViewItemDiscount, textViewDescriptions, textViewAddToCart, textViewBuyNow;
    int imagePosition;
    String itemPk, stringImageUri;
//    RecyclerView specificationsListView;
    ListView specificationsListView;
    Context mContext;
    private Menu menu;
    AsyncHttpClient client;
    ArrayList<ListingLite> listingLites;
    public ListingLite lite;
    String name, value, fieldType, helpText, unit, jsonData;
    JSONArray jsonArray = new JSONArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        mContext = ItemDetailsActivity.this;
        BackendServer backend = new BackendServer(mContext);
        client = backend.getHTTPClient();
        listingLites = new ArrayList<>();

        //Getting image uri from previous screen
        if (getIntent() != null) {
            itemPk = getIntent().getStringExtra("listingLitePk");
//            itemName = getIntent().getStringExtra("itemName");
//            itemPrice = getIntent().getStringExtra("itemPrice");
//            itemDiscountPrice = getIntent().getStringExtra("itemDiscountPrice");
//            itemDiscount = getIntent().getStringExtra("itemDiscount");
//            stringImageUri = getIntent().getStringExtra(ImageListFragment.STRING_IMAGE_URI);
//            imagePosition = getIntent().getIntExtra(ImageListFragment.STRING_IMAGE_URI,0);
        }
        getParentType();

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final LinearLayout itemDetails = findViewById(R.id.layout_linear_item_details);
        progressBar.setVisibility(View.VISIBLE);
        itemDetails.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                itemDetails.setVisibility(View.VISIBLE);
                lite = listingLites.get(0);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
//                String name = getIntent().getExtras().getString(lite.getParentTypeName().toUpperCase());
                getSupportActionBar().setTitle(lite.getParentTypeName().toUpperCase());
                final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_items_detail);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        ItemDetailsActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
//                specificationsListView.setLayoutManager(new LinearLayoutManager(mContext));
                SpecificationsListAdapter list = new SpecificationsListAdapter();
                specificationsListView.setAdapter(list);
                list.notifyDataSetChanged();
            }
        },1000);


    }

    public void init() {
        mImageView = findViewById(R.id.image1);
        textViewItemName = findViewById(R.id.item_name);
        textViewItemPrice = findViewById(R.id.item_price);
        textViewItemDiscountPrice = findViewById(R.id.actual_price);
        textViewItemDiscount = findViewById(R.id.discount_percentage);
        textViewAddToCart = findViewById(R.id.text_action_bottom1);
        textViewBuyNow = findViewById(R.id.text_action_bottom2);
        specificationsListView = findViewById(R.id.specifications_list);
        textViewDescriptions = findViewById(R.id.description_txt);
        lite = listingLites.get(0);

        Uri uri = Uri.parse(lite.getFilesAttachment());
        mImageView.setImageURI(uri);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, ViewPagerActivity.class);
                intent.putExtra("position", imagePosition);
                startActivity(intent);
            }
        });


        textViewItemName.setText(lite.getProductName());
        if (lite.getProductDiscount().equals("0")){
            textViewItemPrice.setText("\u20B9"+lite.getProductPrice());
            textViewItemDiscount.setVisibility(View.GONE);
            textViewItemDiscountPrice.setVisibility(View.GONE);
        } else {
            textViewItemPrice.setText("\u20B9"+lite.getProductDiscountedPrice());
            textViewItemDiscount.setVisibility(View.VISIBLE);
            textViewItemDiscount.setText("("+lite.getProductDiscount()+"% OFF)");
            textViewItemDiscountPrice.setVisibility(View.VISIBLE);
            textViewItemDiscountPrice.setText("\u20B9"+lite.getProductPrice());
            textViewItemDiscountPrice.setPaintFlags(textViewItemDiscountPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }



        Spanned htmlAsSpanned = Html.fromHtml(lite.getSource());
        if (htmlAsSpanned.toString().equals("null")|| htmlAsSpanned.toString().equals("")|| htmlAsSpanned.toString() == null) {
            textViewDescriptions.setText("");
        } else {
            textViewDescriptions.setText("\u2022 " +htmlAsSpanned);
        }

        textViewAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams params = new RequestParams();
                params.put("product", lite.getPk());
                params.put("qty", "1");
                params.put("type", "card");
                params.put("user", "1");
                client.post(BackendServer.url + "/api/ecommerce/cart/", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(ItemDetailsActivity.this,"Item added to cart.", Toast.LENGTH_SHORT).show();
                        MainActivity.notificationCountCart++;
                        NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(mContext, "This Product is already in card.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        textViewBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams params = new RequestParams();
                params.put("product", lite.getPk());
                params.put("qty", "1");
                params.put("typ", "cart");
                params.put("user", "1");
                client.post(BackendServer.url + "/api/ecommerce/cart/", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
//                        imageUrlUtils.addCartListImageUri(stringImageUri);
                        Toast.makeText(ItemDetailsActivity.this,"Item added to cart.", Toast.LENGTH_SHORT).show();
                        MainActivity.notificationCountCart++;
                        NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
                        startActivity(new Intent(ItemDetailsActivity.this, CartListActivity.class));
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(mContext, "This Product is already in card.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    public void getParentType(){
        client.get(BackendServer.url+"/api/ecommerce/listingLite/"+itemPk+"/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                    try {
                        ListingLite lite = new ListingLite(response);
                        listingLites.add(lite);
                        String data = response.getString("specifications");
                        JSONArray dataJson = new JSONArray(data);
                        for (int j = 0; j < dataJson.length(); j++) {
                            JSONObject object = dataJson.getJSONObject(j);
                            name = object.getString("name");
                            value = object.getString("value");
                            fieldType = object.getString("fieldType");
                            helpText = object.getString("helpText");
                            unit = object.getString("unit");
                            jsonData = object.getString("data");
                            jsonArray.put(object);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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
            lite = listingLites.get(0);
            item.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
            RequestParams params = new RequestParams();
            params.put("product", lite.getPk());
            params.put("qty", "1");
            params.put("type", "favourite");
            params.put("user", "1");
            client.post(BackendServer.url + "/api/ecommerce/cart/", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(getApplicationContext(),"Item added to wishlist.", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(mContext, "This Product is already in wishlist.", Toast.LENGTH_SHORT).show();
                }
            });

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected class SpecificationsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return jsonArray == null ? 0 : jsonArray.length();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            TextView propertyName, propertyValue;
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = layoutInflater.inflate(R.layout.layout_specifications_style, viewGroup, false);
            propertyName = v.findViewById(R.id.property_name);
            propertyValue = v.findViewById(R.id.property_value);
            try {
                JSONObject obj = jsonArray.getJSONObject(position);
                propertyName.setText(obj.getString("name"));
                propertyValue.setText(obj.getString("value"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return v;
        }
    }


//    private class SpecificationsListAdapter extends RecyclerView.Adapter<SpecificationsListAdapter.MyHolder>{
//        Context context;
//        JSONArray array;
//
//        public SpecificationsListAdapter(Context context, JSONArray jsonArray) {
//            this.context = context;
//            this.array = jsonArray;
//        }
//
//        @NonNull
//        @Override
//        public SpecificationsListAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View v = layoutInflater.inflate(R.layout.layout_specifications_style, viewGroup, false);
//            SpecificationsListAdapter.MyHolder myHolder = new SpecificationsListAdapter.MyHolder(v);
//
//            return myHolder;
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {
//            try {
//                JSONObject obj = array.getJSONObject(position);
//                myHolder.propertyName.setText(obj.getString("name"));
//                myHolder.propertyValue.setText(obj.getString("value"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return array.length();
//        }
//
//        public class MyHolder extends RecyclerView.ViewHolder{
//            TextView propertyName, propertyValue;
//            public MyHolder(@NonNull View itemView) {
//                super(itemView);
//
//                propertyName = itemView.findViewById(R.id.property_name);
//                propertyValue = itemView.findViewById(R.id.property_value);
//
//            }
//        }
//    }


}
