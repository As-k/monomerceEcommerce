package com.cioc.monomerce.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.cioc.monomerce.R;


public class FilterItemsActivity extends AppCompatActivity {
    public static String filterName = "";
    TextView category, brand, filterBy, size, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_items);

        category = findViewById(R.id.filter_category);
        brand = findViewById(R.id.filter_brand);
        filterBy = findViewById(R.id.filter_by);
        size = findViewById(R.id.filter_size);
        type = findViewById(R.id.filter_type);

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setBackgroundColor(Color.parseColor("#ffffff"));
                brand.setBackgroundColor(Color.parseColor("#f7f7f7"));
                filterBy.setBackgroundColor(Color.parseColor("#f7f7f7"));
                size.setBackgroundColor(Color.parseColor("#f7f7f7"));
                type.setBackgroundColor(Color.parseColor("#f7f7f7"));
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.filter_fragment, new FilterCategoryFragment(), "FilterCategoryFragment");
                filterName = "category";
//                Bundle bundle = new Bundle();
//                bundle.putString("filter", "category");
//                categoryFragment.setArguments(bundle);
//                ft.replace(R.id.filter_fragment, categoryFragment);
                ft.commit();
            }
        });

        brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setBackgroundColor(Color.parseColor("#f7f7f7"));
                brand.setBackgroundColor(Color.parseColor("#ffffff"));
                filterBy.setBackgroundColor(Color.parseColor("#f7f7f7"));
                size.setBackgroundColor(Color.parseColor("#f7f7f7"));
                type.setBackgroundColor(Color.parseColor("#f7f7f7"));
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.filter_fragment, new FilterCategoryFragment(), "FilterCategoryFragment");
                filterName = "brand";
//                Bundle bundle = new Bundle();
//                bundle.putString("filter", "brand");
//                categoryFragment.setArguments(bundle);
//                ft.replace(R.id.filter_fragment, categoryFragment);
                ft.commit();
            }
        });

        filterBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setBackgroundColor(Color.parseColor("#f7f7f7"));
                brand.setBackgroundColor(Color.parseColor("#f7f7f7"));
                filterBy.setBackgroundColor(Color.parseColor("#ffffff"));
                size.setBackgroundColor(Color.parseColor("#f7f7f7"));
                type.setBackgroundColor(Color.parseColor("#f7f7f7"));
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.filter_fragment, new FilterCategoryFragment(), "FilterCategoryFragment");
                filterName = "filterBy";
//                Bundle bundle = new Bundle();
//                bundle.putString("filter", "filterBy");
//                ft.replace(R.id.filter_fragment, categoryFragment);
//                categoryFragment.setArguments(bundle);
                ft.commit();
            }
        });

        size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setBackgroundColor(Color.parseColor("#f7f7f7"));
                brand.setBackgroundColor(Color.parseColor("#f7f7f7"));
                filterBy.setBackgroundColor(Color.parseColor("#f7f7f7"));
                size.setBackgroundColor(Color.parseColor("#ffffff"));
                type.setBackgroundColor(Color.parseColor("#f7f7f7"));
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.filter_fragment, new FilterCategoryFragment(), "FilterCategoryFragment");
                filterName = "size";
//                Bundle bundle = new Bundle();
//                bundle.putString("filter", "size");
//                categoryFragment.setArguments(bundle);
//                ft.replace(R.id.filter_fragment, categoryFragment);
                ft.commit();
            }
        });

        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setBackgroundColor(Color.parseColor("#f7f7f7"));
                brand.setBackgroundColor(Color.parseColor("#f7f7f7"));
                filterBy.setBackgroundColor(Color.parseColor("#f7f7f7"));
                size.setBackgroundColor(Color.parseColor("#f7f7f7"));
                type.setBackgroundColor(Color.parseColor("#ffffff"));
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.filter_fragment, new FilterCategoryFragment(), "FilterCategoryFragment");
                filterName = "type";
//                Bundle bundle = new Bundle();
//                bundle.putString("filter", "type");
//                categoryFragment.setArguments(bundle);
//                ft.replace(R.id.filter_fragment, categoryFragment);
                ft.commit();
            }
        });

    }

}
