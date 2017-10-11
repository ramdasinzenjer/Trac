package com.example.sagar.trac.Views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;


import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sagar.trac.Api.ApiParser;
import com.example.sagar.trac.Models.RegisterModel;
import com.example.sagar.trac.R;
import com.example.sagar.trac.Services.CameraService;
import com.example.sagar.trac.Utils.Constants;
import com.example.sagar.trac.Utils.Trac;
import com.example.sagar.trac.Utils.Utility;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class LoginAcivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
//    boolean permissionCheck;
    String simSerial, simOperatorName,simOperatorCode,simCountry;
    int permissionCheck;
    EditText Email, Password;
    Trac trac;
    TextToSpeech t1;
    ProgressDialog progressDialog;
    final static int REQUEST_READ_PHONE_STATE=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acivity);
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage(Constants.LOADING_MSG);
        progressDialog.setCanceledOnTouchOutside(false);

        t1 = new TextToSpeech(this, this);
        initView();



        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO

        }




//        permissionCheck = Utility.checkPermissions(this, Manifest.permission.ACCESS_WIFI_STATE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.SEND_SMS,Manifest.permission.SEND_SMS);
//        initView();
//        if (permissionCheck) {
//
//        } else {
//            Toast.makeText(this, "Need permission ON.", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    private void initView() {

        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        trac = (Trac) getApplication();

    }

    public void Login(View v) {
        if (Email.getText().toString().trim().length()!=0 && Password.getText().toString().trim().length()!=0){
            UserLogin();
            progressDialog.show();
        }else {
            Toast.makeText(LoginAcivity.this, "invalid entry", Toast.LENGTH_SHORT).show();
        }


    }

    private void UserLogin() {
//        startService(new Intent(LoginAcivity.this, CameraService.class));
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Trac.getInstance().getIPAddress()+Constants.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        RegisterModel registerModel = ApiParser.RegisterDataFetch(response);

//                        Log.e(Constants.ERROR_TAG, registerModel.getEmail_id());
//                        Log.e(Constants.ERROR_TAG, registerModel.getMessage());
//                        Log.e(Constants.ERROR_TAG, registerModel.getName());
//                        Log.e(Constants.ERROR_TAG, registerModel.getPassword());
                        Log.e(Constants.ERROR_TAG, response.toString());


                        if (registerModel.getMessage().equals("Login success")) {


                            if (registerModel.getSimSerial().equals(simSerial)){

                                Intent mintent = new Intent(LoginAcivity.this,CameraService.class);
                            mintent.putExtra("OwnerUser",trac.getEmail());
                            mintent.putExtra("phone_secure",trac.getSecure_phone());
                            mintent.putExtra("userId",trac.getUserId());

                                Log.d(Constants.APP_DEBUG,""+Trac.getInstance().getEmail());
                                Log.d(Constants.APP_DEBUG,""+Trac.getInstance().getSecure_phone());

                                startService(mintent);

                           }

                            trac.setUserId(registerModel.getUser_id());
                            trac.setLoginStatus(true);
//                            trac.setSecure_phone(registerModel.getSecure_phone());
//                            trac.setSim_serial(registerModel.getSimSerial());
//                            trac.setPhone(registerModel.getPhone());
//                            trac.setEmail(registerModel.getEmail_id());
//                            trac.setSim_operator_name(registerModel.getSimOperatorName());
                            progressDialog.dismiss();
                            Toast.makeText(LoginAcivity.this,trac.getUserId(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginAcivity.this, HomePage.class);
                            startActivity(intent);
                            finish();

                        }else {
                            Toast.makeText(LoginAcivity.this,registerModel.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginAcivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", Email.getText().toString().trim());
                params.put("password", Password.getText().toString().trim());

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void Register(View v) {
        Intent intent = new Intent(LoginAcivity.this, RegisterActivity.class);
        startActivity(intent);
    }
    private void GettingSimCardDetails() {
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telephonyManager.getSimState();

        switch (simState) {

            case (TelephonyManager.SIM_STATE_ABSENT): break;
            case (TelephonyManager.SIM_STATE_NETWORK_LOCKED): break;
            case (TelephonyManager.SIM_STATE_PIN_REQUIRED): break;
            case (TelephonyManager.SIM_STATE_PUK_REQUIRED): break;
            case (TelephonyManager.SIM_STATE_UNKNOWN): break;
            case (TelephonyManager.SIM_STATE_READY): {

                // Get the SIM country ISO code
                simCountry = telephonyManager.getSimCountryIso();

                // Get the operator code of the active SIM (MCC + MNC)
                simOperatorCode = telephonyManager.getSimOperator();

                // Get the name of the SIM operator
                simOperatorName = telephonyManager.getSimOperatorName();

                // Get the SIM’s serial number
                simSerial = telephonyManager.getSimSerialNumber();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GettingSimCardDetails();   //IMEI data.....

                }
                break;
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = t1.setLanguage(Locale.US);

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


        String Welcome= Constants.APP_WELCOME_NOTE+" ";

        t1.speak(Welcome, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        t1.shutdown();
    }

}
