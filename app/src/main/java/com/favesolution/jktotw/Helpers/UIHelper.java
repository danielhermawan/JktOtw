package com.favesolution.jktotw.Helpers;

import android.support.v4.app.FragmentActivity;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

/**
 * Created by Daniel on 11/3/2015 for JktOtw project.
 */
public class UIHelper {
    public static void showOverflowMenu(FragmentActivity activity) {
        try {
            ViewConfiguration config = ViewConfiguration.get(activity);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
