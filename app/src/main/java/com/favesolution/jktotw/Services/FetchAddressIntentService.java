package com.favesolution.jktotw.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.favesolution.jktotw.Models.Place;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {
    private static final String TAG="FetchAddressService";
    private static final String EXTRA_LOCATION = "extra_location";
    private static final String EXTRA_RECEIVER = "extra_receiver";
    public static final String RESULT_DATA = "result_data";
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_ERROR = 0;
    private ResultReceiver mReceiver;
    public FetchAddressIntentService() {
        super(TAG);
    }
    public static Intent newIntent(Context context,ArrayList<Place> places,ResultReceiver receiver) {
        Intent i = new Intent(context,FetchAddressIntentService.class);
        i.putExtra(EXTRA_RECEIVER, receiver);
        i.putParcelableArrayListExtra(EXTRA_LOCATION, places);
        return i;
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        mReceiver = intent.getParcelableExtra(EXTRA_RECEIVER);
        if (mReceiver == null) {
            Log.e(TAG,"No receiver received.");
        }
        //Place place = intent.getParcelableExtra(EXTRA_LOCATION);
        ArrayList<Place> places = intent.getParcelableArrayListExtra(EXTRA_LOCATION);
        if (places == null) {
            Log.e(TAG,"No location provided");
            return;
        }
        List<List<Address>> addresses = new ArrayList<>();
        for (int i = 0; i < places.size(); i++) {
            try {
                addresses.add(geocoder.getFromLocation(places.get(i).getLatitude(), places.get(i).getLongitude(), 1));
            } catch (IOException e) {
                Log.e(TAG,e.getMessage());
            }
        }

        for (int a = 0; a < places.size(); a++) {
            Address address = addresses.get(a).get(0);
            List<String> addressFragments = new ArrayList<String>();
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
                places.get(a).setAddress(TextUtils.join(" ", addressFragments));
            }
        }
        deliverResultToReceiver(RESULT_SUCCESS, places);

    }
    private void deliverResultToReceiver(int resultCode, ArrayList<Place> places) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RESULT_DATA, places);
        mReceiver.send(resultCode, bundle);
    }

}
