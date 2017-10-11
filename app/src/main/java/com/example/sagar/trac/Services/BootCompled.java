package com.example.sagar.trac.Services;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import com.example.sagar.trac.Utils.Trac;

/**
 * Created by Sagar on 2/17/2017.
 */

public class BootCompled extends BroadcastReceiver {

    final static int REQUEST_READ_PHONE_STATE=0;
    String simSerial, simOperatorName,simOperatorCode,simCountry;
    Trac trac;
    @Override
    public void onReceive(Context context, Intent intent) {
        trac=(Trac)context.getApplicationContext();
        String sim_Serail=GettingSimCardDetails(context);

        if (sim_Serail.equals(trac.getSim_serial())){
            getImageFromRobber(context);
        }


    }

    private void getImageFromRobber(Context context) {


        Intent in = new Intent(context,CameraService.class);
        context.startService(in);


    }


    private String GettingSimCardDetails(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telephonyManager.getSimState();

        switch (simState) {

            case (TelephonyManager.SIM_STATE_ABSENT): break;
            case (TelephonyManager.SIM_STATE_NETWORK_LOCKED): break;
            case (TelephonyManager.SIM_STATE_PIN_REQUIRED): break;
            case (TelephonyManager.SIM_STATE_PUK_REQUIRED): break;
            case (TelephonyManager.SIM_STATE_UNKNOWN): break;
            case (TelephonyManager.SIM_STATE_READY): {



                // Get the name of the SIM operator
                simOperatorName = telephonyManager.getSimOperatorName();

                // Get the SIM’s serial number
                simSerial = telephonyManager.getSimSerialNumber();
            }
        }
        return simSerial;
    }


}
