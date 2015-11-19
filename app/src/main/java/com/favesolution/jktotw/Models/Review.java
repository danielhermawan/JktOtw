package com.favesolution.jktotw.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Daniel on 11/6/2015 for JktOtw project.
 */
public class Review implements Parcelable {
    private String mAuthor;
    private String mReview;
    private Date mDate;

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getReview() {
        return mReview;
    }

    public void setReview(String review) {
        mReview = review;
    }

    public static Review fromJson(JSONObject jsonObject) {
        Review review = new Review();
        try {
            review.mAuthor = jsonObject.getString("author_name");
            review.mReview = jsonObject.getString("text");
            Long time = jsonObject.getLong("time");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            review.mDate = calendar.getTime();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return review;
    }
    public static Review fromJsonJktOtw(JSONObject jsonObject) {
        Review review = new Review();
        try {
            review.mAuthor = jsonObject.getString("nama");
            review.mReview = jsonObject.getString("review");
            String date = jsonObject.getString("date");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            review.mDate = dateFormat.parse(date);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return review;
    }
    public static ArrayList<Review> fromJson(JSONArray jsonArray) {
        ArrayList<Review> reviews = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject reviewJson = jsonArray.getJSONObject(i);
                Review review = Review.fromJson(reviewJson);
                reviews.add(review);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return reviews;
    }
    public static ArrayList<Review> fromJsonJktOtw(JSONArray jsonArray) {
        ArrayList<Review> reviews = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject reviewJson = jsonArray.getJSONObject(i);
                Review review = Review.fromJsonJktOtw(reviewJson);
                reviews.add(review);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return reviews;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mAuthor);
        dest.writeString(this.mReview);
        dest.writeLong(mDate != null ? mDate.getTime() : -1);
    }

    public Review() {
    }

    protected Review(Parcel in) {
        this.mAuthor = in.readString();
        this.mReview = in.readString();
        long tmpMDate = in.readLong();
        this.mDate = tmpMDate == -1 ? null : new Date(tmpMDate);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
