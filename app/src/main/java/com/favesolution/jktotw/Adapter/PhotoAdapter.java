package com.favesolution.jktotw.Adapter;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.favesolution.jktotw.Helpers.PhotoTask;
import com.favesolution.jktotw.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Daniel on 11/5/2015 for JktOtw project.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    private List<PhotoTask.AttributedPhoto> mPhotos;
    private final static String TAG = "PhotoAdapter";

    public PhotoAdapter(List<PhotoTask.AttributedPhoto> attributedPhotos) {
        mPhotos = attributedPhotos;
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_image_circle,parent,false);
        Log.d("debug","oncreate");
        return new PhotoHolder(v);
    }

    @Override
    public void onBindViewHolder(PhotoHolder holder, int position) {
        PhotoTask.AttributedPhoto photo = mPhotos.get(position);
        holder.bindPhotoItems(photo);
    }
    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    class PhotoHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private PhotoTask.AttributedPhoto mPhoto;
        private CircleImageView mImageView;
        public PhotoHolder(View itemView) {
            super(itemView);
            mImageView = (CircleImageView) itemView.findViewById(R.id.item_image_circle);
            itemView.setOnClickListener(this);
        }
        public void bindPhotoItems(PhotoTask.AttributedPhoto photo) {
            mPhoto = photo;
            mImageView.setImageBitmap(photo.bitmap);
            Log.d(TAG,"bind photo");
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG,"clock");
        }
    }
}