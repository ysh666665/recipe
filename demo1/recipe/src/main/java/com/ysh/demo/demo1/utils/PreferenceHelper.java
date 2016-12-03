package com.ysh.demo.demo1.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by hasee on 2016/11/26.
 */

public class PreferenceHelper {

     public static SharedPreferences getUniquePublicPreference(Context mContext){
         return  PreferenceManager.getDefaultSharedPreferences(mContext);
     }

    public static SharedPreferences getActivityNamedPreference(Activity activity){
        return  activity.getPreferences(Activity.MODE_PRIVATE);
    }


}

