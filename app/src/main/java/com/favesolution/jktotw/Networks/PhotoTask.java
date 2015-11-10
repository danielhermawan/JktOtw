package com.favesolution.jktotw.Networks;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 11/5/2015 for JktOtw project.
 */
public class PhotoTask  extends AsyncTask<String,Void,List<PhotoTask.AttributedPhoto>>{
    private float mHeight;
    private float mWidth;
    private int mNumber;
    private FinishLoadingAction mFinishLoadingAction;
    private GoogleApiClient mGoogleApiClient;
    public PhotoTask(float width, float height,GoogleApiClient googleApiClient,int number) {
        mHeight = height;
        mWidth = width;
        mGoogleApiClient = googleApiClient;
        mNumber = number;
    }
    public PhotoTask setOnResultCallback(FinishLoadingAction finishLoadingAction) {
        mFinishLoadingAction = finishLoadingAction;
        return this;
    }
    @Override
    protected void onPostExecute(List<AttributedPhoto> attributedPhotos) {
        mFinishLoadingAction.onResult(attributedPhotos);
    }

    @Override
    protected List<AttributedPhoto> doInBackground(String... params) {
        if (params.length != 1) {
            return null;
        }
        final String placeId = params[0];
        List<AttributedPhoto> attributedPhotos = new ArrayList<>();
        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId).await();
        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            int numberPhoto;
            if (mNumber == 0 || photoMetadataBuffer.getCount() <mNumber) {
                numberPhoto = photoMetadataBuffer.getCount();
            } else {
                numberPhoto = mNumber;
            }
            if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                for (int i = 0; i < numberPhoto; i++) {
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(i);
                    CharSequence attribution = photo.getAttributions();
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, (int)mWidth,(int) mHeight).await()
                            .getBitmap();
                    attributedPhotos.add(new AttributedPhoto(attribution, image,photoMetadataBuffer.getCount()));
                }
            }
            photoMetadataBuffer.release();
        }
        return attributedPhotos;

    }
    /**
     * Holder for an image and its attribution.
     */
    public class AttributedPhoto {

        public final CharSequence attribution;

        public final Bitmap bitmap;
        public final int totalPhoto;
        public AttributedPhoto(CharSequence attribution, Bitmap bitmap,int totalPhoto) {
            this.attribution = attribution;
            this.bitmap = bitmap;
            this.totalPhoto = totalPhoto;
        }
    }
    public interface FinishLoadingAction{
        public void onResult(List<AttributedPhoto> attributedPhotos);
    };
}
