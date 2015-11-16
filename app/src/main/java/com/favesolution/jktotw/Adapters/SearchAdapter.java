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
import com.favesolution.jktotw.Interfaces.OnLoadMoreListener;
import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.Networks.UrlEndpoint;
import com.favesolution.jktotw.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Daniel on 11/9/2015 for JktOtw project.
 */
public class SearchAdapter extends RecyclerViewFooterAdapter<Place>{

    public SearchAdapter(RecyclerView recyclerView, List<Place> dataSet, OnLoadMoreListener onLoadMoreListener) {
        super(recyclerView, dataSet, onLoadMoreListener);
    }


    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_search_item,parent,false);
        return new SearchHolder(v,parent.getContext());
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        SearchHolder holder = (SearchHolder) genericHolder;
        Place place = getItem(position);
        holder.bindView(place);
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
        public void bindView(Place place) {
            mPlace = place;
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
