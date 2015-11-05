package com.favesolution.jktotw.Fragments;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.favesolution.jktotw.Adapter.PhotoAdapter;
import com.favesolution.jktotw.Helpers.PhotoTask;
import com.favesolution.jktotw.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Daniel on 11/4/2015 for JktOtw project.
 */
public class DetailPlaceFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks {
    private static final String ARGS_PLACE_ID = "place_id";
    private static final String ARGS_NAME = "place_name";
    private static final String TAG = "DetailPlaceFragment";
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private String mplaceId;
    private String mPlaceName;
    private Place mPlace;
    private String mPhone;
    private int mType;
    private GoogleMap mMap;
    @Bind(R.id.text_search_nearby) TextView mTextSearchNearby;
    @Bind(R.id.text_name_place) TextView mTextNamePlace;
    @Bind(R.id.text_address_place) TextView mTextAddressPlace;
    @Bind(R.id.text_rating) TextView mTextRating;
    @Bind(R.id.rating_bar) RatingBar mRatingBar;
    @Bind(R.id.image_place) CircleImageView mImagePlace;
    @Bind(R.id.text_count_photo) TextView mTextCountPhoto;
    @Bind(R.id.text_photo) TextView mTextPhoto;
    @Bind(R.id.recyclerview_photo) RecyclerView mPhotoRecyclerView;
    public static DetailPlaceFragment newInstance(String place_id,String placeName) {
        Bundle args = new Bundle();
        args.putString(ARGS_PLACE_ID,place_id);
        args.putString(ARGS_NAME,placeName);
        DetailPlaceFragment fragment = new DetailPlaceFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mplaceId = getArguments().getString(ARGS_PLACE_ID);
        mPlaceName = getArguments().getString(ARGS_NAME);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_place, container, false);
        ButterKnife.bind(this, v);
        setActionBarTitle(mPlaceName);
        mImagePlace.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bitmap_placeholder));
        mTextPhoto.setText(getString(R.string.photos_number, 0));
        mTextCountPhoto.setText(0 + "");
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        /*SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map_place);
        mMap = mapFragment.getMap();
       *//* mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (mPlace != null) {
                    updateMap();
                }
            }
        });*/
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, mplaceId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            mPlace = places.get(0);
                            updatePlace();
                        } else {
                            Log.e(TAG, "Place not found. Status " + places.getStatus().getStatusMessage());
                        }
                        places.release();
                    }
                });
        new PhotoTask(mImagePlace.getWidth(),mImagePlace.getHeight(),mGoogleApiClient,4)
                .setOnResultCallback(new PhotoTask.FinishLoadingAction() {
                    @Override
                    public void onResult(List<PhotoTask.AttributedPhoto> attributedPhotos) {
                        if (attributedPhotos != null && attributedPhotos.size()!=0) {
                            PhotoTask.AttributedPhoto firstPhoto = attributedPhotos.get(0);
                            mImagePlace.setImageBitmap(firstPhoto.bitmap);
                            mTextCountPhoto.setText(firstPhoto.totalPhoto + "");
                            mTextPhoto.setText(getString(R.string.photos_number,firstPhoto.totalPhoto));
                            mPhotoRecyclerView.setAdapter(new PhotoAdapter(attributedPhotos));
                            Log.d(TAG,mPhotoRecyclerView.getAdapter().getItemCount()+"");
                        }
                    }
                })
        .execute(mplaceId);
    }

    @Override
    public void onConnectionSuspended(int i) {

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
        mPhone = (String) mPlace.getPhoneNumber();
        List<Integer> types = mPlace.getPlaceTypes();
        mType = getType(types.get(0));//if lebih dari 1 maka....
        TypedArray categories = getResources().obtainTypedArray(R.array.categories);
        mTextSearchNearby.setText(Html.fromHtml(getString(R.string.search_place_nearby, categories.getString(mType))));
        if (mMap!=null) {
            updateMap();
        }

    }
    private void updateMap() {
        mMap.addMarker(new MarkerOptions().position(mPlace.getLatLng()));
    }
    private int getType(int type) {
        switch (type) {
            case Place.TYPE_LODGING:
                return 3;
            case Place.TYPE_FOOD:case Place.TYPE_RESTAURANT:
                return 0;
            case Place.TYPE_AMUSEMENT_PARK:case Place.TYPE_MOVIE_THEATER:
            case Place.TYPE_SHOPPING_MALL:case Place.TYPE_ZOO:
            case Place.TYPE_PARK:case Place.TYPE_BOWLING_ALLEY:
                return 1;
            case Place.TYPE_ATM:case Place.TYPE_BANK:
                return 2;
            case Place.TYPE_GROCERY_OR_SUPERMARKET:case Place.TYPE_CONVENIENCE_STORE:
            case Place.TYPE_DEPARTMENT_STORE:case Place.TYPE_STORE:
                return 4;
            case Place.TYPE_HOSPITAL:
                return 5;
        }
        return 1;
    }
    private void setActionBarTitle(String title) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(title);
    }
}
