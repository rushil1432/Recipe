package com.example.recipe.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.example.recipe.MainActivity;
import com.example.recipe.app.AppConstants;
import com.example.recipe.model.User;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "usersharedpref";


    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(AppConstants.KEY_USER_ID, user.getUser_id());
        if (user.getFname()==null){
            editor.putString(AppConstants.KEY_FNAME,"");
        }
        else {
            editor.putString(AppConstants.KEY_FNAME, user.getFname());
        }

        if (user.getMname()==null){
            editor.putString(AppConstants.KEY_MNAME,"");
        }
        else {
            editor.putString(AppConstants.KEY_MNAME, user.getMname());
        }

        if (user.getLname()==null){
            editor.putString(AppConstants.KEY_LNAME,"");
        }
        else {
            editor.putString(AppConstants.KEY_LNAME, user.getLname());
        }
        if (user.getAddress()==null){
            editor.putString(AppConstants.KEY_ADDRESS,"");
        }
        else {
            editor.putString(AppConstants.KEY_ADDRESS, user.getAddress());
        }


        editor.putString(AppConstants.KEY_PHONE, user.getPhone());
        editor.putString(AppConstants.KEY_EMAIL, user.getEmail());
        editor.putString(AppConstants.KEY_PWD,user.getPassword());
        if (user.getImage()==null){
            editor.putString(AppConstants.KEY_IMAGE,"");
        }
        else {
            editor.putString(AppConstants.KEY_IMAGE, user.getImage());
        }
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstants.KEY_EMAIL, null) != null;
    }

    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(AppConstants.KEY_USER_ID,-1),
                sharedPreferences.getString(AppConstants.KEY_FNAME, null),
                sharedPreferences.getString(AppConstants.KEY_MNAME, null),
                sharedPreferences.getString(AppConstants.KEY_LNAME, null),
                sharedPreferences.getString(AppConstants.KEY_ADDRESS, null),
                sharedPreferences.getString(AppConstants.KEY_PHONE, null),
                sharedPreferences.getString(AppConstants.KEY_EMAIL, null),
                sharedPreferences.getString(AppConstants.KEY_PWD,null),
                sharedPreferences.getString(AppConstants.KEY_IMAGE,null)
        );
    }

    public void clear(){

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx,MainActivity.class));
    }
}
