package com.example.sagar.trac.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;


import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sagar.trac.Api.ApiParser;
import com.example.sagar.trac.Models.ProfileSettings;
import com.example.sagar.trac.R;
import com.example.sagar.trac.Utils.Constants;
import com.example.sagar.trac.Utils.Trac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

public class ProfileSetupPage extends AppCompatActivity implements Switch.OnCheckedChangeListener {

    private Spinner mode_selection;
    private ImageView locationFinder;

    private EditText
            profilename,
            sms_message;

    private Switch
            wifi_switch,
            bluetooth_switch,

    mobile_data_switch,
            audio_profile_switch,

    message_reading;

    Realm mRealm;
    Trac trac;
    ProfileSettings profileSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup_page);
        mRealm.init(this);
        mRealm = Realm.getDefaultInstance();


        initView();

        SetModeSpinnerData();

    }

    private void SetModeSpinnerData() {

        ArrayList<String> modelist = new ArrayList<>();

        modelist.add(Constants.NORMAL);
        modelist.add(Constants.VIBRATE);
        modelist.add(Constants.SILENT);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, modelist);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mode_selection.setAdapter(dataAdapter);

    }

    private void initView() {

        trac = (Trac) getApplication();

        profilename = (EditText) findViewById(R.id.profilename);
        mode_selection = (Spinner) findViewById(R.id.mode_selection);

        wifi_switch = (Switch) findViewById(R.id.wifi_switch);
        bluetooth_switch = (Switch) findViewById(R.id.bluetooth_switch);
        mobile_data_switch = (Switch) findViewById(R.id.mobile_data_switch);
        audio_profile_switch = (Switch) findViewById(R.id.audio_profile_switch);
        sms_message = (EditText) findViewById(R.id.sms_message);
        message_reading = (Switch) findViewById(R.id.message_reading);

        audio_profile_switch.setOnCheckedChangeListener(this);
        wifi_switch.setOnCheckedChangeListener(this);
        bluetooth_switch.setOnCheckedChangeListener(this);
        mobile_data_switch.setOnCheckedChangeListener(this);
        message_reading.setOnCheckedChangeListener(this);




    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        //Ui update on switch state change occurs//

        switch (buttonView.getId()) {
            case R.id.audio_profile_switch:

                if (isChecked) {
                    mode_selection.setVisibility(View.VISIBLE);
                } else {
                    mode_selection.setVisibility(View.GONE);
                }
                break;


        }


    }

    public void Save(View v) {

        final HashMap<String, Object> prolist = GetDataValue();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Trac.getInstance().getIPAddress()+Constants.PROFILE_SETTING_SAVE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APP", " response :-" + response);

                        HashMap<String, String> map = ApiParser.GetProfileID(response);

                        if (map.get("message").equals("Profile added success")) {
                            Toast.makeText(ProfileSetupPage.this, "Profile Added Successfully..", Toast.LENGTH_LONG).show();

                            Toast.makeText(ProfileSetupPage.this, "response : "+response, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ProfileSetupPage.this, MapsActivity.class);
                            intent.putExtra(Constants.PROFILE_ID, map.get("profile_id"));
                            startActivity(intent);
                            finish();
                        }else {
                           String error= map.get("message");
                            Toast.makeText(ProfileSetupPage.this, "Status : "+error, Toast.LENGTH_SHORT).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileSetupPage.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("profile_name", profilename.getText().toString().trim());
                params.put("profile_content", prolist.toString());
                params.put("status", "0");
                params.put("user_id", trac.getUserId());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private HashMap<String, Object> GetDataValue() {
        // Create a new object

        HashMap<String, Object> proMap = new HashMap<>();


        if (wifi_switch.isChecked()) {

            proMap.put(Constants.ISWIFI, true);
        } else {

            proMap.put(Constants.ISWIFI, false);
        }

        if (audio_profile_switch.isChecked()) {

            proMap.put(Constants.IS_PROFILE_AUDIO, true);
            proMap.put(Constants.MODE_SELECTION, mode_selection.getSelectedItem().toString());

        } else {

            proMap.put(Constants.IS_PROFILE_AUDIO, false);
        }

        if (bluetooth_switch.isChecked()) {
            proMap.put(Constants.IS_BLUETOOTH, true);

        } else {
            proMap.put(Constants.IS_BLUETOOTH, false);

        }
        if (mobile_data_switch.isChecked()) {
            proMap.put(Constants.IS_MOBILE_DATA, true);

        } else {
            proMap.put(Constants.IS_MOBILE_DATA, false);

        }
        if (message_reading.isChecked()) {
            proMap.put(Constants.IS_MESSAGE_READ, true);

        } else {
            proMap.put(Constants.IS_MESSAGE_READ, false);

        }
        proMap.put(Constants.PROFILE_NAME, profilename.getText().toString().trim());
        proMap.put(Constants.USERID, trac.getUserId());


        return proMap;
    }
}
