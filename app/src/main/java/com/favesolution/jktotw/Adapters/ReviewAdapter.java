package com.favesolution.jktotw.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.favesolution.jktotw.Models.Review;
import com.favesolution.jktotw.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Daniel on 11/17/2015 for JktOtw project.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {
    List<Review> mReviews;
    int hasMax = 0;
    public ReviewAdapter(List<Review> reviews) {
        mReviews = reviews;
    }

    public ReviewAdapter(List<Review> reviews,int hasMax) {
        this.hasMax = hasMax;
        mReviews = reviews;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_reviews,parent,false);
        return new ReviewHolder(v,parent.getContext());
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.bindView(review);
    }

    @Override
    public int getItemCount() {
        if (hasMax == 0 || mReviews.size()<hasMax) {
            return mReviews.size();
        } else{
            return hasMax;
        }

    }
    class ReviewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.text_username) TextView mTextUsername;
        @Bind(R.id.text_date) TextView mTextDate;
        @Bind(R.id.text_review) TextView mTextReview;
        private Context mContext;
        public ReviewHolder(View itemView,Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
        }
        public void bindView(Review review) {
            mTextUsername.setText(review.getAuthor());
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            mTextDate.setText(DateUtils.getRelativeTimeSpanString(review.getDate().getTime(),System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS));
            mTextReview.setText(mContext.getString(R.string.review, review.getReview()));
        }
    }
}
