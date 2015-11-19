package com.favesolution.jktotw.Activities;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.plugin.radar.RadarView;
import com.beyondar.android.plugin.radar.RadarWorldPlugin;
import com.beyondar.android.util.ImageUtils;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.BeyondarObjectList;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.Networks.CustomJsonRequest;
import com.favesolution.jktotw.Networks.RequestQueueSingleton;
import com.favesolution.jktotw.Networks.UrlEndpoint;
import com.favesolution.jktotw.R;
import com.favesolution.jktotw.Utils.UIHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AugmentedActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {
    private BeyondarFragmentSupport mBeyondarFragment;
    private GoogleApiClient mClient;
    private Location mCurrentLocation;
    private List<Place> mPlaces = new ArrayList<>();
    private String mNextToken = "";
    private RadarView mRadarView;
    private static final String TMP_IMAGE_PREFIX = "viewImage_";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cleanTempFolder();
        setContentView(R.layout.activity_augmented);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UIHelper.showOverflowMenu(this);
        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(
                R.id.beyondar_fragment);
        mRadarView = (RadarView) findViewById(R.id.radarView);
        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_reload_more:
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reloadMorePlace();
                    }
                },1000);
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
        RequestQueueSingleton.getInstance(this).getRequestQueue().cancelAll(this);
    }
    private void setWorld() {
        //Set world
        World world = new World(this);
        world.setDefaultImage(R.drawable.bitmap_placeholder);
        world.setGeoPosition(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        //Set Radar
        RadarWorldPlugin radarWorldPlugin = new RadarWorldPlugin(this);
        radarWorldPlugin.setRadarView(mRadarView);
        radarWorldPlugin.setMaxDistance(300);
        radarWorldPlugin.setListDotRadius(1,3);
        world.addPlugin(radarWorldPlugin);
        //Set geo object
        for (int i = 0; i < mPlaces.size(); i++) {
            Place place = mPlaces.get(i);
            GeoObject geoObject = new GeoObject();
            geoObject.setGeoPosition(place.getLatitude(), place.getLongitude());
            geoObject.setName(place.getName());
            if (place.getPhotoRefs().size() != 0) {
                geoObject.setImageUri(place.getPhotoRefs().get(0));
            } else {
                geoObject.setImageResource(R.drawable.bitmap_default_placeholder_300x300);
            }
            world.addBeyondarObject(geoObject);
        }
        mBeyondarFragment.setWorld(world);
        //replaceImagesByStaticViews(world);
    }
    private void replaceImagesByStaticViews(World world) {
        String path = getTmpPath();

        for (BeyondarObjectList beyondarList : world.getBeyondarObjectLists()) {
            for (BeyondarObject beyondarObject : beyondarList) {
                // First let's get the view, inflate it and change some stuff
                View view = getLayoutInflater().inflate(R.layout.list_geo_object, null);
                TextView textView = (TextView) view.findViewById(R.id.place_name);
                textView.setText(beyondarObject.getName());
                try {
                    // Now that we have it we need to store this view in the
                    // storage in order to allow the framework to load it when
                    // it will be need it
                    String imageName = TMP_IMAGE_PREFIX + beyondarObject.getName() + ".png";
                    ImageUtils.storeView(view, path, imageName);

                    // If there are no errors we can tell the object to use the
                    // view that we just stored
                    beyondarObject.setImageUri(path + imageName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void reloadMorePlace() {
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
                    mPlaces.addAll(Place.fromJson(result, mCurrentLocation, AugmentedActivity.this));
                    setWorld();
                    if (response.has("next_page_token")) {
                        try {
                            mNextToken = response.getString("next_page_token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mNextToken = "";
                    }
                    Toast.makeText(AugmentedActivity.this,"Load more places successfully",Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AugmentedActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    Log.e("error", error.getMessage());
                }
            });
            loadMoreRequest.setTag(this);
            RequestQueueSingleton.getInstance(this)
                    .addToRequestQueue(loadMoreRequest);
        } else {
            Toast.makeText(AugmentedActivity.this,"Cannot load more places",Toast.LENGTH_SHORT).show();
        }

    }
    private void reloadPlace() {
        String url = UrlEndpoint.searchNearbyPlace(mCurrentLocation,"all",this);
        CustomJsonRequest placeRequest = new CustomJsonRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray result = response.getJSONArray("results");
                    mPlaces = Place.fromJson(result, mCurrentLocation, AugmentedActivity.this);
                    setWorld();
                    if (response.has("next_page_token")) {
                        try {
                            mNextToken = response.getString("next_page_token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mNextToken = "";
                    }
                    Toast.makeText(AugmentedActivity.this,"Data places successfully loaded",Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AugmentedActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("error", error.getMessage());
            }
        });
        placeRequest.setTag(this);
        RequestQueueSingleton.getInstance(this)
                .addToRequestQueue(placeRequest);
    }
    /** Clean all the generated files */
    private void cleanTempFolder() {
        File tmpFolder = new File(getTmpPath());
        if (tmpFolder.isDirectory()) {
            String[] children = tmpFolder.list();
            for (int i = 0; i < children.length; i++) {
                if (children[i].startsWith(TMP_IMAGE_PREFIX)) {
                    new File(tmpFolder, children[i]).delete();
                }
            }
        }
    }
    private String getTmpPath() {
        return getExternalFilesDir(null).getAbsoluteFile() + "/tmp/";
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
                            Toast.makeText(AugmentedActivity.this,"Fetch place data from server",Toast.LENGTH_SHORT).show();
                            reloadPlace();
                        } else {
                            mCurrentLocation = location;
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
