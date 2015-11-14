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
import com.favesolution.jktotw.Models.Type;
import com.favesolution.jktotw.R;
import com.favesolution.jktotw.Utils.UIHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class ListPlacesActivity extends AppCompatActivity {
    private static final String EXTRA_TYPE = "extra_type";
    private static final int REQUEST_ERROR = 0;
    private Type mType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_places);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //int position = getIntent().getIntExtra(EXTRA_TYPE,0);
        mType = getIntent().getParcelableExtra(EXTRA_TYPE);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);
        if(fragment == null){
            fragment = ListPlacesFragment.newInstance(mType);
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
    @Override
    public void startActivity(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra(SearchActivity.EXTRA_CATEGORY, mType.getCategoryFilter());
        }
        super.startActivity(intent);
    }
    public static Intent newIntent(Context context,Type type) {
        Intent i = new Intent(context,ListPlacesActivity.class);
        i.putExtra(EXTRA_TYPE,type);
        return i;
    }
}
