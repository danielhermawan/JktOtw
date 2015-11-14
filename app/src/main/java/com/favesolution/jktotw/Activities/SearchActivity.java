package com.favesolution.jktotw.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.favesolution.jktotw.Adapters.SearchAdapter;
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

public class SearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    public static final String EXTRA_CATEGORY = "extra_category";
    private ArrayList<Place> mPlaces = new ArrayList<>();
    private GoogleApiClient mClient;
    private Location mCurrentLocation;
    private String mQuery;
    private String mFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UIHelper.showOverflowMenu(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        showProgressBar(true);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            mFilter = intent.getStringExtra(EXTRA_CATEGORY);
        }
        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

    }
    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            doSearch();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setQuery(mQuery, false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mClient.disconnect();
        RequestQueueSingleton.getInstance(this)
                .getRequestQueue()
                .cancelAll(this);
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
                        doSearch();
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    private void doSearch() {
        if (mCurrentLocation == null) {
            return;
        }
        RequestQueueSingleton.getInstance(this)
                .getRequestQueue()
                .cancelAll(this);
        final String url;
        if (mFilter == null || mFilter.equals("")) {
            url = UrlEndpoint.searchNearbyPlaceByKeyword(this, mCurrentLocation, mQuery);
        } else {
            url = UrlEndpoint.searchNearbyPlaceByKeyword(this, mCurrentLocation, mQuery,mFilter);
        }
        CustomJsonRequest searchRequest = new CustomJsonRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray result = response.getJSONArray("results");
                    mPlaces = Place.fromJson(result, mCurrentLocation,SearchActivity.this);
                    SearchActivity.this.startService(FetchAddressIntentService.newIntent(
                            SearchActivity.this,
                            mPlaces,
                            new ResultReceiver(new Handler()){
                                @Override
                                protected void onReceiveResult(int resultCode, Bundle resultData) {
                                    mPlaces = resultData.getParcelableArrayList(FetchAddressIntentService.RESULT_DATA);
                                    setupAdapter();
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
                Toast.makeText(SearchActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("error", error.getMessage());
            }
        });
        searchRequest.setTag(this);
        RequestQueueSingleton.getInstance(this)
                .addToRequestQueue(searchRequest);
    }
    private void setupAdapter() {
        mRecyclerView.setAdapter(new SearchAdapter(mPlaces, mClient));
    }

    private void showProgressBar(boolean isShow) {
        if (isShow) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else{
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
