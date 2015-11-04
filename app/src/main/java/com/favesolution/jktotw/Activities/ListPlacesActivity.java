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

import com.favesolution.jktotw.Fragments.ListPlacesFragment;
import com.favesolution.jktotw.Helpers.UIHelper;
import com.favesolution.jktotw.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class ListPlacesActivity extends AppCompatActivity {
    private static final String EXTRA_POSITION = "extra_position";
    private static final int REQUEST_ERROR = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_places);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int position = getIntent().getIntExtra(EXTRA_POSITION,0);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);
        if(fragment == null){
            fragment = ListPlacesFragment.newInstance(position);
            fm.beginTransaction().add(R.id.content_frame,fragment).commit();
        }
        UIHelper.showOverflowMenu(this);
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

    public static Intent newIntent(Context context,int position) {
        Intent i = new Intent(context,ListPlacesActivity.class);
        i.putExtra(EXTRA_POSITION,position);
        return i;
    }
}
