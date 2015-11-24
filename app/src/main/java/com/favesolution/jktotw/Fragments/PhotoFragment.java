package com.favesolution.jktotw.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
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
import org.json.JSONException;
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
    static final int REQUEST_PICK_GALLERY = 2;
    private Place mPlace;
    ProgressDialog progress;
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
        setActionBarTitle(mPlace.getName()+" Photos");
        if (!User.checkIsLogin(getActivity())) {
            mButtonAddPhoto.setVisibility(View.GONE);
        }
        mButtonAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheet.Builder(getActivity())
                        .title("Upload Photo")
                        .sheet(R.menu.menu_upload_photo)
                        .listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case R.id.action_camera:
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
                                        break;
                                    case R.id.action_gallery:
                                        Intent intent = new Intent(Intent.ACTION_PICK,
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                            // Bring up gallery to select a photo
                                            startActivityForResult(intent, REQUEST_PICK_GALLERY);
                                        }
                                        break;
                                }
                            }
                        }).show();
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
            String url;
            if (mPlace.getType().getCategoryName().equals(getString(R.string.category_indosat))) {
                url = UrlEndpoint.insertIndosatImage();
            } else {
                url = UrlEndpoint.insertPlaceImage();
            }
            RequestParams params = new RequestParams();
            params.put("id", mPlace.getId());
            try {
                params.put("imageName", mPhotoFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Toast.makeText(getActivity(), "Upload photo to server", Toast.LENGTH_SHORT).show();
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("token", SharedPreference.getUserToken(getActivity()));
            progress = ProgressDialog.show(getActivity(), "Uploading", "Upload photo to server", true);
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        String result = response.getString("status");
                        if (result.equalsIgnoreCase("success")) {
                            progress.dismiss();
                            Toast.makeText(getActivity(), "Add Photo Success", Toast.LENGTH_SHORT).show();

                            mPlace.getPhotoRefs().add(response.getString("img").replace("\\/", "/"));
                            mRecyclerView.setAdapter(new PhotoDetailAdapter(mPlace.getPhotoRefs()));
                            /*Intent i = DetailPlaceActivity.newIntent(getActivity(),mPlace);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);*/
                        } else {
                            progress.dismiss();
                            Toast.makeText(getActivity(), "Add Photo Failure", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
        else if (requestCode == REQUEST_PICK_GALLERY && data != null && resultCode == Activity.RESULT_OK) {
            Uri photoUri = data.getData();
            File file = new File(convertMediaUriToPath(photoUri));
            String url;
            if (mPlace.getType().getCategoryName().equals(getString(R.string.category_indosat))) {
                url = UrlEndpoint.insertIndosatImage();
            } else {
                url = UrlEndpoint.insertPlaceImage();
            }
            RequestParams params = new RequestParams();
            params.put("id", mPlace.getId());
            try {
                params.put("imageName", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Toast.makeText(getActivity(), "Upload photo to server", Toast.LENGTH_SHORT).show();
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("token", SharedPreference.getUserToken(getActivity()));
            progress = ProgressDialog.show(getActivity(), "Uploading", "Upload photo to server", true);
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        String result = response.getString("status");
                        if (result.equalsIgnoreCase("success")) {
                            progress.dismiss();
                            Toast.makeText(getActivity(), "Add Photo Success", Toast.LENGTH_SHORT).show();

                            mPlace.getPhotoRefs().add(response.getString("img").replace("\\/", "/"));
                            mRecyclerView.setAdapter(new PhotoDetailAdapter(mPlace.getPhotoRefs()));
                            /*Intent i = DetailPlaceActivity.newIntent(getActivity(),mPlace);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);*/
                        } else {
                            progress.dismiss();
                            Toast.makeText(getActivity(), "Add Photo Failure", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progress.dismiss();
                    }

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.e("PhotoFragment", errorResponse.toString());
                    progress.dismiss();
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
    private String convertMediaUriToPath(Uri uri) {
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, proj,  null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }
}
