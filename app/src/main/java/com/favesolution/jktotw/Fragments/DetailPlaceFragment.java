package com.favesolution.jktotw.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.favesolution.jktotw.R;

/**
 * Created by Daniel on 11/4/2015 for JktOtw project.
 */
public class DetailPlaceFragment extends Fragment {
    private static final String ARGS_PLACE_ID = "place_id";
    public static DetailPlaceFragment newInstance(String place_id) {
        Bundle args = new Bundle();
        args.putString(ARGS_PLACE_ID,place_id);
        DetailPlaceFragment fragment = new DetailPlaceFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        String placeId = getArguments().getString(ARGS_PLACE_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_place, container, false);
        return v;
    }
    private void setActionBarTitle(String title) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(title);
    }
}
