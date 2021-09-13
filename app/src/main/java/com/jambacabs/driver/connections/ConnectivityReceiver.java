package com.jambacabs.driver.connections;

/**
 * Created by Lincoln on 18/03/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityReceiver
        extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1)
    {

        if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(arg1.getAction())) {

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean is_connected = false;
            if (isGpsEnabled || isNetworkEnabled) {
                is_connected = true;
            }

            if (connectivityReceiverListener != null)
            {
                connectivityReceiverListener.onProviderChange(is_connected);
            }
        }else
        {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            assert cm != null;
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected =  false;
            if (activeNetwork != null) {
                isConnected = activeNetwork.isConnectedOrConnecting();
            }
            if (connectivityReceiverListener != null)
            {
                connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
            }
        }
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
        void onProviderChange(boolean isConected);
    }
}