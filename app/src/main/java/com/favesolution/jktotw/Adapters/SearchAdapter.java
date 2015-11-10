package com.favesolution.jktotw.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.favesolution.jktotw.Activities.DetailPlaceActivity;
import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.Networks.UrlEndpoint;
import com.favesolution.jktotw.R;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Daniel on 11/9/2015 for JktOtw project.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder>{
    private List<Place> mPlaces;
    private GoogleApiClient mClient;
    public SearchAdapter(List<Place> places,GoogleApiClient client) {
        mPlaces = places;
        mClient = client;
    }

    @Override
    public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_search_item,parent,false);
        return new SearchHolder(v,parent.getContext());
    }

    @Override
    public void onBindViewHolder(SearchHolder holder, int position) {
        Place place = mPlaces.get(position);
        //Log.d("debug","Position = "+position+" "+place.getAddress());
        holder.bindView(place,mClient);
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    class SearchHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @Bind(R.id.image_place) ImageView mImageView;
        @Bind(R.id.text_name_place) TextView mTextName;
        @Bind(R.id.text_address_place) TextView mTextAddress;
        private Place mPlace;
        private Context mContext;
        public SearchHolder(View itemView,Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mContext = context;
        }
        @Override
        public void onClick(View v) {
            mContext.startActivity(DetailPlaceActivity.newIntent(mContext,mPlace));
        }
        public void bindView(Place place,GoogleApiClient client) {
            mPlace = place;
            mClient = client;
            mTextName.setText(place.getName());
            mTextAddress.setText(place.getAddress());
            String url = UrlEndpoint.getPhotoUrl(mPlace.getPhotoRef(), 70, 70);
            Glide.with(mContext)
                    .load(url)
                    .error(R.drawable.bitmap_placeholder)
                    .into(mImageView);
        }
    }
}
