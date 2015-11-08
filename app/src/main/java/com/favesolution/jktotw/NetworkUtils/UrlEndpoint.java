package com.favesolution.jktotw.NetworkUtils;

import android.location.Location;
import android.net.Uri;

public class UrlEndpoint {
    private static final String BASE_WEBSERVICE_URL = "http://favesolution.com/jktotw/";
    private static final String GOOGLE_SERVER_KEY = "AIzaSyDfVq9akzp7xLY3lzWKDFnFo8idWTBq9Pc";
    private static final String API_JKTOTW_KEY ="KP4HcIeqUTJPvJBDZ5d6j4Gyp54htV13";
    private static final Uri URL_SEARCH_PLACE = Uri
            .parse("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
            .buildUpon()
            .appendQueryParameter("key", GOOGLE_SERVER_KEY)
            .build();
    private static final Uri URL_DETAIL_PLACE = Uri
            .parse("https://maps.googleapis.com/maps/api/place/details/json")
            .buildUpon()
            .appendQueryParameter("key", GOOGLE_SERVER_KEY)
            .build();
    private static final String DEFAULT_RADIUS = "500";
    public static String registerUser() {
        return BASE_WEBSERVICE_URL+"user/InsertUser/"+API_JKTOTW_KEY;
    }
    public static String loginUser() {
        return BASE_WEBSERVICE_URL+"user/Login/"+API_JKTOTW_KEY;
    }
    public static String searchNearbyPlaceByRadius(Location location, String type,String radius) {
        if (radius == null) {
            radius = DEFAULT_RADIUS;
        }
        String stringLocation = location.getLatitude() + "," + location.getLongitude();
        return URL_SEARCH_PLACE.buildUpon()
                .appendQueryParameter("radius", radius)
                .appendQueryParameter("types", type)
                .appendQueryParameter("location", stringLocation)
                .toString();
    }
    public static String searchNearbyPlace(Location location, String type) {
        String stringLocation = location.getLatitude() + "," + location.getLongitude();
        return URL_SEARCH_PLACE.buildUpon()
                .appendQueryParameter("rankby", "distance")
                .appendQueryParameter("types", type)
                .appendQueryParameter("location", stringLocation)
                .toString();
    }
    public static String getDetailPlace(String placeId) {
        return URL_DETAIL_PLACE.buildUpon()
                .appendQueryParameter("placeid",placeId)
                .toString();
    }
}
