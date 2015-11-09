package com.favesolution.jktotw.Fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.NetworkUtils.RequestQueueSingleton;
import com.favesolution.jktotw.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Daniel on 11/9/2015 for JktOtw project.
 */
public class DirectionFragment extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks {
    private static final String ARG_PLACE = "arg_place";
    private Place mPlace;
    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private Location mCurrentLocation;
    public static DirectionFragment newInstance(Place place) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_PLACE, place);
        DirectionFragment fragment = new DirectionFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mPlace = getArguments().getParcelable(ARG_PLACE);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getString(R.string.direction_actiobar,mPlace.getName()));
        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                updateMap();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
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
    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mCurrentLocation = location;
                        updateMap();
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    private void updateMap() {
        if(mMap == null || mCurrentLocation == null)
            return;
        LatLng latLngUser = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLngUser).title(getString(R.string.you)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLngUser, 17);
        mMap.animateCamera(cameraUpdate);
    }
}
