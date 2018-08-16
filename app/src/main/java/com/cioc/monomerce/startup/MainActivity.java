package com.cioc.monomerce.startup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.R;
import com.cioc.monomerce.entites.Cart;
import com.cioc.monomerce.entites.GenericProduct;
import com.cioc.monomerce.entites.OfferBanners;
import com.cioc.monomerce.fragments.ImageListFragment;
import com.cioc.monomerce.miscellaneous.EmptyActivity;
import com.cioc.monomerce.notification.NotificationCountSetClass;
import com.cioc.monomerce.options.CartListActivity;
import com.cioc.monomerce.options.FeedBackActivity;
import com.cioc.monomerce.options.HelpCenterActivity;
import com.cioc.monomerce.options.MyAccountActivity;
import com.cioc.monomerce.options.OrderActivity;
import com.cioc.monomerce.options.SearchResultActivity;
import com.cioc.monomerce.options.WishlistActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.merhold.extensiblepageindicator.ExtensiblePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static int notificationCountCart = 0;
    static ViewPager viewPager;
    static TabLayout tabLayout;
    BottomNavigationView navigationBottom;
    Context context;
    TextView userName;
    LinearLayout navHeadLayout;
    SimpleDraweeView userImage;
    private SliderImageFragmentAdapter mSliderImageFragmentAdapter;
    private ViewPager mViewPager;
    private ExtensiblePageIndicator extensiblePageIndicator;

    AsyncHttpClient client;

    public static ArrayList<GenericProduct> genericProducts;
    public static ArrayList<OfferBanners> offerBannersList;
    public static ArrayList<Cart> cartList;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = MainActivity.this;

        BackendServer backend = new BackendServer(context);
        client = backend.getHTTPClient();
        genericProducts = new ArrayList<GenericProduct>();
        offerBannersList = new ArrayList<OfferBanners>();
        cartList = new ArrayList<>();

        getGenericProduct();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(null);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_white_24dp, getApplicationContext().getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationBottom = findViewById(R.id.bottom_navigation);
        navigation();
//        View v =  navigationView.inflateHeaderView(R.layout.nav_header_main);
        View v =  navigationView.getHeaderView(0);
        navHeadLayout = v.findViewById(R.id.nav_head_ll);
        userName = v.findViewById(R.id.user_name);
        userImage = v.findViewById(R.id.user_image);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        initCollapsingToolbar();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                while (genericProducts.size()>0) {
                    notificationCountCart = cartList.size();
                    if (viewPager != null) {
                        getViewpagerFragment();
                        setupViewPager(viewPager);
                        tabLayout.setupWithViewPager(viewPager);
//                    }
                }
            }
        },2500);
        getUserDetails();

        navHeadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MyAccountActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        offerBannersList.clear();
