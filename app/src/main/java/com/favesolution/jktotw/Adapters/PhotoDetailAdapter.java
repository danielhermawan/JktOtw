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

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Daniel on 11/11/2015 for JktOtw project.
 */
public class PhotoDetailAdapter extends RecyclerView.Adapter<PhotoDetailAdapter.PhotoDetailHolder>{
    private List<String> mPhotoList;

    public PhotoDetailAdapter(List<String> photoList) {
        mPhotoList = photoList;
    }

    @Override
    public PhotoDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_image_circle_large,parent,false);
        return new PhotoDetailHolder(v,parent.getContext());
    }

    @Override
    public void onBindViewHolder(PhotoDetailHolder holder, int position) {
        String photoRef = mPhotoList.get(position);
        holder.bindView(photoRef);
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    class PhotoDetailHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @Bind(R.id.item_image_circle) CircleImageView mImageView;
        private String mPhoto;
        private Context mContext;
        public PhotoDetailHolder(View itemView,Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
            itemView.setOnClickListener(this);
        }
        public void bindView(String photo) {
            mPhoto = photo;
            if (!photo.equals("error")) {
                String url;
                if (photo.startsWith("http://favesolution.com/jktotw/")) {
                    url = photo;
                } else {
                    url = UrlEndpoint.getPhotoUrl(mPhoto,
                            (int) mContext.getResources().getDimension(R.dimen.image_photo_circle_large_width),
                            (int) mContext.getResources().getDimension(R.dimen.image_photo_circle_large_height));
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
        @Override
        public void onClick(View v) {

        }
    }
}
