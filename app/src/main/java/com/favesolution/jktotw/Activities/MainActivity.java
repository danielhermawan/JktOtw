package com.favesolution.jktotw.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.favesolution.jktotw.Fragments.HomeFragment;
import com.favesolution.jktotw.Models.Type;
import com.favesolution.jktotw.R;
import com.favesolution.jktotw.Utils.SharedPreference;
import com.favesolution.jktotw.Utils.UIHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view) NavigationView mNavigationView;
    View mHeaderNavigation;
    ImageView mCloseNavigation;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkSkippedLogin();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mHeaderNavigation = LayoutInflater.from(this).inflate(R.layout.navigation_header, null);
        mCloseNavigation = (ImageView) mHeaderNavigation.findViewById(R.id.img_back_arrow);
        mCloseNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
            }
        });
        mNavigationView.addHeaderView(mHeaderNavigation);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                List<Type> types = Type.getCategory(MainActivity.this);
                switch (item.getItemId()) {
                    case R.id.navigation_item_login:
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        return true;
                    case R.id.navigation_item_home:
                        setActiveContent(HomeFragment.newInstance());
                        return true;
                    case R.id.navigation_item_food:
                        startActivity(ListPlacesActivity.newIntent(MainActivity.this, types.get(0)));
                        return true;
                    case R.id.navigation_item_entertaiment:
                        startActivity(ListPlacesActivity.newIntent(MainActivity.this, types.get(1)));
                        return true;
                    case R.id.navigation_item_atm:
                        startActivity(ListPlacesActivity.newIntent(MainActivity.this, types.get(2)));
                        return true;
                    case R.id.navigation_item_hotel:
                        startActivity(ListPlacesActivity.newIntent(MainActivity.this, types.get(3)));
                        return true;
                    case R.id.navigation_item_shooping:
                        startActivity(ListPlacesActivity.newIntent(MainActivity.this, types.get(4)));
                        return true;
                    case R.id.navigation_item_hospital:
                        startActivity(ListPlacesActivity.newIntent(MainActivity.this, types.get(5)));
                        return true;
                    case R.id.navigation_item_hotspot:
                        startActivity(ListPlacesActivity.newIntent(MainActivity.this, types.get(6)));
                        return true;
                    case R.id.navigation_item_profile:
                        return true;
                    case R.id.navigation_item_logout:
                        SharedPreference.setUserToken(MainActivity.this, null);
                        //TODO: Erase token from database server
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                        return true;
                    case R.id.navigation_item_map:
                        Type type = new Type();
                        type.setCategoryName(getString(R.string.all_place));
                        type.setCategoryFilter("all");
                        startActivity(MapPlaceActivity.newIntent(MainActivity.this,type));
                        return true;
                    default:
                        return true;
                }
            }
        });
        setActiveContent(HomeFragment.newInstance());
        checkNavigationMenu();
        UIHelper.showOverflowMenu(this);
        //todo: change menu to display programtilly
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setActiveContent(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.content_frame, fragment)
               .commit();
        mDrawerLayout.closeDrawers();
    }
    private void checkSkippedLogin() {
        if (!SharedPreference.getSkipLogin(this)&& SharedPreference.getUserToken(this)==null) {
            Intent i = new Intent(this,LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }

    private void checkNavigationMenu() {
        Menu menu = mNavigationView.getMenu();
        if (SharedPreference.getUserToken(this) == null) {
            menu.findItem(R.id.navigation_item_profile).setVisible(false);
            menu.findItem(R.id.navigation_item_logout).setVisible(false);
        } else {
            menu.findItem(R.id.navigation_item_login).setVisible(false);
        }
    }
}
