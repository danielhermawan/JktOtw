package com.favesolution.jktotw.Models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 11/3/2015 for JktOtw project.
 */
public class Place implements Serializable{
    private String mName;
    private String mId;
    private double mLatitude;
    private double mLongitude;
    private double mDistance;
    private String mAddress;
    private float mRating;
    private String mPhoneNumber;
    private List<String> mTypes;
    public LatLng getLatLng() {
        return new LatLng(mLatitude,mLongitude);
    }
    public List<String> getTypes() {
        return mTypes;
    }

    public void setTypes(List<String> types) {
        mTypes = types;
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
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return place;
    }
    public static Place fromJsonDetail(JSONObject jsonObject) {
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
            List<String> listType = new ArrayList<>();
            for (int i = 0; i < types.length(); i++) {
                listType.add(types.getString(i));
            }
            place.setTypes(listType);
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
}
