package com.favesolution.jktotw.Models;

import android.content.Context;

import com.favesolution.jktotw.Utils.SharedPreference;

/**
 * Created by Daniel on 11/1/2015 for JktOtw project.
 */
public class User {
    private String mName;
    private String mEmail;
    private String mPhone;
    private String mPassword;
    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public static boolean checkIsLogin(Context context) {
        if (SharedPreference.getUserToken(context)!=null)
            return true;
        else
            return false;
    }

}
