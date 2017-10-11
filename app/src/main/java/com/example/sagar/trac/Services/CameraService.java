package com.example.sagar.trac.Services;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;



import com.example.sagar.trac.HiddenCameraFolder.CameraConfig;
import com.example.sagar.trac.HiddenCameraFolder.CameraError;
import com.example.sagar.trac.HiddenCameraFolder.HiddenCameraService;
import com.example.sagar.trac.HiddenCameraFolder.HiddenCameraUtils;
import com.example.sagar.trac.HiddenCameraFolder.config.CameraFacing;
import com.example.sagar.trac.HiddenCameraFolder.config.CameraImageFormat;
import com.example.sagar.trac.HiddenCameraFolder.config.CameraResolution;
import com.example.sagar.trac.Utils.ConnectivityReceiver;
import com.example.sagar.trac.Utils.Constants;
import com.example.sagar.trac.Utils.PermissionActivity;
import com.example.sagar.trac.Utils.Trac;
import com.example.sagar.trac.Utils.mail.GMailSender;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Settings;
import com.klinker.android.send_message.Transaction;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.util.UUID;

/**
 * Created by Sagar on 2/18/2017.
 */
public class CameraService extends HiddenCameraService {
    Context context;
String OwnerUser="",phone_secure,userId;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=CameraService.this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        OwnerUser=intent.getStringExtra("OwnerUser");
        userId=intent.getStringExtra("userId");
        phone_secure=intent.getStringExtra("phone_secure");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            if (HiddenCameraUtils.canOverDrawOtherApps(this)) {
                CameraConfig cameraConfig = new CameraConfig()
                        .getBuilder(this)
                        .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                        .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                        .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                        .build();

                startCamera(cameraConfig);

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        takePicture();
                    }
                }, 10000);
            } else {

                //Open settings to grant permission for "Draw other apps".
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
            }
        } else {
            //TODO Ask your parent activity for providing runtime permission
            Toast.makeText(this, "Camera permission not available", Toast.LENGTH_SHORT).show();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        Log.d("Image capture", imageFile.length() + "");


        if (ConnectivityReceiver.isConnected()) {
            GetImageSendFromSd(imageFile.getAbsolutePath(), imageFile.length());
            SendEmail(imageFile.getAbsolutePath());
        } else {
            if (PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("request_permissions", true) &&Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                startActivity(new Intent(this, PermissionActivity.class));

                return;
            }
            GPSTracker gps = new GPSTracker(context);
            String lat=String.valueOf(gps.getLatitude());
            String lon=String.valueOf(gps.getLongitude());
            sendSms(phone_secure,"lat : "+lat+"Lon :"+lon,true);
        }


        stopSelf();
    }
    private void sendSms(String phonenumber,String message, boolean isBinary)
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phonenumber, null, message, null, null);


    }
    private void SendEmail(final String absolutePath) {

        new Thread(new Runnable() {

            public void run() {

                try {

                        GMailSender sender = new GMailSender(

                            "sagar.vilakkupara@gmail.com",

                            "password1234");



                    sender.addAttachment(absolutePath);

                    sender.sendMail("User Image", "Image from your Trac app",

                            Trac.getInstance().getEmail(),

                            OwnerUser);


                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();



                }

            }

        }).start();



    }

    private void SendBySms(Bitmap absolutePath) {
        PackageManager pm = this.getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA)) {
            Toast.makeText(this, "Sorry, your device probably can't send SMS...", Toast.LENGTH_SHORT).show();
        }else {

            try {
                Settings sendSettings = new Settings();
                Transaction sendTransaction = new Transaction(context, sendSettings);
                Message mMessage = new Message("Some one is using your phone", "9605192985");
                mMessage.setImage(absolutePath);
                sendTransaction.sendNewMessage(mMessage, Transaction.NO_THREAD_ID);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }




        }

    }

    private void GetImageSendFromSd(String absolutePath, long length) {

        // loading or check internet connection or something...
        // ... then
        String url = Trac.getInstance().getIPAddress()+Constants.IMAGE_UPLOAD_URL;

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, url)
                    .addFileToUpload(absolutePath, "myimage") //Adding file
                    .addParameter("image_name", String.valueOf(length)) //Adding text parameter to the request
                    .addParameter("email", OwnerUser) //Adding text parameter to the request
                    .addParameter("userid", userId) //Adding text parameter to the request

                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
            Log.d(Constants.APP_DEBUG, "Response Co0mpleted");
        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

//        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("Response", response);
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(Constants.ERROR_TAG,"error tag :"+error);
//            }
//        });
//
//        smr.addFile("myimage", absolutePath);
////        smr.addMultipartParam("email", "text/plain", "sagarvlk@gmail.com");
////        smr.addMultipartParam("userid", "text/plain", "45");
////        smr.addMultipartParam("image_name", "text/plain", String.valueOf(length));
//
//
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(smr);
//        requestQueue.start();


    }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Toast.makeText(this, "Cannot open camera.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camra permission before initializing it.
                Toast.makeText(this, "Camera permission not available.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(this, "Your device does not have front camera.", Toast.LENGTH_LONG).show();
                break;
        }

        stopSelf();
    }
}