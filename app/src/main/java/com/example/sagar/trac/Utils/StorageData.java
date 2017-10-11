package com.example.sagar.trac.Utils;

import com.example.sagar.trac.Models.ProfileDataSettingModel;

/**
 * Created by Sagar on 2/12/2017.
 */

public class StorageData   {
  static   ProfileDataSettingModel profileDataSettingModel;

    public static ProfileDataSettingModel getProfileDataSettingModel() {
        return profileDataSettingModel;
    }

    public static void setProfileDataSettingModel(ProfileDataSettingModel profileDataSettingModel) {
        StorageData.profileDataSettingModel = profileDataSettingModel;
    }
}
