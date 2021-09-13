package com.jambacabs.driver.connections;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

public class MyLocationReceiver extends BroadcastReceiver {
    private static LocationReceiverListener locationReceiver;
    public MyLocationReceiver() {
        super();
    }

    public static LocationReceiverListener getLocationReceiver() {
        return locationReceiver;
    }

    public static void setLocationReceiver(LocationReceiverListener locationReceiver) {
        MyLocationReceiver.locationReceiver = locationReceiver;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null)
        {
            double latitude = intent.getDoubleExtra("latitude", 0);
            double longitude = intent.getDoubleExtra("longitude", 0);
            Location location = new Location("");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            getLocationReceiver().onLocationUpdate(location);
        }

    }

    public interface LocationReceiverListener {
        void onLocationUpdate(Location location);
    }
}
