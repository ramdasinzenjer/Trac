package com.example.sagar.trac.Services;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.sagar.trac.Models.ProfileDataSettingModel;
import com.example.sagar.trac.Utils.Constants;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ProfileActivationServices extends Service {
    HashMap<String, String> profileMap;
    public static boolean ACTIVATE = true;
    public static boolean DEACTIVATE = true;
   final static int REQUEST_WIFI_STATE=3;
    Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        this.context=ProfileActivationServices.this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        profileMap = (HashMap<String, String>) intent.getExtras().getSerializable("ActivationService");
        String id=intent.getStringExtra("id");
        Intent mapintent= new Intent(ProfileActivationServices.this,ProfileActivate.class);
        mapintent.putExtra("profile_data",profileMap);
        mapintent.putExtra("id",id);
        mapintent.putExtra("latitude",intent.getStringExtra("latitude"));
        mapintent.putExtra("longitude",intent.getStringExtra("longitude"));
        mapintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(mapintent);



//        ProfileActivates(profileMap);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(ProfileActivationServices.this, "All profile services stopped", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




}
