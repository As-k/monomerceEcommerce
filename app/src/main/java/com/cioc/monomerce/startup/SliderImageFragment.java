package com.cioc.monomerce.startup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cioc.monomerce.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SliderImageFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section-icon";
    private static final String ARG_SECTION_COLOR = "section-color";

    public static SliderImageFragment newInstance(int color, int icon) {
        SliderImageFragment fragment = new SliderImageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, icon);
        args.putInt(ARG_SECTION_COLOR, color);
        fragment.setArguments(args);
        return fragment;
    }

    public SliderImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_slider_image, container, false);
        rootView.setBackgroundColor(ContextCompat.getColor(getContext(), getArguments().getInt(ARG_SECTION_COLOR)));
        ImageView image = (ImageView) rootView.findViewById(R.id.iv_icon);
        image.setImageResource(getArguments().getInt(ARG_SECTION_NUMBER));
        return rootView;

    }

}
