package com.example.sagar.trac.Views;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;


import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sagar.trac.Api.ApiParser;
import com.example.sagar.trac.R;
import com.example.sagar.trac.Services.GPSTracker;
import com.example.sagar.trac.Utils.Constants;
import com.example.sagar.trac.Utils.GoogleAdressApiTask;
import com.example.sagar.trac.Utils.GoogleApiTaskCompleted;
import com.example.sagar.trac.Utils.Trac;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Sagar on 1/26/2017.
 */
public class MapsActivity extends FragmentActivity {
    double MyLat = 0.0, MyLong = 0.0;
    Trac trac;
    PopupWindow mpopup;
    View popUpView;
    LatLng latLng;
    String profile_id = "",addressText;
    GoogleMap googleMap;
    Dialog dialog;
    GPSTracker gps;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initMap();
        progressDialog= new ProgressDialog(MapsActivity.this);
        progressDialog.setMessage("Your position is "+Constants.LOADING_MSG);
        progressDialog.setCanceledOnTouchOutside(false);
        gps=new GPSTracker(this);

        trac = (Trac) getApplication();
        getIntentData();

    }

    private void getIntentData() {
        profile_id = getIntent().getStringExtra(Constants.PROFILE_ID);
    }


    private void initMap() {

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        // Getting a reference to the map


        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mgoogleMap) {
                googleMap=mgoogleMap;

                // Getting a reference to the map

                gps= new GPSTracker(MapsActivity.this);
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                googleMap.setMaxZoomPreference(120);
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.setTrafficEnabled(true);
                googleMap.setMinZoomPreference(6.0f);
                googleMap.setMaxZoomPreference(14.0f);
                googleMap.setTrafficEnabled(true);
                googleMap.setBuildingsEnabled(true);
                googleMap.setIndoorEnabled(true);


                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    boolean success = googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    MapsActivity.this, R.raw.style_row));

                    if (!success) {
                        Log.e("APP_DEBUG", "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e("ERROR_APP_DEBUG", "Can't find style. Error: ", e);
                }



                LatLng latLng = new LatLng(gps.getLatitude(), gps.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                googleMap.animateCamera(cameraUpdate);
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    public void onMapClick(LatLng point) {
                        Toast.makeText(MapsActivity.this,
                                point.latitude + ", " + point.longitude,
                                Toast.LENGTH_SHORT).show();
                        MyLat = point.latitude;
                        MyLong = point.longitude;
                        LatLng latLng = new LatLng(MyLat, MyLong);

                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("Profile Point"));


                        //  Toast.makeText(MapsActivity.this, "lat : " + MyLat + "long : " + MyLong, Toast.LENGTH_SHORT).show();

                        if (MyLong == 0.0 && MyLat == 0.0) {
                            Toast.makeText(MapsActivity.this, " Gps is not stable ..Please wait... ", Toast.LENGTH_SHORT).show();

                        } else {
                            //    progressDialog.show();
                            //  getAddressFromMap();
//                            new GeocoderTask().execute();
                            String latlog=String.valueOf(MyLat)+","+String.valueOf(MyLong);

                            String url="http://maps.googleapis.com/maps/api/geocode/json?latlng="+latlog+"&sensor=true";




                            GoogleAdressApiTask googleAdressApiTask= new GoogleAdressApiTask(MapsActivity.this,url,new GoogleApiTaskCompleted(){
                                @Override
                                public void onFinishApi(String address) {


                                  if (!address.contains("ZERO_RESULTS")){
                                      try {
                                          JSONObject jsonObject= new JSONObject(address);
                                          JSONArray jnResultArray= jsonObject.getJSONArray("results");

                                          JSONObject addressObject=jnResultArray.getJSONObject(0);
                                          String addressline=addressObject.getString("formatted_address");
                                          LocationAddedPopUp(addressline,"","");
                                      } catch (JSONException e) {
                                          e.printStackTrace();
                                      }
                                  }else {
                                      Toast.makeText(MapsActivity.this,"No Address find",Toast.LENGTH_SHORT).show();

                                  }



                                }
                            } );
                            googleAdressApiTask.execute();



                        }

                    }
                });
            }
        });

            }


            private void getAddressFromMap() {
                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(MyLat, MyLong, 1);
                    String cityName = addresses.get(0).getAddressLine(0);
                    String stateName = addresses.get(0).getAddressLine(1);
                    String countryName = addresses.get(0).getAddressLine(2);

                    Log.d(Constants.APP_DEBUG, "cityName " + cityName);
                    Log.d(Constants.APP_DEBUG, "stateName " + stateName);
                    Log.d(Constants.APP_DEBUG, "countryName " + countryName);
                    progressDialog.dismiss();
                    LocationAddedPopUp(cityName, stateName, countryName);

                } catch (IOException e) {
                    progressDialog.dismiss();
                    LocationAddedPopUp("", "", "");
                    e.printStackTrace();
                }
            }

            private void LocationAddedPopUp(String cityName, String stateName, String countryName) {

//        popUpView = getLayoutInflater().inflate(R.layout.address_layout,
//                null); // inflating popup layout
//        mpopup = new PopupWindow(popUpView, LinearLayoutCompat.LayoutParams.MATCH_PARENT,
//                LinearLayoutCompat.LayoutParams.WRAP_CONTENT, true); // Creation of popup
//        mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
//        mpopup.showAtLocation(popUpView, Gravity.CENTER, 0, 0); // Displaying popup


                dialog = new Dialog(MapsActivity.this);
                dialog.setContentView(R.layout.address_layout);
                dialog.setTitle("Address");

                TextView Address;
                Button save, cancel;

                Address = (TextView) dialog.findViewById(R.id.Address);
                save = (Button) dialog.findViewById(R.id.save);
                cancel = (Button) dialog.findViewById(R.id.cancel);
                if (cityName != "") {
                    Address.setText(cityName+" \n " + MyLat + "-" + MyLong);
                } else {
                    Address.setText(MyLat + "-" + MyLong + "\n" + cityName + "\n" + stateName + "," + countryName);

                }
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaveLocation();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }

            private void SaveLocation() {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Trac.getInstance().getIPAddress() + Constants.LOCATION_INSERT,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {


                                String message = ApiParser.getMessageFromLocationUpdate(response);
                                if (message.equals("Location Added Successfully")) {
                                    dialog.dismiss();
                                    Toast.makeText(MapsActivity.this, message.toString(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MapsActivity.this, message.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MapsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("longitude", String.valueOf(MyLong));
                        params.put("latitude", String.valueOf(MyLat));
                        params.put("user_id", trac.getUserId());
                        params.put("profile_id", profile_id);
                        return params;
                    }

                };

                RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
                requestQueue.add(stringRequest);
            }


            public void openDialog(String st) {

                String sdest = st;

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);


                alertDialogBuilder.setTitle("Please choose an action!");
                alertDialogBuilder.setMessage("Would you like to continue with the selected loation :\n" + st);

                alertDialogBuilder.setPositiveButton("Submit Destination", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {


                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
