package glae.speedlimitsmusic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
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

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.media.AudioManager.STREAM_MUSIC;
import static android.os.StrictMode.ThreadPolicy.Builder;
import static java.util.concurrent.TimeUnit.SECONDS;

public class SpeedLimitsFragment extends Fragment {

    public static final String NOKIA_HERE_BASE_URL_API = "http://route.st.nlp.nokia.com/routing/6.2/getlinkinfo.json?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg&waypoint=";
    public static final String TAG = "[speedlimitsmusic]";
    public static final String NO_CONTENT = "-";
    public static final double METER_PER_SECOND_TO_KMPH_RATIO = 3.6;
    public static final int REFRESH_TIME_IN_SECONDS = 1;

    private MainActivity mainActivity;
    private Context applicationContext;
    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        StrictMode.setThreadPolicy(new Builder().permitAll().build());
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        final TextView speedText = (TextView) rootView.findViewById(R.id.speed);
        final TextView limitText = (TextView) rootView.findViewById(R.id.limit);
        final TextView volumeText = (TextView) rootView.findViewById(R.id.volume);
        final TextView detailText = (TextView) rootView.findViewById(R.id.detailView);

        final AudioManager audioManager = (AudioManager) applicationContext.getSystemService(AUDIO_SERVICE);
        final LocationManager locationManager = (LocationManager) applicationContext.getSystemService(LOCATION_SERVICE);

        if (locationManager.getProvider(GPS_PROVIDER).supportsSpeed()) {
            refreshDataEverySecond(speedText, volumeText, limitText, detailText, locationManager, audioManager);
        } else {
            warnAboutGPSspeedFunction();
        }
    }

    private void refreshDataEverySecond(final TextView speedText, final TextView volumeText, final TextView limitText, final TextView detailText, final LocationManager locationManager, final AudioManager audioManager) {
        Thread thread = new Thread() {

            HttpClient client = new DefaultHttpClient();
            SpeedLimitsLocationListener locationListener = new SpeedLimitsLocationListener();
            VolumeUpdater updater = new VolumeUpdater();
            Runnable updateLabels = createUpdateLabelsAction();

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        SECONDS.sleep(REFRESH_TIME_IN_SECONDS);

                        mainActivity.runOnUiThread(updateLabels);
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, e.getMessage(), e);
                }
            }

            private Runnable createUpdateLabelsAction() {
                return new Runnable() {

                    private int limitInKmH = 0;
                    private int speedInKmH = 0;

                    @Override
                    public void run() {

                        locationManager.requestLocationUpdates(GPS_PROVIDER, SECONDS.toMillis(REFRESH_TIME_IN_SECONDS), 0, locationListener);
                        Location lastKnownLocation = locationManager.getLastKnownLocation(GPS_PROVIDER);

                        if (lastKnownLocation != null) {
                            refreshSpeed(lastKnownLocation);
                            refeshSpeedLimit(lastKnownLocation);
                            refreshMusicVolume();
                        } else {
                            speedText.setText(NO_CONTENT);
                            limitText.setText(NO_CONTENT);
                            volumeText.setText(NO_CONTENT);
                            Log.d(TAG, "lastKnowLocation is null");
                        }
                    }

                    private void refeshSpeedLimit(Location lastKnownLocation) {

                        lastKnownLocation.getLatitude();
                        String baseUrl = NOKIA_HERE_BASE_URL_API;
                        baseUrl += lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();

                        String nokiaHereJson = getNokiaHere(baseUrl);

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
                                        Log.d(TAG, "limit=" + limitText);

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

                            if (label != null) {
                                stringBuilder.append(label);
                            }
                            String city = address.getString("City");
                            if (city != null) {
                                stringBuilder.append(", ").append(city);
                            }

                            String county = address.getString("County");
                            if (county != null) {
                                stringBuilder.append(" (").append(county);
                            }
                            String state = address.getString("State");
                            if (state != null) {
                                stringBuilder.append(", ").append(state);
                            }

                            String country = address.getString("Country");
                            if (country != null) {
                                stringBuilder.append(", ").append(country).append(")");
                            }
                            if(!stringBuilder.toString().isEmpty()){
                                detailText.setText(stringBuilder.toString());
                            }
                        } else {
                            detailText.setText(NO_CONTENT);
                        }
                    }

                    private int roundAndConvertToKmph(double speed) {
                        return (int) Math.round(speed * METER_PER_SECOND_TO_KMPH_RATIO);
                    }

                    private String getNokiaHere(String url) {
                        StringBuilder builder = new StringBuilder();
                        HttpGet httpGet = new HttpGet(url);

                        try {
                            HttpResponse response = client.execute(httpGet);
                            StatusLine statusLine = response.getStatusLine();
                            int statusCode = statusLine.getStatusCode();
                            if (statusCode == HttpStatus.SC_OK) {
                                HttpEntity entity = response.getEntity();
                                InputStream content = entity.getContent();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    builder.append(line);
                                }
                            } else {
                                Log.e(TAG, "Failed to download file");
                            }
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                        return builder.toString();
                    }

                    private void refreshSpeed(Location lastKnownLocation) {
                        speedInKmH = roundAndConvertToKmph(lastKnownLocation.getSpeed());
                        speedText.setText(Integer.toString(speedInKmH));
                        Log.d(TAG, "speedText=" + speedText);
                    }

                    private void refreshMusicVolume() {
                        int nextVolume = updater.evaluateNextVolume(speedInKmH, limitInKmH);
                        audioManager.setStreamVolume(STREAM_MUSIC, nextVolume, 0);

                        int currentVolume = audioManager.getStreamVolume(STREAM_MUSIC);
                        volumeText.setText(Integer.toString(currentVolume));
                        Log.d(TAG, "volume=" + currentVolume);
                    }
                };
            }
        };
        thread.start();
    }

    private void warnAboutGPSspeedFunction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);
        builder.setMessage("Your GPS does not support #getSpeed() function. The application cannot work")
                .setCancelable(false)
                .setNeutralButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mainActivity.finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Log.d(TAG, "no support speed GPS available");
    }

    public void setContext(Context context, MainActivity mainActivity) {
        this.applicationContext = context;
        this.mainActivity = mainActivity;
    }
}
