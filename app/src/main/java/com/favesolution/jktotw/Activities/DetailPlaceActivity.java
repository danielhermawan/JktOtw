package com.favesolution.jktotw.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.favesolution.jktotw.Fragments.DetailPlaceFragment;
import com.favesolution.jktotw.Fragments.ListPlacesFragment;
import com.favesolution.jktotw.Helpers.UIHelper;
import com.favesolution.jktotw.R;

public class DetailPlaceActivity extends AppCompatActivity {
    private static final String EXTRA_PLACE_ID="place_id";

    public static Intent newIntent(Context context,String placeId) {
        Intent i = new Intent(context,DetailPlaceActivity.class);
        i.putExtra(EXTRA_PLACE_ID,placeId);
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
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);
        if(fragment == null){
            fragment = DetailPlaceFragment.newInstance(placeId);
            fm.beginTransaction().add(R.id.content_frame,fragment).commit();
        }
        UIHelper.showOverflowMenu(this);
    }

}
