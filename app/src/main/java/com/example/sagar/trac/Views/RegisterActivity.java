package com.example.sagar.trac.Views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.example.sagar.trac.Utils.Constants;
import com.example.sagar.trac.Utils.Trac;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText name,password,email,secure_phone,phone;
    Trac trac;
    final static int REQUEST_READ_PHONE_STATE=0;
    String simSerial, simOperatorName,simOperatorCode,simCountry;
    int permissionCheck;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        trac=(Trac)getApplication();
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage(Constants.LOADING_MSG);
        progressDialog.setCancelable(false);
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        initView();
    }

    private void initView() {

        name=(EditText)findViewById(R.id.name);
        password=(EditText)findViewById(R.id.password);
        email=(EditText)findViewById(R.id.email);
        secure_phone=(EditText)findViewById(R.id.secure_phone);
        phone=(EditText)findViewById(R.id.phone);
    }

    public void register(View v){

      if (SetValidationForm())
          progressDialog.show();
        RegisterUser();
    }

    private boolean SetValidationForm() {

        if (name.getText().toString().trim().length()==0){
            name.setError(Constants.NON_EMPTY_FIELD);
            return false;
        }
        if (email.getText().toString().trim().length()==0){
            email.setError(Constants.NON_EMPTY_FIELD);
            return false;
        }
        if (password.getText().toString().trim().length()==0){
            password.setError(Constants.NON_EMPTY_FIELD);
            return false;
        }
        if (phone.getText().toString().trim().length()==0){
            phone.setError(Constants.NON_EMPTY_FIELD);
            return false;
        }
        if (secure_phone.getText().toString().trim().length()==0){
            secure_phone.setError(Constants.NON_EMPTY_FIELD);
            return false;
        }
        if (phone.getText().toString().trim().equals(secure_phone.getText().toString().trim())){
            secure_phone.setError("Wrong ....Give another number for secure phone");
            return false;
        }
        if (phone.getText().toString().length()<10){
            phone.setError("Invalid phone number");
            return false;
        }
        if (secure_phone.getText().toString().length()<10){
            secure_phone.setError("Invalid phone number");
            return false;
        }
        return true;
    }

    private void RegisterUser() {
        GetPermissionSimDetails();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Trac.getInstance().getIPAddress()+Constants.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                      Toast.makeText(RegisterActivity.this,response,Toast.LENGTH_LONG).show();

                        Log.d(Constants.APP_DEBUG,"Register data : "+response);


                        RegisterModel registerModel= ApiParser.RegisterDataFetch(response);

                        if (registerModel.getMessage().equals("Register completed")){

                            trac.setUserId(registerModel.getUser_id());
                            trac.setLoginStatus(true);
                            trac.setSecure_phone(registerModel.getSecure_phone());
                            trac.setSim_serial(registerModel.getSimSerial());
                            trac.setPhone(registerModel.getPhone());
                            trac.setEmail(registerModel.getEmail_id());
                            trac.setSim_operator_name(registerModel.getSimOperatorName());
                            progressDialog.dismiss();
                            Intent intent= new Intent(RegisterActivity.this,HomePage.class);
                            startActivity(intent);
                            finish();

                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this,registerModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }




                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email_id",email.getText().toString().trim());
                params.put("password",password.getText().toString().trim());
                params.put("name", name.getText().toString().trim());
                params.put("phone",phone.getText().toString().trim());
                params.put("secure_phone",secure_phone.getText().toString().trim());
                params.put("simSerial",simSerial);
                params.put("simOperatorName",simOperatorName);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    public void GetPermissionSimDetails() {
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
            GettingSimCardDetails();   //IMEI data.....

        }
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                    GettingSimCardDetails();   //IMEI data.....

                }
                break;

            default:
                break;
        }
    }

}
