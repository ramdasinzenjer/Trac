package com.example.sagar.trac.Models;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Sagar on 1/28/2017.
 */

public class ProfileDataSettingModel implements Serializable {
    String latitude,longitude,status,profile_name,profile_id;
    HashMap<String,String> profile_content;

    public HashMap<String, String> getProfile_content() {
        return profile_content;
    }

    public void setProfile_content(HashMap<String, String> profile_content) {
        this.profile_content = profile_content;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }


    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }
}
