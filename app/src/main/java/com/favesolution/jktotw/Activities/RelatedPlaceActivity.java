package com.favesolution.jktotw.Activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.favesolution.jktotw.Adapters.SearchAdapter;
import com.favesolution.jktotw.Interfaces.OnLoadMoreListener;
import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.Networks.CustomJsonRequest;
import com.favesolution.jktotw.Networks.RequestQueueSingleton;
import com.favesolution.jktotw.Networks.UrlEndpoint;
import com.favesolution.jktotw.R;
import com.favesolution.jktotw.Services.FetchAddressIntentService;
import com.favesolution.jktotw.Utils.DividerItemDecoration;
import com.favesolution.jktotw.Utils.UIHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RelatedPlaceActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    private Place mPlace;
    private ArrayList<Place> mPlaces = new ArrayList<>();
    private SearchAdapter mAdapter;
    private static final String EXTRA_PLACE = "extra_place";
    private String mNextToken;
    private GoogleApiClient mClient;
    private Location mCurrentLocation;
    public static Intent newIntent(Place place, Context context) {
        Intent i = new Intent(context, RelatedPlaceActivity.class);
        i.putExtra(EXTRA_PLACE, place);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_place);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UIHelper.showOverflowMenu(this);
        mPlace = getIntent().getParcelableExtra(EXTRA_PLACE);
        setTitle(getString(R.string.related_place_title,mPlace.getName()));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        mAdapter = new SearchAdapter(mRecyclerView, mPlaces, new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMorePlace();
                    }
                }, 1000);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        showProgressBar(true);
        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
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
    protected void onStop() {
        super.onStop();
        mClient.disconnect();
        RequestQueueSingleton.getInstance(this)
                .getRequestQueue()
                .cancelAll(this);
    }

    private void loadPlace() {
        if (isIndosat()) {

        } else {
            final String url = UrlEndpoint.searchNearbyPlace(mPlace.getLocation(), mPlace.getType().getCategoryFilter());
            CustomJsonRequest relatedPlaceRequest = new CustomJsonRequest(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray result = response.getJSONArray("results");
                        if (response.has("next_page_token")) {
                            mNextToken = response.getString("next_page_token");
                        }
                        mPlaces = Place.fromJson(result, mCurrentLocation, RelatedPlaceActivity.this);
                        RelatedPlaceActivity.this.startService(FetchAddressIntentService.newIntent(
                                RelatedPlaceActivity.this,
                                mPlaces,
                                new ResultReceiver(new Handler()) {
                                    @Override
                                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                                        mPlaces = resultData.getParcelableArrayList(FetchAddressIntentService.RESULT_DATA);
                                        mAdapter.addItems(mPlaces);
                                        showProgressBar(false);
                                    }
                                }
                        ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RelatedPlaceActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    Log.e("error", error.getMessage());
                }
            });
            relatedPlaceRequest.setTag(this);
            RequestQueueSingleton.getInstance(this)
                    .addToRequestQueue(relatedPlaceRequest);
        }
    }

    private void loadMorePlace() {
        if (!mNextToken.equals("")) {
            final String url = UrlEndpoint.loadMorePlace(mNextToken);
            CustomJsonRequest loadMoreRequest = new CustomJsonRequest(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray result = null;
                    try {
                        result = response.getJSONArray("results");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mPlaces = Place.fromJson(result, mCurrentLocation,RelatedPlaceActivity.this);
                    if (response.has("next_page_token")) {
                        try {
                            mNextToken = response.getString("next_page_token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mNextToken = "";
                        mAdapter.stopLoading();
                    }
                    RelatedPlaceActivity.this.startService(FetchAddressIntentService.newIntent(
                            RelatedPlaceActivity.this,
                            mPlaces,
                            new ResultReceiver(new Handler()) {
                                @Override
                                protected void onReceiveResult(int resultCode, Bundle resultData) {
                                    mPlaces = resultData.getParcelableArrayList(FetchAddressIntentService.RESULT_DATA);
                                    mAdapter.addItems(mPlaces);
                                    mAdapter.removeItem(null);
                                }
                            }
                    ));
                    //mAdapter.addItems(Place.fromJson(result, mCurrentLocation, SearchActivity.this));

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RelatedPlaceActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    Log.e("error", error.getMessage());
                }
            });
            loadMoreRequest.setTag(this);
            RequestQueueSingleton.getInstance(RelatedPlaceActivity.this)
                    .addToRequestQueue(loadMoreRequest);
        } else {
            mAdapter.stopLoading();
        }
    }

    private void showProgressBar(boolean isShow) {
        if (isShow) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
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
                        loadPlace();
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    private boolean isIndosat() {
        if(mPlace.getType().getCategoryName().equals(getString(R.string.category_indosat)))
            return true;
        else
            return false;
    }
}
