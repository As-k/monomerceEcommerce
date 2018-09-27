package com.cioc.monomerce.product;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cioc.monomerce.R;
import com.facebook.drawee.view.SimpleDraweeView;

public class ProductImageFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section-icon";
    private static final String ARG_SECTION_COLOR = "section-color";


    public ProductImageFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProductImageFragment newInstance(int color, String icon) {
        ProductImageFragment fragment = new ProductImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_NUMBER, icon);
        args.putInt(ARG_SECTION_COLOR, color);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_product_image, container, false);
        rootView.setBackgroundColor(ContextCompat.getColor(getContext(), getArguments().getInt(ARG_SECTION_COLOR)));
        ImageView image =  rootView.findViewById(R.id.slider_image);
        Glide.with(getActivity())
                .load(getArguments().getString(ARG_SECTION_NUMBER))
                .into(image);
        return rootView;
    }



}
