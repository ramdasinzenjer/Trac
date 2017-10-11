package com.example.sagar.trac.Models;

import io.realm.RealmObject;

/**
 * Created by Sagar on 1/25/2017.
 */

public class ProfileSettings{
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    String profileName,emailid,phone,audiomode,messageForModeSelection,userid;
    Boolean iswifi,isBluetooth,isGps,isMobileData,isModeSelection,isModeBySMS,isSimCardDetection,isHandFreeSMSReading;

    public String getAudiomode() {
        return audiomode;
    }

    public void setAudiomode(String audiomode) {
        this.audiomode = audiomode;
    }

    public String getMessageForModeSelection() {
        return messageForModeSelection;
    }

    public void setMessageForModeSelection(String messageForModeSelection) {
        this.messageForModeSelection = messageForModeSelection;
    }


    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIswifi() {
        return iswifi;
    }

    public void setIswifi(Boolean iswifi) {
        this.iswifi = iswifi;
    }

    public Boolean getBluetooth() {
        return isBluetooth;
    }

    public void setBluetooth(Boolean bluetooth) {
        isBluetooth = bluetooth;
    }

    public Boolean getGps() {
        return isGps;
    }

    public void setGps(Boolean gps) {
        isGps = gps;
    }

    public Boolean getMobileData() {
        return isMobileData;
    }

    public void setMobileData(Boolean mobileData) {
        isMobileData = mobileData;
    }

    public Boolean getModeSelection() {
        return isModeSelection;
    }

    public void setModeSelection(Boolean modeSelection) {
        isModeSelection = modeSelection;
    }

    public Boolean getModeBySMS() {
        return isModeBySMS;
    }

    public void setModeBySMS(Boolean modeBySMS) {
        isModeBySMS = modeBySMS;
    }

    public Boolean getSimCardDetection() {
        return isSimCardDetection;
    }

    public void setSimCardDetection(Boolean simCardDetection) {
        isSimCardDetection = simCardDetection;
    }

    public Boolean getHandFreeSMSReading() {
        return isHandFreeSMSReading;
    }

    public void setHandFreeSMSReading(Boolean handFreeSMSReading) {
        isHandFreeSMSReading = handFreeSMSReading;
    }
}
