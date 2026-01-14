package com.example.binbuddy.data.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Utility class to check network connectivity.
 * Used by repositories to determine if offline caching should be used.
 */
public class NetworkChecker {
    
    private final Context context;
    private final ConnectivityManager connectivityManager;

    public NetworkChecker(Context context) {
        this.context = context.getApplicationContext();
        this.connectivityManager = (ConnectivityManager) 
            this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Check if device has active network connection.
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        if (connectivityManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Use NetworkCapabilities for Android M and above
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) {
                return false;
            }
            
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                   capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        } else {
            // Fallback for older Android versions
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    /**
     * Check if device has WiFi connection.
     * 
     * @return true if connected via WiFi, false otherwise
     */
    public boolean isWifiConnected() {
        if (connectivityManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) {
                return false;
            }
            
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && 
                   networkInfo.isConnected() && 
                   networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
    }

    /**
     * Check if device has mobile data connection.
     * 
     * @return true if connected via mobile data, false otherwise
     */
    public boolean isMobileDataConnected() {
        if (connectivityManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) {
                return false;
            }
            
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && 
                   networkInfo.isConnected() && 
                   networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
    }
}
