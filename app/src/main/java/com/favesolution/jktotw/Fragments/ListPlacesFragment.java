package com.favesolution.jktotw.Fragments;


import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.favesolution.jktotw.Activities.DetailPlaceActivity;
import com.favesolution.jktotw.Helpers.CustomJsonRequest;
import com.favesolution.jktotw.Helpers.DisplayHelper;
import com.favesolution.jktotw.Helpers.DividerItemDecoration;
import com.favesolution.jktotw.Helpers.NetworkHelper;
import com.favesolution.jktotw.Helpers.RequestQueueSingleton;
import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListPlacesFragment extends Fragment {
    private static final String ARG_POSITION = "arg_position";
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Place> mPlaces = new ArrayList<>();
    private String mCategoryFilter;
    private GoogleApiClient mClient;
    private Location mCurrentLocation;
    public static ListPlacesFragment newInstance(int position) {
        ListPlacesFragment fragment = new ListPlacesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        int position = getArguments().getInt(ARG_POSITION);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getResources()
                .obtainTypedArray(R.array.categories)
                .getString(position) + " " + getString(R.string.near_you));
        TypedArray categoryFilterList = getResources().obtainTypedArray(R.array.category_filter);
        mCategoryFilter = categoryFilterList.getString(position);
        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        //mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
                        LocationRequest request = LocationRequest.create();
                        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        request.setNumUpdates(1);
                        request.setInterval(0);
                        LocationServices.FusedLocationApi
                                .requestLocationUpdates(mClient, request, new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                        if (mCurrentLocation == null) {
                                            mCurrentLocation = location;
                                            mSwipeRefreshLayout.setRefreshing(true);
                                            refreshPlace();
                                        } else {
                                            mCurrentLocation=location;
                                        }
                                    }
                                });
                    }
                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_places, container, false);
        ButterKnife.bind(this,v);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        setupAdapter();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_light,
                R.color.colorAccent,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPlace();
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_place,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_see_map:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
        RequestQueueSingleton.getInstance(getActivity()).getRequestQueue().cancelAll(this);
    }
    private void refreshPlace() {
        if (mCurrentLocation != null) {
            RequestQueueSingleton.getInstance(getActivity())
                    .getRequestQueue()
                    .cancelAll(this);
            final String url = NetworkHelper.searchNearbyPlace(mCurrentLocation, mCategoryFilter);
            CustomJsonRequest placeRequest = new CustomJsonRequest(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray result = response.getJSONArray("results");
                        mPlaces = Place.fromJson(result,mCurrentLocation);
                        clearAdapter();
                        setupAdapter();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                    Log.e("error",error.getMessage());
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
            placeRequest.setTag(this);
            RequestQueueSingleton.getInstance(getActivity())
                    .addToRequestQueue(placeRequest);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
    private void clearAdapter() {
        if (mRecyclerView!=null && mRecyclerView.getAdapter() != null) {
            ((PlacesAdapter)mRecyclerView.getAdapter()).clear();
        }
    }
    private void setupAdapter() {
        if(mRecyclerView.getAdapter() == null)
            mRecyclerView.setAdapter(new PlacesAdapter(mPlaces));
        else
            ((PlacesAdapter)mRecyclerView.getAdapter()).addAll(mPlaces);
    }
    private class PlacesHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView mNamePlaceText;
        private TextView mDistance;
        private Place mPlace;
        public PlacesHolder(View itemView) {
            super(itemView);
            mNamePlaceText = (TextView) itemView.findViewById(R.id.place_text);
            mDistance = (TextView) itemView.findViewById(R.id.text_distance);
            itemView.setOnClickListener(this);
        }
        public void bindPlacesItem(Place place) {
            mPlace = place;
            mNamePlaceText.setText(DisplayHelper.dotString(mPlace.getName()));
            int distance = (int) DisplayHelper.round(mPlace.getDistance(), 0);
            mDistance.setText(String.format("%d m", distance));
        }
        @Override
        public void onClick(View v) {
            startActivity(DetailPlaceActivity.newIntent(getActivity(),mPlace.getId(),mPlace.getName()));
        }
    }

    private class PlacesAdapter extends RecyclerView.Adapter<PlacesHolder> {
        private List<Place> mPlaceItems = new ArrayList<>();
        public PlacesAdapter(List<Place> places) {
            mPlaceItems = places;
        }
        @Override
        public PlacesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.list_places,parent,false);
            return new PlacesHolder(v);
        }

        @Override
        public void onBindViewHolder(PlacesHolder holder, int position) {
            Place place = mPlaceItems.get(position);
            holder.bindPlacesItem(place);
        }

        @Override
        public int getItemCount() {
            return mPlaceItems.size();
        }
        public void clear() {
            mPlaceItems.clear();
            notifyDataSetChanged();
        }
        public void addAll(List<Place> list) {
            mPlaceItems.addAll(list);
            notifyDataSetChanged();

        }
    }
}
