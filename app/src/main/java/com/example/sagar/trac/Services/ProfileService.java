package com.example.sagar.trac.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sagar.trac.Api.ApiParser;
import com.example.sagar.trac.Models.ProfileDataSettingModel;
import com.example.sagar.trac.Utils.Constants;
import com.example.sagar.trac.Utils.Trac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sagar on 1/27/2017.
 */

public class ProfileService extends Service {
    GPSTracker gps;
    Handler h;
    String profile_distance="20";
     String lat;
     String lon;
    int delay = 10000; //milliseconds
     Trac trac;


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        gps = new GPSTracker(ProfileService.this);
        h = new Handler();

        Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();

        if (intent!=null){
            profile_distance=intent.getStringExtra("distance");
        }
        TimeTrackMachine();

        return super.onStartCommand(intent, flags, startId);
    }

    private void TimeTrackMachine() {


        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                LocationFetch();
                h.postDelayed(this, delay);
            }
        }, delay);


    }

    private void LocationFetch() {


        double _mylats = gps.getLatitude();
        double _mylongs = gps.getLongitude();
        Toast.makeText(ProfileService.this, "Service Locations : "+_mylats +"long : "+_mylongs, Toast.LENGTH_LONG).show();
        Log.d(Constants.APP_DEBUG, "" + _mylats + "long : " + _mylongs);

        LocationProfileActivator(_mylats, _mylongs);


    }

    private void LocationProfileActivator(double _mylats, double _mylongs) {
        lat=String.valueOf(_mylats);
        lon=String.valueOf(_mylongs);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Trac.getInstance().getIPAddress()+ Constants.PROFILE_ACTIVATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("APP", " response :-" + response);
if (!response.contains("no near by profile found")){
    HashMap<String, String> map = ApiParser.GetLocationData(response);

    GetProfileFromServer(map.get(Constants.PROFILE_ID));
}



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileService.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("coordinates", lat+"-"+lon);
                params.put("distance", profile_distance);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void GetProfileFromServer(final String profile_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Trac.getInstance().getIPAddress()+Constants.PROFILE_FETCH_FOR_ACTIVATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(ProfileService.this, "profile Activativat", Toast.LENGTH_SHORT).show();

                        ArrayList<ProfileDataSettingModel> list = ApiParser.profiledata_parser(response);
                     if (list!=null) {

                         for (int i=0;i<list.size();i++){
                             Intent intent = new Intent(ProfileService.this, ProfileActivationServices.class);
                             intent.putExtra("ActivationService", list.get(i).getProfile_content());
                             intent.putExtra("id",list.get(i).getProfile_id());
                             intent.putExtra("latitude",list.get(i).getLatitude());
                             intent.putExtra("longitude",list.get(i).getLongitude());
                             startService(intent);
                         }

                     }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileService.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("profileid", profile_id);


                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
