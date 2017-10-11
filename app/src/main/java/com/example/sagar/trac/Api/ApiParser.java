package com.example.sagar.trac.Api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.example.sagar.trac.Models.ProfileDataSettingModel;
import com.example.sagar.trac.Models.ProfileSettings;
import com.example.sagar.trac.Models.RegisterModel;
import com.example.sagar.trac.Utils.Constants;
import com.example.sagar.trac.Views.LoginAcivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sagar on 1/27/2017.
 */

public class ApiParser {

    public static RegisterModel RegisterDataFetch(String result) {
        RegisterModel registerModel = new RegisterModel();
        JSONObject jsonObject = null;

        JSONObject record_object = null;


        try {

            Log.e("You at",result);
            jsonObject = new JSONObject(result);

            String message = jsonObject.getString("message");

            if (message.equals("Login success") | message.equals("Register completed")) {
                record_object = jsonObject.getJSONObject("record");
                String name = record_object.getString("name");
                String email = record_object.getString("email_id");
                String password = record_object.getString("password");
                String userid = record_object.getString("user_id");

                String phone = record_object.getString("phone");
                String secure_phone = record_object.getString("secure_phone");
                String simSerial = record_object.getString("simSerial");
                String simOperatorName = record_object.getString("simOperatorName");
                registerModel.setEmail_id(email);
                registerModel.setName(name);
                registerModel.setUser_id(userid);
                registerModel.setPassword(password);
                registerModel.setPhone(phone);
                registerModel.setSecure_phone(secure_phone);
                registerModel.setSimSerial(simSerial);
                registerModel.setSimOperatorName(simOperatorName);
                registerModel.setMessage(message);
            } else {
                registerModel.setMessage(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return registerModel;
    }

    public static ArrayList<ProfileDataSettingModel> profiledata_parser(String result) {

        ArrayList<ProfileDataSettingModel> list = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            list = new ArrayList<>();
            list.clear();
            JSONArray record_object = jsonObject.getJSONArray("record");

            for (int i = 0; i < record_object.length(); i++) {

                JSONObject content_object = record_object.getJSONObject(i);
                ProfileDataSettingModel profileDataSettingModel = new ProfileDataSettingModel();
                String profile_id = content_object.getString("profile_id");
                String profile_content = content_object.getString("profile_content");

                String status = content_object.getString("status");
                String profile_name = content_object.getString("profile_name");
                StringToHashMapConverter(profile_content);
//                Log.d("APP DEBUG",""+StringToHashMapConverter(profile_content).toString());
                profileDataSettingModel.setProfile_id(profile_id);
                profileDataSettingModel.setProfile_content(StringToHashMapConverter(profile_content));   // covert string to hash map

                profileDataSettingModel.setStatus(status);
                profileDataSettingModel.setProfile_name(profile_name);
                list.add(profileDataSettingModel);


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return list;
    }


    public static HashMap<String, String> StringToHashMapConverter(String profile_content) {

        profile_content = profile_content.substring(1, profile_content.length() - 1);           //remove curly brackets
        String[] keyValuePairs = profile_content.split(",");

        HashMap<String, String> map = new HashMap<>();


        for (String pair : keyValuePairs)                        //iterate over the pairs
        {
            String[] entry = pair.split("=");                   //split the pairs to get key and value
            if (entry.length == 2) {
                map.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces

            }
        }
        return map;
    }

    public static HashMap<String, String> GetProfileID(String response) {
        HashMap<String, String> map = new HashMap<>();
        String profile_id = "";
        String message = "";
        try {
            JSONObject jsonObject = new JSONObject(response);

            message = jsonObject.getString("message");
            if (!message.equals("Profile exist,Try with other name")) {
                JSONObject jsonObject1 = jsonObject.getJSONObject("record");
                profile_id = jsonObject1.getString("profile_id");
                map.put("message", message);
                map.put("profile_id", profile_id);
            } else {
                map.put("message", message);
                map.put("profile_id", "");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String getMessageFromLocationUpdate(String response) {
        String message = "";
        try {
            JSONObject jsonobject = new JSONObject(response);
            message = jsonobject.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }

    public static HashMap<String, String> GetLocationData(String response) {
         HashMap<String,String> map= new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject Event = jsonObject.getJSONObject("Event");

            String status = Event.getString("status");


            if (!status.equals("no nearby profile Found")) {

                JSONArray Details = Event.getJSONArray("Details");
                JSONObject detail_content = Details.getJSONObject(0);
                String Location_id = detail_content.getString("Location_id");
                String profile_id = detail_content.getString("profile_id");
                String user_id = detail_content.getString("user_id");
                String latitude = detail_content.getString("latitude");
                String longitude = detail_content.getString("longitude");
                String distance = detail_content.getString("distance");

                map.put(Constants.PROFILE_ID,profile_id);
                map.put(Constants.LOCATION_ID,Location_id);
                map.put(Constants.USER_ID,user_id);
                map.put(Constants.LATITUDE,latitude);
                map.put(Constants.LONGITUDE,longitude);
                map.put(Constants.DISTANCE,distance);

            }
             map.put(Constants.STATUS,status);

        } catch (JSONException e) {
            e.printStackTrace();
        }

     return map;
    }
    public static byte[] getFileDataFromDrawable(Context context, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
