package com.example.sagar.trac.Services;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sagar.trac.Utils.Constants;
import com.example.sagar.trac.Utils.Trac;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Sagar on 2/20/2017.
 */
public class ProfileActivate extends AppCompatActivity implements TextToSpeech.OnInitListener {
    HashMap<String,String> profileMap;

    private TextToSpeech tts;
    public static boolean ACTIVATE = true;
    public static boolean DEACTIVATE = true;
    final static int REQUEST_WIFI_STATE=3;
    public  static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    int permissionCheck;
    String Tempid="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileMap=(HashMap<String, String>) getIntent().getExtras().getSerializable("profile_data");
        Tempid=getIntent().getStringExtra("id");
        GPSTracker gps= new GPSTracker(this);
        tts = new TextToSpeech(this, this);
//        long latitude=Long.valueOf(getIntent().getStringExtra("latitude"));
//        long longitude=Long.valueOf(getIntent().getStringExtra("longitude"));


//       int i=0;
//        if (Trac.getInstance().getLongitude().equals("")){
//            Trac.getInstance().setLatitude(getIntent().getStringExtra(Constants.LATITUDE));
//            Trac.getInstance().setLongitude(getIntent().getStringExtra(Constants.LONGITUDE));
//
//
//
//        }else {
//        }
//
//        if (latitude==gps.getLatitude()&&longitude==gps.getLongitude()){
//
//            i=1;
//        }




        checkAndroidVersion();
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);



    }
    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
            ProfileActivates(profileMap);
        } else {
            // write your logic here
            ProfileActivates(profileMap);
        }

    }

    private void ProfileActivates(HashMap<String, String> profileDataSettingModel) {

        Log.d(Constants.APP_DEBUG, " profile contant" + profileDataSettingModel.toString());

        String wifi_status = profileDataSettingModel.get(Constants.ISWIFI);
        String bluetooth = profileDataSettingModel.get(Constants.IS_BLUETOOTH);
        String Auto_msg_read_status = profileDataSettingModel.get(Constants.IS_MESSAGE_READ);
        String mobile_data_status = profileDataSettingModel.get(Constants.IS_MOBILE_DATA);
        String auto_profile_change = profileDataSettingModel.get(Constants.IS_PROFILE_AUDIO);
        String mode = profileDataSettingModel.get(Constants.MODE_SELECTION);
        String email = profileDataSettingModel.get(Constants.EMAIL);


        Log.d(Constants.APP_DEBUG, "wifi : " + wifi_status);
        Log.d(Constants.APP_DEBUG, "bluetooth : " + bluetooth);
        Log.d(Constants.APP_DEBUG, "Auto_msg_read_status : " + Auto_msg_read_status);
        Log.d(Constants.APP_DEBUG, "mobile_data_status : " + mobile_data_status);
        Log.d(Constants.APP_DEBUG, "Profile mode : " + mode);
        Log.d(Constants.APP_DEBUG, "auto_profile_change : " + auto_profile_change);



        if (wifi_status.equals("true")) {
            WifiStatusUpdate();  //Checking wifi status(on or off) and changing the status
        }
        if (bluetooth.equals("true")) {

            BluetoothStatusUpdate();  //Changing bluetooth status On or Off

        }
        if (Auto_msg_read_status.equals("true")) {
            AutoReadMessageFunction();
        }
        if (mobile_data_status.equals("true")) {
            MobileDataStatus();
        }
        if (auto_profile_change.equals("true")) {

            AutoProfileUpdateStatus(mode);

        }
        BackgroundMonitorData(Tempid,"0");

    }

    private void BackgroundMonitorData(final String profile_id, final String status) {



        StringRequest stringRequest = new StringRequest(Request.Method.POST,Trac.getInstance().getIPAddress()+Constants.PROFILE_STATUS_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ProfileActivate.this,"Profile updated Successfully..",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileActivate.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("profile_id",profile_id);
                params.put("status",status);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivate.this);
        requestQueue.add(stringRequest);



    }

    private void MobileDataStatus() {
        if (getSimcardStatus()) {
            mobiledataenable(ACTIVATE);
        } else {
            mobiledataenable(DEACTIVATE);
        }
    }

    private boolean getSimcardStatus() {
        TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        if (simState == TelephonyManager.SIM_STATE_ABSENT) {
            return false;
        } else if (simState == TelephonyManager.SIM_STATE_NETWORK_LOCKED) {
            return false;
        } else if (simState == TelephonyManager.SIM_STATE_PIN_REQUIRED) {
            return false;
        } else if (simState == TelephonyManager.SIM_STATE_PUK_REQUIRED) {
            return false;
        } else if (simState == TelephonyManager.SIM_STATE_UNKNOWN) {
            return false;
        }
        return true;
    }

    private void BluetoothStatusUpdate() {
//Disable bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }

    private void AutoProfileUpdateStatus(String mode) {
        AudioManager am;
        am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        if (mode.equals(Constants.SILENT)) {
            //For Silent mode
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else if (mode.equals(Constants.VIBRATE)) {
            //For Vibrate mode
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        } else if (mode.equals(Constants.NORMAL)) {
            //For Normal mode
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }

    private void AutoReadMessageFunction() {
        IncomingSms mReceiver = new IncomingSms();
        IntentFilter filter = new IntentFilter();  //Filter for your receiver...
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mReceiver , filter);

//        unregisterReceiver(mReceiver );  **// Unregister**
    }

    private void WifiStatusUpdate() {  //On wifi if it is Off

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, REQUEST_WIFI_STATE);

            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(ACTIVATE);
            }


        } else {
            //TODO
        }


    }

    public void mobiledataenable(boolean enabled) {
        try {
            final ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class<?> conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(ProfileActivate.this,
                Manifest.permission.ACCESS_WIFI_STATE) + ContextCompat
                .checkSelfPermission(ProfileActivate.this,
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (ProfileActivate.this, Manifest.permission.ACCESS_WIFI_STATE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (ProfileActivate.this, Manifest.permission.CAMERA)) {

                Snackbar.make(ProfileActivate.this.findViewById(android.R.id.content),
                        "Please Grant Permissions to upload profile photo",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission
                                                .ACCESS_WIFI_STATE, Manifest.permission.CAMERA},
                                        PERMISSIONS_MULTIPLE_REQUEST);
                            }
                        }).show();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission
                                .ACCESS_WIFI_STATE, Manifest.permission.CAMERA},
                        PERMISSIONS_MULTIPLE_REQUEST);
            }
        } else {
            // write your logic code if permission already granted
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(cameraPermission && readExternalFile)
                    {
                        // write your logic here
                    }
                }
                else {
                    Snackbar.make(ProfileActivate.this.findViewById(android.R.id.content),
                            "Please Grant Permissions to upload profile photo",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(View v) {
                                    requestPermissions(
                                            new String[]{Manifest.permission
                                                    .ACCESS_WIFI_STATE, Manifest.permission.CAMERA},
                                            PERMISSIONS_MULTIPLE_REQUEST);
                                }
                            }).show();
                }
                break;
        }

        switch (requestCode) {
            case REQUEST_WIFI_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
                break;

            default:
                break;
        }

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
    private void speakOut() {

        String text = "Your Profile Activated";

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
