package com.favesolution.jktotw.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.favesolution.jktotw.Networks.UrlEndpoint;
import com.favesolution.jktotw.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Daniel on 11/5/2015 for JktOtw project.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    private List<String> mPhotos;
    private final static String TAG = "PhotoAdapter";

    public PhotoAdapter(List<String> attributedPhotos) {
        mPhotos = attributedPhotos;
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_image_circle,parent,false);
        return new PhotoHolder(v,parent.getContext());
    }

    @Override
    public void onBindViewHolder(PhotoHolder holder, int position) {
        String photo = mPhotos.get(position);
        holder.bindPhotoItems(photo);
    }
    @Override
    public int getItemCount() {
        if (mPhotos.size() > 4) {
            return 4;
        } else {
            return mPhotos.size();
        }
    }

    class PhotoHolder extends RecyclerView.ViewHolder {
        private String mPhoto;
        private CircleImageView mImageView;
        private Context mContext;
        public PhotoHolder(View itemView,Context context) {
            super(itemView);
            mImageView = (CircleImageView) itemView.findViewById(R.id.item_image_circle);
            mContext = context;
        }
        public void bindPhotoItems(String photo) {
            mPhoto = photo;
            if (!photo.equals("error")) {
                String url;
                if (photo.startsWith("http://favesolution.com/jktotw/")) {
                    url = photo;
                } else {
                    url = UrlEndpoint.getPhotoUrl(mPhoto,
                            (int) mContext.getResources().getDimension(R.dimen.image_photo_circle_width),
                            (int) mContext.getResources().getDimension(R.dimen.image_photo_circle_height));
                }
                Glide.with(mContext)
                        .load(url)
                        //.placeholder(R.drawable.bitmap_default_placeholder_300x300)
                        .error(R.drawable.bitmap_placeholder)
                        .into(mImageView);
            } else {
                mImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.bitmap_placeholder));
            }

        }

    }
}
