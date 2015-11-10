package com.favesolution.jktotw.Models;

import android.content.Context;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 11/3/2015 for JktOtw project.
 */
public class Place implements Parcelable{
    private String mName;
    private String mId;
    private double mLatitude;
    private double mLongitude;
    private double mDistance;
    private String mAddress;
    private float mRating;
    private String mPhoneNumber;
    //private List<String> mTypes;
    private String mPhotoRef;
    private Type mType;
    public Place() {
    }

    public LatLng getLatLng() {
        return new LatLng(mLatitude,mLongitude);
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public String getPhotoRef() {
        return mPhotoRef;
    }

    public void setPhotoRef(String photoRef) {
        mPhotoRef = photoRef;
    }


    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }


    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double distance) {
        mDistance = distance;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getId() {
        return mId;
    }
    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public static Place fromJson(JSONObject jsonObject,Location userLocation) {
        Place place = new Place();
        try {
            place.mId =  jsonObject.getString("place_id");
            place.mName = jsonObject.getString("name");
            JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
            place.mLatitude = location.getDouble("lat");
            place.mLongitude = location.getDouble("lng");
            Location locationPlace = new Location("");
            locationPlace.setLatitude(place.mLatitude);
            locationPlace.setLongitude(place.mLongitude);
            place.mDistance = userLocation.distanceTo(locationPlace);
            if (jsonObject.has("photos")) {
                JSONArray photos = jsonObject.getJSONArray("photos");
                JSONObject photo = photos.getJSONObject(0);
                place.mPhotoRef = photo.getString("photo_reference");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return place;
    }
    public static Place fromJsonDetail(JSONObject jsonObject,Context context) {
        Place place = new Place();
        try {
            place.setAddress(jsonObject.getString("formatted_address"));
            if(jsonObject.has("rating"))
                place.setRating((float) jsonObject.getDouble("rating"));
            else
                place.setRating(0f);
            JSONObject location = jsonObject.getJSONObject("geometry")
                    .getJSONObject("location");
            place.setLatitude(location.getDouble("lat"));
            place.setLongitude(location.getDouble("lng"));
            if(jsonObject.has("international_phone_number"))
                place.setPhoneNumber(jsonObject.getString("international_phone_number"));
            place.setName(jsonObject.getString("name"));
            JSONArray types = jsonObject.getJSONArray("types");
            List<Type> typeList = Type.getCategory(context);
            Type type = new Type();
            for (int i = 0; i < types.length(); i++) {
                for (int j = 0; j < typeList.size(); j++) {
                    if (typeList.get(j).getCategoryFilter().contains(types.getString(i))) {
                        type = typeList.get(j);
                        i=types.length();
                        break;
                    }
                }
            }
            place.setType(type);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return place;
    }
    public static ArrayList<Place> fromJson(JSONArray jsonArray,Location userLocation) {
        ArrayList<Place> places = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject placeJson;
            try {
                placeJson=jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
            Place place = Place.fromJson(placeJson,userLocation);
            if (place != null) {
                places.add(place);
            }
        }
        return places;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mId);
        dest.writeDouble(this.mLatitude);
        dest.writeDouble(this.mLongitude);
        dest.writeDouble(this.mDistance);
        dest.writeString(this.mAddress);
        dest.writeFloat(this.mRating);
        dest.writeString(this.mPhoneNumber);
        //dest.writeStringList(this.mTypes);
        dest.writeString(this.mPhotoRef);
        dest.writeParcelable(this.mType, 0);
    }

    protected Place(Parcel in) {
        this.mName = in.readString();
        this.mId = in.readString();
        this.mLatitude = in.readDouble();
        this.mLongitude = in.readDouble();
        this.mDistance = in.readDouble();
        this.mAddress = in.readString();
        this.mRating = in.readFloat();
        this.mPhoneNumber = in.readString();
        //this.mTypes = in.createStringArrayList();
        this.mPhotoRef = in.readString();
        this.mType = in.readParcelable(Type.class.getClassLoader());
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        public Place createFromParcel(Parcel source) {
            return new Place(source);
        }

        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}
