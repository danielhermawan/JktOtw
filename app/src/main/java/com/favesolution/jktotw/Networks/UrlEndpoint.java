package com.favesolution.jktotw.Networks;

import android.content.Context;
import android.location.Location;
import android.net.Uri;

import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.Models.Type;

import java.util.List;

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
    private static final Uri URL_PHOTO_PLACE = Uri
            .parse("https://maps.googleapis.com/maps/api/place/photo")
            .buildUpon()
            .appendQueryParameter("key", GOOGLE_SERVER_KEY)
            .build();
    private static final Uri URL_PLACE_DIRECTION = Uri
            .parse("https://maps.googleapis.com/maps/api/directions/json")
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
    public static String getHotspot() {
        return BASE_WEBSERVICE_URL+"hotspot/gethotspot/"+API_JKTOTW_KEY;
    }

    public static String getHotspotPhoto(String hotspotId) {
        return BASE_WEBSERVICE_URL+"image/getImagebyHotspotID/"+API_JKTOTW_KEY+"/"+hotspotId;
    }
    public static String insertImage() {
        return BASE_WEBSERVICE_URL+"image/insertImage/"+API_JKTOTW_KEY;
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
    public static String searchNearbyPlace(Location location, String type,Context context) {
        String stringLocation = location.getLatitude() + "," + location.getLongitude();
        String filter="";
        if (type.equals("all")) {
            List<Type> types = Type.getCategory(context);
            for (int i = 0; i < types.size() - 1; i++) {
                if (i != 0)
                    filter += "|";
                filter += types.get(i).getCategoryFilter();
            }
        } else {
            filter = type;
        }
        return URL_SEARCH_PLACE.buildUpon()
                .appendQueryParameter("rankby", "distance")
                .appendQueryParameter("types", filter)
                .appendQueryParameter("location", stringLocation)
                .toString();
    }

    public static String getDetailPlace(String placeId) {
        return URL_DETAIL_PLACE.buildUpon()
                .appendQueryParameter("placeid",placeId)
                .toString();
    }
    public static String searchNearbyPlaceByKeyword(Context context,Location location,String keyword) {
        String filter="";
        List<Type> types = Type.getCategory(context);
        for (int i = 0; i < types.size()-1; i++) {
            if(i!=0)
                filter+="|";
            filter+=types.get(i).getCategoryFilter();
        }

        String stringLocation = location.getLatitude() + "," + location.getLongitude();
        return URL_SEARCH_PLACE.buildUpon()
                .appendQueryParameter("rankby", "distance")
                .appendQueryParameter("location", stringLocation)
                .appendQueryParameter("keyword", keyword)
                .appendQueryParameter("types", filter)
                .toString();
    }
    public static String searchNearbyPlaceByKeyword(Context context,Location location,String keyword,String filter) {
        String stringLocation = location.getLatitude() + "," + location.getLongitude();
        return URL_SEARCH_PLACE.buildUpon()
                .appendQueryParameter("rankby", "distance")
                .appendQueryParameter("location", stringLocation)
                .appendQueryParameter("keyword", keyword)
                .appendQueryParameter("types", filter)
                .toString();
    }
    public static String getPhotoUrl(String reference,int maxWidth,int maxHeight) {
        return URL_PHOTO_PLACE.buildUpon()
                .appendQueryParameter("photoreference",reference)
                .appendQueryParameter("maxheight",maxHeight+"")
                .appendQueryParameter("maxwidth",maxWidth+"")
                .toString();
    }
    public static String getDirectionUrl(Location origin,Place place) {
        return URL_PLACE_DIRECTION.buildUpon()
                .appendQueryParameter("origin",origin.getLatitude()+","+origin.getLongitude())
                .appendQueryParameter("destination",place.getLatitude()+","+place.getLongitude())
                .toString();
    }
}
