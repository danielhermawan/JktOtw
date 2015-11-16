package com.favesolution.jktotw.Models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.favesolution.jktotw.Utils.SharedPreference;

/**
 * Created by Daniel on 11/1/2015 for JktOtw project.
 */
public class User implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mEmail);
        dest.writeString(this.mPhone);
        dest.writeString(this.mPassword);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.mName = in.readString();
        this.mEmail = in.readString();
        this.mPhone = in.readString();
        this.mPassword = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
