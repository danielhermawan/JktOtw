package com.favesolution.jktotw.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.favesolution.jktotw.Fragments.DetailPlaceFragment;
import com.favesolution.jktotw.R;
import com.favesolution.jktotw.Utils.UIHelper;

public class DetailPlaceActivity extends AppCompatActivity {
    private static final String EXTRA_PLACE_ID="place_id";
    private static final String EXTRA_PLACE_NAME = "place_name";
    public static Intent newIntent(Context context,String placeId,String placeName) {
        Intent i = new Intent(context,DetailPlaceActivity.class);
        i.putExtra(EXTRA_PLACE_ID, placeId);
        i.putExtra(EXTRA_PLACE_NAME,placeName);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_place);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String placeId = getIntent().getStringExtra(EXTRA_PLACE_ID);
        String placeName = getIntent().getStringExtra(EXTRA_PLACE_NAME);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);
        if(fragment == null){
            fragment = DetailPlaceFragment.newInstance(placeId,placeName);
            fm.beginTransaction().add(R.id.content_frame,fragment).commit();
        }
        UIHelper.showOverflowMenu(this);
    }

}
