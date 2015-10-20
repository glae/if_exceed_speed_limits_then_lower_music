package glae.speedlimitsmusic;

import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.media.AudioManager.STREAM_MUSIC;
import static java.util.concurrent.TimeUnit.SECONDS;

public class LabelsUpdater implements Runnable {

    private final AudioManager audioManager;
    private final LocationManager locationManager;
    private final TextView speedText;
    private final TextView limitText;
    private final TextView volumeText;
    private final TextView detailText;
    HttpClient client;
    SpeedLimitsLocationListener locationListener;
    VolumeUpdater updater;

    private int limitInKmH;
    private int speedInKmH;

    public LabelsUpdater(AudioManager audioManager, LocationManager locationManager, TextView speedText, TextView limitText, TextView volumeText, TextView detailText) {
        this.audioManager = audioManager;
        this.locationManager = locationManager;
        this.speedText = speedText;
        this.limitText = limitText;
        this.volumeText = volumeText;
        this.detailText = detailText;
        client = new DefaultHttpClient();
        locationListener = new SpeedLimitsLocationListener();
        updater = new VolumeUpdater();
        limitInKmH = 0;
        speedInKmH = 0;
    }

    @Override
    public void run() {

        locationManager.requestLocationUpdates(GPS_PROVIDER, SECONDS.toMillis(SpeedLimitsFragment.REFRESH_TIME_IN_SECONDS), 0, locationListener);
        Location lastKnownLocation = locationManager.getLastKnownLocation(GPS_PROVIDER);

        if (lastKnownLocation != null) {
            refreshSpeed(lastKnownLocation);
            refreshSpeedLimit(lastKnownLocation);
            refreshMusicVolume();
        } else {
            speedText.setText(SpeedLimitsFragment.NO_CONTENT);
            limitText.setText(SpeedLimitsFragment.NO_CONTENT);
            volumeText.setText(SpeedLimitsFragment.NO_CONTENT);
            Log.d(SpeedLimitsFragment.TAG, "lastKnowLocation is null");
        }
    }

    private void refreshSpeedLimit(Location lastKnownLocation) {

        lastKnownLocation.getLatitude();//???
        String url = SpeedLimitsFragment.NOKIA_HERE_BASE_URL_API+ lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
        String nokiaHereJson = getNokiaHere(url);

        try {
            setSpeedLimitLabel(nokiaHereJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSpeedLimitLabel(String nokiaHereJson) throws JSONException {
        JSONObject json = new JSONObject(nokiaHereJson);

        JSONObject response = json.getJSONObject("Response");
        if (response != null) {
            JSONArray link = response.getJSONArray("Link");
            if (link != null) {
                JSONObject jsonLink = link.getJSONObject(0);
                if (jsonLink != null) {
                    JSONObject dynamicSpeedInfo = jsonLink.getJSONObject("DynamicSpeedInfo");
                    if (dynamicSpeedInfo != null) {
                        double speedLimit = dynamicSpeedInfo.getDouble("BaseSpeed");
                        limitInKmH = roundAndConvertToKmph(speedLimit);
                        limitText.setText(Integer.toString(limitInKmH));
                        Log.d(SpeedLimitsFragment.TAG, "limit=" + limitText);

                        setDetailLabel(jsonLink);
                    }
                }
            }
        }
    }

    private void setDetailLabel(JSONObject jsonLink) throws JSONException {
        //Label, City (county, state, country)
        JSONObject address = jsonLink.getJSONObject("Address");
        if (address != null) {

            StringBuilder stringBuilder = new StringBuilder();

            String label = address.getString("Label");
            if (label != null) stringBuilder.append(label);

            String city = address.getString("City");
            if (city != null) stringBuilder.append(", ").append(city);

            String county = address.getString("County");
            if (county != null) stringBuilder.append(" (").append(county);

            String state = address.getString("State");
            if (state != null) stringBuilder.append(", ").append(state);

            String country = address.getString("Country");
            if (country != null) stringBuilder.append(", ").append(country).append(")");

            if (!stringBuilder.toString().isEmpty()) detailText.setText(stringBuilder.toString());
        } else detailText.setText(SpeedLimitsFragment.NO_CONTENT);
    }

    private int roundAndConvertToKmph(double speed) {
        return (int) Math.round(speed * SpeedLimitsFragment.METER_PER_SECOND_TO_KMPH_RATIO);
    }

    private String getNokiaHere(String url) {
        StringBuilder builder = new StringBuilder();
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse response = client.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream content = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(SpeedLimitsFragment.TAG, "Failed to download file");
            }
        } catch (IOException e) {
            Log.e(SpeedLimitsFragment.TAG, e.getMessage(), e);
        }
        return builder.toString();
    }

    private void refreshSpeed(Location lastKnownLocation) {
        speedInKmH = roundAndConvertToKmph(lastKnownLocation.getSpeed());
        speedText.setText(Integer.toString(speedInKmH));
        Log.d(SpeedLimitsFragment.TAG, "speedText=" + speedText);
    }

    private void refreshMusicVolume() {
        int nextVolume = updater.evaluateNextVolume(speedInKmH, limitInKmH);
        audioManager.setStreamVolume(STREAM_MUSIC, nextVolume, 0);

        int currentVolume = audioManager.getStreamVolume(STREAM_MUSIC);
        volumeText.setText(Integer.toString(currentVolume));
        Log.d(SpeedLimitsFragment.TAG, "volume=" + currentVolume);
    }
}
