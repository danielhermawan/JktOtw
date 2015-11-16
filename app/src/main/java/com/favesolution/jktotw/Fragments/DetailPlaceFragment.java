package com.favesolution.jktotw.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.favesolution.jktotw.Activities.DirectionActivity;
import com.favesolution.jktotw.Activities.ListPlacesActivity;
import com.favesolution.jktotw.Activities.PhotoActivity;
import com.favesolution.jktotw.Activities.RelatedPlaceActivity;
import com.favesolution.jktotw.Adapters.PhotoAdapter;
import com.favesolution.jktotw.Dialogs.DialogConfirmation;
import com.favesolution.jktotw.Dialogs.DialogMessage;
import com.favesolution.jktotw.Dialogs.DialogShare;
import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.Models.Type;
import com.favesolution.jktotw.Networks.CustomJsonRequest;
import com.favesolution.jktotw.Networks.RequestQueueSingleton;
import com.favesolution.jktotw.Networks.UrlEndpoint;
import com.favesolution.jktotw.R;
import com.favesolution.jktotw.Utils.ImageHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import icepick.Icepick;
import icepick.State;

/**
 * Created by Daniel on 11/4/2015 for JktOtw project.
 */
public class DetailPlaceFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks {
    private static final String ARGS_PLACE = "args_place";
    private static final String TAG = "DetailPlaceFragment";
    private static final String DIALOG_CONFIMATION = "dialog_confirmation";
    private static final String DIALOG_MESSAGE = "dialog_message";
    private static final int REQUEST_DIALOG_CONFIMATION = 1;
    private GoogleApiClient mGoogleApiClient;
    @State Place mPlace;
    private GoogleMap mMap;
    private String mPlaceId;
    private List<Place> mRelatedPlaces = new ArrayList<>();
    @Bind(R.id.map_place) MapView mMapView;
    @Bind(R.id.text_search_nearby) TextView mTextSearchNearby;
    @Bind(R.id.text_name_place) TextView mTextNamePlace;
    @Bind(R.id.text_address_place) TextView mTextAddressPlace;
    @Bind(R.id.text_rating) TextView mTextRating;
    @Bind(R.id.rating_bar) RatingBar mRatingBar;
    @Bind(R.id.image_place) CircleImageView mImagePlace;
    @Bind(R.id.text_count_photo) TextView mTextCountPhoto;
    @Bind(R.id.text_photo) TextView mTextPhoto;
    @Bind(R.id.text_count_review) TextView mTextCountReview;
    @Bind(R.id.text_count_place) TextView mTextCountPlace;
    @Bind(R.id.text_review_number) TextView mTextReviewNumber;
    @Bind(R.id.text_related_place_number) TextView mTextRelatedPlaceNumber;
    @Bind(R.id.text_review) TextView mTextReview;
    @Bind(R.id.recyclerview_photo) RecyclerView mPhotoRecyclerView;
    @Bind(R.id.recyclerview_related_place) RecyclerView mRelatedRecyclerView;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.button_call) Button mButtonCall;
    @Bind(R.id.button_share) Button mButtonShare;
    @Bind(R.id.button_direction) Button mButtonDirection;
    @Bind(R.id.swipe_container) ScrollView mSwipeContainer;
    @Bind(R.id.content_photo) View mContentPhoto;
    @Bind(R.id.content_related) View mContentRelated;
    @Bind(R.id.see_all_photo) TextView mTextAllPhoto;
    @Bind(R.id.see_all_related_place) TextView mTextAllRelated;
    public static DetailPlaceFragment newInstance(Place place) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_PLACE, place);
        DetailPlaceFragment fragment = new DetailPlaceFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mPlace = getArguments().getParcelable(ARGS_PLACE);
        mPlaceId = mPlace.getId();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_place, container, false);
        ButterKnife.bind(this, v);
        setActionBarTitle(mPlace.getName());
        mImagePlace.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bitmap_placeholder));
        mTextPhoto.setText(getString(R.string.photos_number, 0));
        mTextReviewNumber.setText(getString(R.string.reviews_number, 0));
        mTextRelatedPlaceNumber.setText(getString(R.string.related_place_number,0));
        mTextCountPhoto.setText(0 + "");
        mTextCountReview.setText(0 + "");
        mTextCountPlace.setText(0 + "");
        mTextReview.setText(getString(R.string.text_review, 0));
        mButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if (mPlace.getPhoneNumber() != null) {
                    DialogConfirmation dialog = DialogConfirmation
                            .newInstance(getString(R.string.dialog_call, mPlace.getPhoneNumber()));
                    dialog.setTargetFragment(DetailPlaceFragment.this, REQUEST_DIALOG_CONFIMATION);
                    dialog.show(fm, DIALOG_CONFIMATION);
                } else {
                    DialogMessage dialog = DialogMessage
                            .newInstance(getString(R.string.no_number));
                    dialog.show(fm, DIALOG_MESSAGE);
                }
            }
        });
        mButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriImage = ImageHelper.getLocalBitmapUri(mImagePlace);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogShare dialog = DialogShare
                        .newInstance(getString(R.string.share_place, mPlace.getName()), uriImage);
                dialog.show(fm, DIALOG_CONFIMATION);
            }
        });
        mButtonDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(DirectionActivity.newInstance(getActivity(), mPlace));
            }
        });
        mContentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PhotoActivity.newIntent(getActivity(), mPlace));
            }
        });
        mTextAllPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PhotoActivity.newIntent(getActivity(), mPlace));
            }
        });
        mTextAllRelated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RelatedPlaceActivity.newIntent(mPlace,getActivity()));
            }
        });
        mContentRelated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RelatedPlaceActivity.newIntent(mPlace,getActivity()));
            }
        });
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mRelatedRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        mMapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());
        mMapView.setClickable(false);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setMapToolbarEnabled(false);
                if (mPlace != null)
                    updateMap();
            }
        });
        if (isIndosat()) {
            showProgressBar(false);
            updatePlace();
            final String url = UrlEndpoint.getHotspotPhoto(mPlace.getId());
            Log.d(TAG, url);
            CustomJsonRequest photoRequest = new CustomJsonRequest(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        List<String> photoref = new ArrayList<>();
                        for (int i = 0; i < results.length(); i++) {
                            photoref.add(results.getJSONObject(i).getString("link").replace("\\/", "/"));
                        }
                        mPlace.setPhotoRefs(photoref);
                        mPhotoRecyclerView.setAdapter(new PhotoAdapter(mPlace.getPhotoRefs()));
                        if (photoref.size() != 0) {
                            Glide.with(getActivity())
                                    .load(photoref.get(0))
                                    .error(R.drawable.bitmap_placeholder)
                                    .into(mImagePlace);
                        }
                        updatePhoto();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                    Log.e("error",error.getMessage());
                }
            });
            photoRequest.setTag(this);
            RequestQueueSingleton.getInstance(getActivity())
                    .addToRequestQueue(photoRequest);
        } else {
            final String url = UrlEndpoint.getDetailPlace(mPlace.getId());
            CustomJsonRequest placeDetailRequest = new CustomJsonRequest(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //mSwipeContainer.setRefreshing(false);
                    showProgressBar(false);
                    try {
                        JSONObject result = response.getJSONObject("result");
                        mPlace = Place.fromJsonDetail(result,getActivity());
                        mPhotoRecyclerView.setAdapter(new PhotoAdapter(mPlace.getPhotoRefs()));
                        updatePhoto();
                        updatePlace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //mSwipeContainer.setRefreshing(false);
                    showProgressBar(false);
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                    Log.e("error",error.getMessage());
                }
            });
            placeDetailRequest.setTag(this);
            RequestQueueSingleton.getInstance(getActivity())
                    .addToRequestQueue(placeDetailRequest);

        }
        return v;
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
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        RequestQueueSingleton.getInstance(getActivity()).getRequestQueue().cancelAll(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(final Location location) {
                        if (isIndosat()) {
                            final String url = UrlEndpoint.getHotspot();
                            CustomJsonRequest relatedPlaceRequest = new CustomJsonRequest(url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    JSONArray result = null;
                                    try {
                                        result = response.getJSONArray("results");
                                        mRelatedPlaces = Place.fromJsonHotspot(result, location, getActivity());
                                        updatePlace();
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
                        } else {
                            final String url = UrlEndpoint.searchNearbyPlace(mPlace.getLocation(), mPlace.getType().getCategoryFilter());
                            CustomJsonRequest relatedPlaceRequest = new CustomJsonRequest(url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONArray result = response.getJSONArray("results");
                                        mRelatedPlaces = Place.fromJson(result, location, getActivity());
                                        updateRelatedPlace();
                                        List<String> photoRefs = new ArrayList<String>();
                                        for (int i = 0; i < 4; i++) {
                                            if (mRelatedPlaces.get(i).getPhotoRef() != null) {
                                                photoRefs.add(mRelatedPlaces.get(i).getPhotoRef());
                                            } else {
                                                photoRefs.add("error");
                                            }
                                        }
                                        mRelatedRecyclerView.setAdapter(new PhotoAdapter(photoRefs));
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
                            relatedPlaceRequest.setTag(this);
                            RequestQueueSingleton.getInstance(getActivity())
                                    .addToRequestQueue(relatedPlaceRequest);


                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DIALOG_CONFIMATION) {
            if (data.getIntExtra(DialogConfirmation.EXTRA_CONFIRM,0) == 1) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + mPlace.getPhoneNumber()));
                startActivity(callIntent);
            }
        }
    }
    private void updatePhoto() {
        mTextCountPhoto.setText(mPlace.getPhotoRefs().size()+"");
        mTextPhoto.setText(getString(R.string.photos_number, mPlace.getPhotoRefs().size()));
    }
    private void updateRelatedPlace() {
        if (mRelatedPlaces.size() >= 20) {
            mTextRelatedPlaceNumber.setText(getString(R.string.related_place_number, "20+"));
            mTextCountPlace.setText("20+");
        } else {
            mTextRelatedPlaceNumber.setText(getString(R.string.related_place_number, mRelatedPlaces.size() + ""));
            mTextCountPhoto.setText(mRelatedPlaces.size() + "");
        }
    }
    private void showProgressBar(boolean isShow) {
        if (isShow) {
            mProgressBar.setVisibility(View.VISIBLE);
            mSwipeContainer.setVisibility(View.GONE);
        } else{
            mProgressBar.setVisibility(View.GONE);
            mSwipeContainer.setVisibility(View.VISIBLE);
        }
    }
    private void updatePlace() {
        mTextNamePlace.setText(mPlace.getName());
        mTextAddressPlace.setText(mPlace.getAddress());
        if (mPlace.getRating() > 0) {
            mTextRating.setText(String.format("%.1f", mPlace.getRating()));
            mRatingBar.setRating(mPlace.getRating());
        } else {
            mTextRating.setText(getString(R.string.no_rating));
            mRatingBar.setVisibility(View.GONE);
        }
        final Type type =  mPlace.getType();
        mTextSearchNearby.setText(Html.fromHtml(getString(R.string.search_place_nearby, type.getCategoryName())));
        mTextSearchNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getActivity().startActivity(ListPlacesActivity.newIntent(getActivity(), type).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        if (mMap!=null) {
            updateMap();
        }

    }
    private void updateMap() {
        LatLng latLng = new LatLng(mPlace.getLatitude(),mPlace.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(mPlace.getName()));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        mMap.animateCamera(cameraUpdate);
    }
    private void setActionBarTitle(String title) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(title);
    }

    private boolean isIndosat() {
        if(mPlace.getType().getCategoryName().equals(getString(R.string.category_indosat)))
            return true;
        else
            return false;
    }
}
