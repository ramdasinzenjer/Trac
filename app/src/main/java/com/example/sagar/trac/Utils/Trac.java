package com.example.sagar.trac.Utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;
import android.util.LruCache;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;



/**
 * Created by Sagar on 1/25/2017.
 */

public class Trac extends Application {
    String userId;
    Boolean loginStatus;
    String IPAddress;

    public String getLatitude() {
        latitude=pref.getString(Constants.LATITUDE,"");
        return latitude;
    }

    public void setLatitude(String latitude) {
        editor.putString(Constants.LATITUDE, latitude);
        editor.commit();
    }

    public String getLongitude() {
        longitude=pref.getString(Constants.LONGITUDE,"");
        return longitude;
    }

    public void setLongitude(String longitude) {
        editor.putString(Constants.LONGITUDE, longitude);
        editor.commit();
    }

    String latitude,longitude;
    private static Trac mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    Context mCtx;
    String tempid;

    public String getTempid() {
        tempid=pref.getString(Constants.TEMPID,"");
        return tempid;
    }

    public void setTempid(String tempid) {
        editor.putString(Constants.TEMPID, tempid);
        editor.commit();
    }

    //    private Trac(Context context) {
//        mCtx = context;
//        mRequestQueue = getRequestQueue();
//
//
//    }
public static synchronized Trac getInstance() {
    return mInstance;
}
    public String getSecure_phone() {

        secure_phone=pref.getString(Constants.SECURE_PHONE,"");
        return secure_phone;
    }

    public void setSecure_phone(String secure_phone) {
        editor.putString(Constants.SECURE_PHONE, secure_phone);
        editor.commit();
    }

    public String getSim_serial() {
            sim_serial=pref.getString(Constants.SIM_SERIAL,"");
        return sim_serial;
    }

    public void setSim_serial(String sim_serial) {
        editor.putString(Constants.SIM_SERIAL, sim_serial);
        editor.commit();
    }

    public String getSim_operator_name() {
        sim_operator_name=pref.getString(Constants.SIM_OPERATOR_NAME,"");
        return sim_operator_name;
    }

    public void setSim_operator_name(String sim_operator_name) {
        editor.putString(Constants.SIM_OPERATOR_NAME, secure_phone);
        editor.commit();
    }

    public String getPhone() {
        phone=pref.getString(Constants.PH_NO,"");
        return phone;
    }

    public void setPhone(String phone) {
        editor.putString(Constants.PH_NO, phone);
        editor.commit();
    }

    public String getEmail() {
        email=pref.getString(Constants.EMAIL,"");
        return email;
    }

    public void setEmail(String email) {
        editor.putString(Constants.EMAIL, email);
        editor.commit();
    }

    String phone,email,secure_phone,sim_serial,sim_operator_name;

    public String getIPAddress() {
        IPAddress=pref.getString(Constants.IPADDRESS,"");
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        editor.putString(Constants.IPADDRESS, IPAddress);
        editor.commit();
    }

    public Boolean getLoginStatus() {
        loginStatus = pref.getBoolean(Constants.LOGIN_STATUS, false);
        return loginStatus;
    }

    public void setLoginStatus(Boolean loginStatus) {
        editor.putBoolean(Constants.LOGIN_STATUS, loginStatus);
        editor.commit();
    }

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public String getUserId() {
        userId = pref.getString(Constants.USER_ID, "");
        return userId;
    }

    public void setUserId(String userId) {
        editor.putString(Constants.USER_ID, userId);
        editor.commit();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
         pref = getApplicationContext().getSharedPreferences(Constants.SHARED_DATA, MODE_PRIVATE);
        editor = pref.edit();

        mInstance = this;



    }
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
//    public static synchronized Trac getInstance(Context context) {
//        if (mInstance == null) {
//            mInstance = new Trac(context);
//        }
//        return mInstance;
//    }

    /**
     * Get current request queue.
     *
     * @return RequestQueue
     */
//    public RequestQueue getRequestQueue() {
//        if (mRequestQueue == null) {
//            // getApplicationContext() is key, it keeps you from leaking the
//            // Activity or BroadcastReceiver if someone passes one in.
//            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
//        }
//        return mRequestQueue;
//    }
//
//    /**
//     * Add new request depend on type like string, json object, json array request.
//     *
//     * @param req new request
//     * @param <T> request type
//     */
//    public <T> void addToRequestQueue(Request<T> req) {
//        getRequestQueue().add(req);
//    }
//
//    /**
//     * Get image loader.
//     *
//     * @return ImageLoader
//     */
//    public ImageLoader getImageLoader() {
//        return mImageLoader;
//    }
}