//        getGenericProduct();
        invalidateOptionsMenu();
    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (exit) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Get the notifications MenuItem and
        // its LayerDrawable (layer-list)
        MenuItem item = menu.findItem(R.id.action_cart);
        NotificationCountSetClass.setAddToCart(MainActivity.this, item, notificationCountCart);
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
        if (id == R.id.action_search) {
            startActivity(new Intent(MainActivity.this, SearchResultActivity.class));
            return true;
        }else if (id == R.id.action_cart) {

           /* NotificationCountSetClass.setAddToCart(MainActivity.this, item, notificationCount);
            invalidateOptionsMenu();*/
            startActivity(new Intent(MainActivity.this, CartListActivity.class));

           /* notificationCount=0;//clear notification count
            invalidateOptionsMenu();*/
            return true;
        }else {
            startActivity(new Intent(MainActivity.this, EmptyActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }

    public void getGenericProduct() {
        client.get(BackendServer.url+"/api/ecommerce/genericProduct/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                for (int i=0; i<response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        GenericProduct product = new GenericProduct(object);
                        genericProducts.add(product);
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

        client.get(BackendServer.url+"/api/ecommerce/offerBanner/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                for (int i=0; i<response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        OfferBanners banners = new OfferBanners(object);
                        offerBannersList.add(banners);

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

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        for (int i=0; i<genericProducts.size(); i++) {
            ImageListFragment fragment = new ImageListFragment();
            Bundle bundle = new Bundle();
            GenericProduct product = genericProducts.get(i);
            bundle.putInt("type", i+1);
            bundle.putString("pk", product.getPk());
            fragment.setArguments(bundle);
            adapter.addFragment(fragment, product.getName());
        }
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 2);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_2));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 3);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_3));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 4);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_4));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 5);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_5));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 6);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_6));
        viewPager.setAdapter(adapter);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_item1) {
//            viewPager.setCurrentItem(0);
//        } else if (id == R.id.nav_item2) {
//            viewPager.setCurrentItem(1);
//        } else if (id == R.id.nav_item3) {
//            viewPager.setCurrentItem(2);
//        } else if (id == R.id.nav_item4) {
//            viewPager.setCurrentItem(3);
//        } else if (id == R.id.nav_item5) {
//            viewPager.setCurrentItem(4);
//        }else if (id == R.id.nav_item6) {
//            viewPager.setCurrentItem(5);
//        } else
        if (id == R.id.my_wishlist) {
            startActivity(new Intent(MainActivity.this, WishlistActivity.class));
        }else if (id == R.id.my_cart) {
            startActivity(new Intent(MainActivity.this, CartListActivity.class));
        }else if (id == R.id.my_account) {
            startActivity(new Intent(MainActivity.this, MyAccountActivity.class));
        }else if (id == R.id.help_center) {
                startActivity(new Intent(MainActivity.this, HelpCenterActivity.class));
        }else if (id == R.id.contact_us) {
                startActivity(new Intent(MainActivity.this, FeedBackActivity.class));
//        }else if (id == R.id.my_orders) {
//                startActivity(new Intent(MainActivity.this, OrderActivity.class));
        }else {
                startActivity(new Intent(MainActivity.this, EmptyActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navigation() {
        navigationBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_cart: {
                        startActivity(new Intent(MainActivity.this, CartListActivity.class));
                        return true;
                    }
                    case R.id.action_order: {
                        startActivity(new Intent(MainActivity.this, OrderActivity.class));
                        return true;
                    }
                    case R.id.action_support: {
                        startActivity(new Intent(MainActivity.this, FeedBackActivity.class));
                        return true;
                    }
                    case R.id.action_home: {
                        startActivity(new Intent(MainActivity.this, MainActivity.class));

                        return true;
                    }
                }
                return false;
            }
        });
    }

    void getUserDetails() {
        client.get(BackendServer.url + "/api/HR/users/?mode=mySelf&format=json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("MainActivity","onSuccess");
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONObject usrObj = response.getJSONObject(0);
//                    pk = usrObj.getInt("pk");
                    String username = usrObj.getString("username");
                    String firstName = usrObj.getString("first_name");
                    String lastName = usrObj.getString("last_name");
                    String email = usrObj.getString("email");
                    JSONObject profileObj = usrObj.getJSONObject("profile");

                    String dpLink = profileObj.getString("displayPicture");
                    if (dpLink.equals("null")||dpLink==null){
                        dpLink = BackendServer.url+"static/images/userIcon.png";
                    }
                    String mobile = profileObj.getString("mobile");

                    userName.setText(firstName+" "+lastName);
                    Uri uri = Uri.parse(dpLink);
                    userImage.setImageURI(uri);
//                    emailId.setText(email);
//                    if (!mobile.equals("null"))
//                        mobileNo.setText(mobile);

//                    String[] image = dpLink.split("/"); //Backend.serverUrl+"/media/HR/images/DP/"
//                    String dp = image[7];
//                    Log.e("image "+dpLink,""+dp);

//                    client.get(dpLink, new FileAsyncHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, File file) {
//                            // Do something with the file `response`
//
//                            FileOutputStream outputStream;
//                            try {
//                                file1 = new File(Environment.getExternalStorageDirectory()+"/Monomerce"+ "/" + dp);
//                                if (file1.exists())
//                                    file1.delete();
//                                outputStream = new FileOutputStream(file1);
//                                outputStream.write(dp.getBytes());
//                                outputStream.close();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            Log.e("image",""+file1.getAbsolutePath());
//                            Bitmap pp = BitmapFactory.decodeFile(file.getAbsolutePath());
//                            profileImage.setImageBitmap(pp);
//                        }
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers,Throwable e, File file) {
//                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                            Log.e("failure-image",""+file.getAbsolutePath());
//                            System.out.println("failure");
//                            System.out.println(statusCode);
//                        }
//                    });
                } catch (JSONException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("MainActivity","onFailure");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.e("MainActivity","onFinish");
            }
        });
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.slider_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
//                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    public void getViewpagerFragment() {
        extensiblePageIndicator = (ExtensiblePageIndicator) findViewById(R.id.flexibleIndicator);
        mSliderImageFragmentAdapter = new SliderImageFragmentAdapter(getSupportFragmentManager());
        for (int i=0; i<offerBannersList.size(); i++) {
            OfferBanners banners = offerBannersList.get(i);
            mSliderImageFragmentAdapter.addFragment(SliderImageFragment.newInstance(android.R.color.transparent, banners.getImage()));
        }
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSliderImageFragmentAdapter);
        extensiblePageIndicator.initViewPager(mViewPager);
        mViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "viewPager", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
