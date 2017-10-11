package com.example.sagar.trac.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;


import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sagar.trac.Adapters.ProfileListAdapter;
import com.example.sagar.trac.Api.ApiParser;
import com.example.sagar.trac.Models.ProfileDataSettingModel;

import com.example.sagar.trac.R;
import com.example.sagar.trac.Services.ProfileActivationServices;
import com.example.sagar.trac.Services.ProfileService;
import com.example.sagar.trac.Utils.Constants;
import com.example.sagar.trac.Utils.Trac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class HomePage extends AppCompatActivity {
    Realm mRealm;
    RecyclerView profileList;
    FloatingActionButton fab;
    ProfileListAdapter profileListAdapter;
    Switch ServiceActivationKey;
    Trac trac;
    SeekBar seekBar1;
    int Profile_Distance=0;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Realm.init(this);
        mRealm = Realm.getDefaultInstance();
        progressDialog=new ProgressDialog(HomePage.this);
        progressDialog.setMessage(Constants.LOADING_MSG);
        progressDialog.setCanceledOnTouchOutside(false);
        initView();
        RealmDataFetching();
        ProfileData();
        Log.d(Constants.APP_DEBUG,""+Trac.getInstance().getEmail());
        Log.d(Constants.APP_DEBUG,""+Trac.getInstance().getSecure_phone());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomePage.this, ProfileSetupPage.class);
                startActivity(intent);


                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ServiceActivationKey.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (Profile_Distance==0){
                        Toast.makeText(HomePage.this, "Change your profile distance", Toast.LENGTH_SHORT).show();
                        ServiceActivationKey.setChecked(false);

                    }else {

                        Intent intent= new Intent(HomePage.this,ProfileService.class);
                        intent.putExtra("distance",String.valueOf(Profile_Distance));
                        startService(intent);
                    }
                } else {
                    stopService(new Intent(HomePage.this, ProfileService.class));
                    stopService(new Intent(HomePage.this, ProfileActivationServices.class));
                }
            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();

        initView();
        RealmDataFetching();
        ProfileData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        RealmDataFetching();
        ProfileData();
    }

    private void ProfileData() {

          progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Trac.getInstance().getIPAddress()+Constants.PROFILE_DATA_FETCH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e(Constants.ERROR_TAG, response.toString());

                        if (!response.contains("no data found")){
                            ArrayList<ProfileDataSettingModel> list = ApiParser.profiledata_parser(response);
                            profileListAdapter = new ProfileListAdapter(HomePage.this, list);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            profileList.setLayoutManager(mLayoutManager);
                            profileList.setItemAnimator(new DefaultItemAnimator());
                            profileList.setAdapter(profileListAdapter);
                            progressDialog.dismiss();
                        }else{
                            Toast.makeText(trac, "No active profile ", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomePage.this, error.toString(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", trac.getUserId());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    private void RealmDataFetching() {

//        RealmResults<ProfileSettings> results = mRealm.where(ProfileSettings.class).findAll();

//        profileListAdapter = new ProfileListAdapter(HomePage.this,results);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        profileList.setLayoutManager(mLayoutManager);
//        profileList.setItemAnimator(new DefaultItemAnimator());
//        profileList.setAdapter(profileListAdapter);


//        for (int i=0;i<results.size();i++){
////            Log.d(Constants.APP_DEBUG,"data Second:"+results.get(i).toString());
//        }


    }

    private void initView() {

        trac = (Trac) getApplication();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        profileList = (RecyclerView) findViewById(R.id.profileList);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        ServiceActivationKey = (Switch) findViewById(R.id.ServiceActivationKey);
        seekBar1=(SeekBar)findViewById(R.id.distance_selector);
        seekBar1.setMax(20);
         seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                 Profile_Distance=progress;
                 Toast.makeText(HomePage.this,"Distance :"+Profile_Distance,Toast.LENGTH_SHORT).show();

             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {

             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {

             }
         });
    }


}
