package com.favesolution.jktotw.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.favesolution.jktotw.Fragments.ReviewFragment;
import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.R;
import com.favesolution.jktotw.Utils.UIHelper;

public class ReviewActivity extends AppCompatActivity {
    private static final String EXTRA_PLACE = "extra_place";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UIHelper.showOverflowMenu(this);
        Place place = getIntent().getParcelableExtra(EXTRA_PLACE);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);
        if(fragment == null){
            fragment = ReviewFragment.newInstance(place);
            fm.beginTransaction().add(R.id.content_frame,fragment).commit();
        }
    }
    public static Intent newIntent(Context context,Place place) {
        Intent i = new Intent(context,ReviewActivity.class);
        i.putExtra(EXTRA_PLACE,place);
        return i;
    }
}
