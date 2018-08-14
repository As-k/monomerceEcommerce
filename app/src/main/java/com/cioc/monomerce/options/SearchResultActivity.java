package com.cioc.monomerce.options;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cioc.monomerce.backend.BackendServer;
import com.cioc.monomerce.R;
import com.cioc.monomerce.entites.ProductMeta;
import com.cioc.monomerce.fragments.AllItemsShowActivity;
import com.cioc.monomerce.product.ItemDetailsActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class SearchResultActivity extends AppCompatActivity {
    AsyncHttpClient client;
    ArrayList<ProductMeta> productMetas;
    ListView searchList;
    TextView searchResult;
    Menu menu;
    ProductList productList;
    private String TAG = "SearchResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BackendServer backend = new BackendServer(SearchResultActivity.this);
        client = backend.getHTTPClient();
        productMetas = new ArrayList<>();

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

        searchResult = findViewById(R.id.search_result);
        searchList = findViewById(R.id.search_list);
//        handleIntent(getIntent());

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
                if(productMetas.size() != 0) {
                    searchResult.setVisibility(View.GONE);
                } else {
                    searchResult.setVisibility(View.VISIBLE);
                }
                productList = new ProductList();
                searchList.setAdapter(productList);
//            }
//        },1000);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.getItem(0);
        // Associate searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//                (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(true );
        searchItem.expandActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.toLowerCase();
                productMetas.clear();
                productList.clearData();
                client.get(BackendServer.url + "/api/ecommerce/searchProduct/?limit=10&search="+query, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                ProductMeta userMeta = new ProductMeta(object);
                                productMetas.add(userMeta);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("JSONObject", "Json parsing error: " + e.getMessage());
                            }
                        }
                        productList.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(SearchResultActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchResult.setVisibility(View.GONE);
                if(s.equals("")){
                    searchResult.setVisibility(View.VISIBLE);
                    productMetas.clear();
                    productList.clearData();
                }
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.i(TAG, "mSearchView on close ");
                // TODO Auto-generated method stub
                searchView.onActionViewCollapsed();
                finish();
                return false;
            }
        });
        return true;
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        handleIntent(intent);
//    }

//    private void handleIntent(Intent intent) {
//        finish();
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            //use the query to search your data somehow
//            productMetas.clear();
//            client.get(BackendServer.url + "/api/ecommerce/searchProduct/?limit=10&search="+query, new JsonHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                    super.onSuccess(statusCode, headers, response);
//                    for (int i = 0; i < response.length(); i++) {
//                        try {
//                            JSONObject object = response.getJSONObject(i);
//                            ProductMeta userMeta = new ProductMeta(object);
//                            productMetas.add(userMeta);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.e("JSONObject", "Json parsing error: " + e.getMessage());
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                    super.onFailure(statusCode, headers, throwable, errorResponse);
//                    Toast.makeText(SearchResultActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//        }
//    }

    private class ProductList extends BaseAdapter{
        @Override
        public int getCount() {
            return productMetas.size();
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
            View v = getLayoutInflater().inflate(R.layout.layout_search_result_list, viewGroup, false);
            TextView textResult = v.findViewById(R.id.text_result);

            final ProductMeta product = productMetas.get(position);
            textResult.setText(product.getName());
            textResult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String type = product.getType();
                    if (type.equals("generic")) {
                        startActivity(new Intent(getApplicationContext(), AllItemsShowActivity.class)
                                .putExtra("pk", product.getPk())
                                .putExtra("fragmentName", product.getName().toUpperCase()));

                    } else {
                        if (type.equals("list")) {
                            Intent intent = new Intent(getApplicationContext(), ItemDetailsActivity.class);
//                            intent.putExtra(STRING_IMAGE_URI, cart.getListingParent().getFilesAttachment());
//                            intent.putExtra(STRING_IMAGE_POSITION, position);
                            intent.putExtra("listingLitePk", product.getPk());
                            startActivity(intent);
                        }
                    }

                }
            });
            return v;
        }

        public void clearData() {
            productMetas.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.onActionViewCollapsed();
                finish();
                return false;
            }
        });
        super.onBackPressed();
        finish();
    }
}
