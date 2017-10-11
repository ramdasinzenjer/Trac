package com.example.sagar.trac.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.sagar.trac.Utils.Constants;
import com.example.sagar.trac.Utils.Trac;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Settings;
import com.klinker.android.send_message.Transaction;

import java.util.Locale;

/**
 * Created by Sagar on 2/16/2017.
 */

public class IncomingSms extends BroadcastReceiver implements TextToSpeech.OnInitListener{
    TextToSpeech t1;
    Context context;
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {
        this. context=context;
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);

                    ReadMessagteType(message,senderNum);

                     ReadMessage(senderNum,message,context);

                    // Show Alert
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,
                            "senderNum: "+ senderNum + ", message: " + message, duration);
                    toast.show();

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }

    private void ReadMessagteType(String message, String senderNum) {

        switch (message){
            case Constants.MODE_SMS_DETECTION:
                ModeChanger();
                break;
            case Constants.MODE_LOCATION_FETCH:
                LocationUpdator(senderNum);
                break;

        }


    }

    private void ModeChanger() {

        final AudioManager mode = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        mode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
          r.stop();
            }
        }, 5000);




    }

    private void LocationUpdator(String senderNum) {


        String locationString=getPhoneLocation();
        Settings sendSettings = new Settings();
        Transaction sendTransaction = new Transaction(context, sendSettings);
        Message mMessage = new Message("Your phone location "+locationString, senderNum);
        sendTransaction.sendNewMessage(mMessage, Transaction.NO_THREAD_ID);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(senderNum, null, "Your phone location "+locationString, null, null);



    }

    private String getPhoneLocation() {

        GPSTracker gps = new GPSTracker(context);
        String lat_long="";
        if (gps!=null){
           lat_long = "latitude :"+gps.getLatitude()+" , "+"longitude :"+gps.getLongitude();
        }
        return lat_long;
    }

    private void ReadMessage(final String senderNum, final String message, Context context) {
        t1=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    int result = t1.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    } else {

                        speakOut(senderNum,message);
                    }

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });





    }
    private void speakOut(String senderNum, String message) {


        String Welcome= Constants.WELCOME+" ";
        String messagebody=Constants.SENDER+" "+message;
        t1.speak(Welcome +messagebody, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    public void onInit(int status) {

    }
}