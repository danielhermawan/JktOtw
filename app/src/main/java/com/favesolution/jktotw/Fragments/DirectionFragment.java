package com.favesolution.jktotw.Fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.Networks.CustomJsonRequest;
import com.favesolution.jktotw.Networks.RequestQueueSingleton;
import com.favesolution.jktotw.Networks.UrlEndpoint;
import com.favesolution.jktotw.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 11/9/2015 for JktOtw project.
 */
public class DirectionFragment extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks {
    private static final String ARG_PLACE = "arg_place";
    private Place mPlace;
    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private Location mCurrentLocation;
    private List<LatLng> mLatLngs;
    private Polyline mPolyline;
    private LatLngBounds mLatLngBounds;
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
                        getDirection();
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
        BitmapDescriptor customMarker = BitmapDescriptorFactory
                .fromResource(mPlace.getType().getCategoryIconMarker());
        MarkerOptions marker = new MarkerOptions().position(mPlace.getLatLng())
                .title(mPlace.getName()).icon(customMarker).snippet(mPlace.getAddress());
        mMap.addMarker(marker);
        if (mLatLngs == null)
            return;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLngBounds.getCenter(), 14));
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(mLatLngs);
        polylineOptions.width(20);
        polylineOptions.geodesic(true);
        polylineOptions.color(ContextCompat.getColor(getActivity(),R.color.colorAccent));
        mPolyline = mMap.addPolyline(polylineOptions);
    }
    private void getDirection() {
        RequestQueueSingleton.getInstance(getActivity())
                .getRequestQueue().cancelAll(this);
        final String url = UrlEndpoint.getDirectionUrl(mCurrentLocation,mPlace);
        Log.d("debug", url);
        CustomJsonRequest directionRequest = new CustomJsonRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray routes = response.getJSONArray("routes");
                    JSONObject firstRoute = routes.getJSONObject(0);
                    JSONObject bound = firstRoute.getJSONObject("bounds");
                    JSONObject northBound = bound.getJSONObject("northeast");
                    JSONObject southBound = bound.getJSONObject("southwest");
                    mLatLngBounds = new LatLngBounds(
                            new LatLng(southBound.getDouble("lat"),northBound.getDouble("lng")),
                            new LatLng(northBound.getDouble("lat"),northBound.getDouble("lng"))
                    );
                    JSONArray legs = firstRoute.getJSONArray("legs");
                    JSONObject firstLeg = legs.getJSONObject(0);
                    JSONArray steps = firstLeg.getJSONArray("steps");
                    mLatLngs = new ArrayList<>();
                    for (int i = 0; i < steps.length(); i++) {
                        JSONObject startLocation = steps.getJSONObject(i).getJSONObject("start_location");
                        double lat_start = startLocation.getDouble("lat");
                        double lng_start = startLocation.getDouble("lng");
                        mLatLngs.add(new LatLng(lat_start, lng_start));
                        if (i == steps.length() -1) {
                            JSONObject endLocation = steps.getJSONObject(i).getJSONObject("end_location");
                            double lat_end = endLocation.getDouble("lat");
                            double lng_end = endLocation.getDouble("lng");
                            mLatLngs.add(new LatLng(lat_end,lng_end));
                        }
                    }
                    updateMap();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                Log.e("DirectionFragment", error.getMessage());
            }
        });
        directionRequest.setTag(this);
        RequestQueueSingleton.getInstance(getActivity())
                .addToRequestQueue(directionRequest);
    }
}
