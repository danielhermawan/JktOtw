package com.favesolution.jktotw.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.favesolution.jktotw.Adapters.PhotoDetailAdapter;
import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.Models.User;
import com.favesolution.jktotw.Networks.UrlEndpoint;
import com.favesolution.jktotw.R;
import com.favesolution.jktotw.Utils.ImageHelper;
import com.favesolution.jktotw.Utils.SharedPreference;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import icepick.Icepick;
import icepick.State;

/**
 * A placeholder fragment containing a simple view.
 */
public class PhotoFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks {
    private static final String ARGS_PLACE = "args_place";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Place mPlace;
    @State File mPhotoFile;
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    @Bind(R.id.button_add_photo) Button mButtonAddPhoto;
    @Bind(R.id.text_placeholder) TextView mTextPlaceholder;
    public static PhotoFragment newInstance(Place place) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_PLACE, place);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Icepick.restoreInstanceState(this, savedInstanceState);
        mPlace = getArguments().getParcelable(ARGS_PLACE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo, container, false);
        ButterKnife.bind(this, v);
        setActionBarTitle(mPlace.getName());
        if (!User.checkIsLogin(getActivity())) {
            mButtonAddPhoto.setVisibility(View.GONE);
        }
        mButtonAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    try {
                        mPhotoFile = ImageHelper.createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (mPhotoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(mPhotoFile));
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }

            }
        });
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setAdapter(new PhotoDetailAdapter(mPlace.getPhotoRefs()));
        checkPlaceholderDisplay();
        return v;
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (mPhotoFile == null) {
                Log.e("PhotoFragment","Photo is null");
                return;
            }

            RequestParams params = new RequestParams();
            params.put("HotspotID", mPlace.getId());
            try {
                params.put("imageName", mPhotoFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("token", SharedPreference.getUserToken(getActivity()));
            client.post(UrlEndpoint.insertImage(), params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.e("PhotoFragment",errorResponse.toString());
                }
            });
        }
    }

    private void setActionBarTitle(String title) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(title);
    }
    private void checkPlaceholderDisplay() {
        if (mRecyclerView.getAdapter().getItemCount() == 0) {
            mTextPlaceholder.setVisibility(View.VISIBLE);
        }
    }
}
