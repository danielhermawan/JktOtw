package com.favesolution.jktotw.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.favesolution.jktotw.Fragments.PhotoFragment;
import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.R;
import com.favesolution.jktotw.Utils.UIHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class PhotoActivity extends AppCompatActivity {
    private static final int REQUEST_ERROR = 0;
    private static final String EXTRA_PLACE = "extra_place";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UIHelper.showOverflowMenu(this);
        Place place = getIntent().getParcelableExtra(EXTRA_PLACE);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);
        if(fragment == null){
            fragment = PhotoFragment.newInstance(place);
            fm.beginTransaction().add(R.id.content_frame,fragment).commit();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GooglePlayServicesUtil.
                    getErrorDialog(errorCode, this, REQUEST_ERROR,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    finish();
                                }
                            });
            errorDialog.show();
        }
    }
    public static Intent newIntent(Context context,Place place) {
        Intent i = new Intent(context,PhotoActivity.class);
        i.putExtra(EXTRA_PLACE,place);
        return i;
    }
}
