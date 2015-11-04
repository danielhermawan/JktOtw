package com.favesolution.jktotw.Helpers;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Daniel on 10/31/2015 for JktOtw project.
 */
public class SharedPreference {
    private static final String PREF_SKIP_LOGIN = "skipLogin";
    private static final String PREF_USER_TOKEN = "user_token";
    public static boolean getSkipLogin(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_SKIP_LOGIN,false);
    }
    public static void setSkipLogin(Context context,boolean skipLogin) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_SKIP_LOGIN,skipLogin)
                .apply();
    }
    public static String getUserToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_USER_TOKEN,null);
    }
    public static void setUserToken(Context context,String userToken) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_USER_TOKEN,userToken)
                .apply();
    }
}
