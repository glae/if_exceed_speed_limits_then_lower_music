package glae.speedlimitsmusic;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import static glae.speedlimitsmusic.PlaceholderFragment.TAG;

public class SpeedLimitsLocationListener implements LocationListener {


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "speed="+location.getSpeed()+"");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
