package com.favesolution.jktotw.Fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.favesolution.jktotw.Activities.DetailPlaceActivity;
import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.Models.Type;
import com.favesolution.jktotw.Networks.CustomJsonRequest;
import com.favesolution.jktotw.Networks.RequestQueueSingleton;
import com.favesolution.jktotw.Networks.UrlEndpoint;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapPlaceFragment extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks {
    private static final String ARG_TYPE = "arg_type";
    private List<Place> mPlaces;
    private List<Marker> mMarkers = new ArrayList<>();
    private GoogleApiClient mClient;
    private Location mCurrentLocation;
    //private int mPosition;
    private Type mType;
    private Marker activeMarker;
    private int activeMarkerPosition;
    private GoogleMap mMap;
    public static MapPlaceFragment newInstance(Type type) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_TYPE, type);
        MapPlaceFragment fragment = new MapPlaceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mType = getArguments().getParcelable(ARG_TYPE);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(mType.getCategoryName() + " " + getString(R.string.near_you));
        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (marker.equals(activeMarker)) {
                            Place place = mPlaces.get(activeMarkerPosition);
                            startActivity(DetailPlaceActivity.newIntent(getActivity(),place.getId(),place.getName()));
                        }
                        for (int i = 0; i < mMarkers.size(); i++) {
                            if (marker.equals(mMarkers.get(i))) {
                                activeMarker = marker;
                                activeMarkerPosition = i;
                                break;
                            }
                        }
                        return false;
                    }
                });
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
                        if (mCurrentLocation == null) {
                            mCurrentLocation = location;
                            refreshPlace();
                        } else {
                            mCurrentLocation = location;
                        }
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
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLngUser, 15);
        mMap.animateCamera(cameraUpdate);
        if(mPlaces==null)
            return;
        BitmapDescriptor customMarker = BitmapDescriptorFactory
        .fromResource(mType.getCategoryIconMarker());
        for (Place place:mPlaces) {
            MarkerOptions marker = new MarkerOptions().position(place.getLatLng())
                    .title(place.getName()).icon(customMarker).snippet(place.getAddress());
            Marker m = mMap.addMarker(marker);
            mMarkers.add(m);
        }
    }
    private void refreshPlace() {
        RequestQueueSingleton.getInstance(getActivity())
                .getRequestQueue()
                .cancelAll(this);
        final String url = UrlEndpoint.searchNearbyPlace(mCurrentLocation, mType.getCategoryFilter());
        CustomJsonRequest placeRequest = new CustomJsonRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray result = response.getJSONArray("results");
                    mPlaces = Place.fromJson(result,mCurrentLocation);
                    updateMap();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                Log.e("error", error.getMessage());
            }
        });
        placeRequest.setTag(this);
        RequestQueueSingleton.getInstance(getActivity())
                .addToRequestQueue(placeRequest);
    }

}
