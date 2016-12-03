package com.ysh.demo.demo1.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by hasee on 2016/11/23.
 */

public class NetworkJugde {


    private NetworkJugde() {
    }

    private static class Holder {
        public static NetworkJugde networkJugde = new NetworkJugde();
    }

    public static NetworkJugde getInstance() {
        return Holder.networkJugde;
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
