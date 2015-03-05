package glae.speedlimitsmusic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static android.location.LocationManager.GPS_PROVIDER;

public class PlaceholderFragment extends Fragment {

    private static final String TAG = "[speedlimitsmusic]";
    private MainActivity mainActivity;
    private Context applicationContext;
    private View rootView;

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        final TextView speedText = (TextView) rootView.findViewById(R.id.speed);
        final TextView limitText = (TextView) rootView.findViewById(R.id.limit);
        final TextView volumeText = (TextView) rootView.findViewById(R.id.volume);
        final TextView detailText = (TextView) rootView.findViewById(R.id.detailView);


        final LocationManager locationManager = (LocationManager) applicationContext.getSystemService(LOCATION_SERVICE);
        LocationProvider provider = locationManager.getProvider(GPS_PROVIDER);

        if (provider.supportsSpeed()) {
            refreshDataEverySeconds(speedText, volumeText, limitText, detailText, locationManager);
        } else {
            warnAboutGPSspeedFunction();
        }
    }

    private void refreshDataEverySeconds(final TextView speedText, final TextView volumeText, final TextView limitText, TextView detailText, final LocationManager locationManager) {
        Thread t = new Thread() {

            HttpClient client = new DefaultHttpClient();

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        TimeUnit.SECONDS.sleep(1);
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshMusicVolume();

                                Location lastKnownLocation = locationManager.getLastKnownLocation(GPS_PROVIDER);
                                if (lastKnownLocation != null) {
                                    refreshSpeed(lastKnownLocation);
                                    refeshSpeedLimit(lastKnownLocation);
                                } else {
                                    Log.d(TAG, "lastKnowLocation is null");
                                }
                            }


                            private void refeshSpeedLimit(Location lastKnownLocation) {

                                lastKnownLocation.getLatitude();
                                String baseUrl = "http://route.st.nlp.nokia.com/routing/6.2/getlinkinfo.json?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg&waypoint=";
                                baseUrl += lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();

                                String nokiaHereJson = getNokiaHere(baseUrl);

                                try {
                                    JSONObject json = new JSONObject(nokiaHereJson);

                                    JSONObject jsonObject = json.getJSONObject("Response").getJSONObject("Link");
                                    long speedLimit = jsonObject.getLong("SpeedLimit");

                                    double limitInKmH = speedLimit * 3.6;
                                    limitText.setText(Double.valueOf(limitInKmH).intValue() + "");
                                    Log.d(TAG, "limit=" + limitText);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //TODO detail

                            }


                            private String getNokiaHere(String url) {
                                StringBuilder builder = new StringBuilder();
                                HttpGet httpGet = new HttpGet(url);

                                try {
                                    HttpResponse response = client.execute(httpGet);
                                    StatusLine statusLine = response.getStatusLine();
                                    int statusCode = statusLine.getStatusCode();
                                    if (statusCode == 200) {
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
                                double speedInKmH = lastKnownLocation.getSpeed() * 3.6;
                                speedText.setText(Double.valueOf(speedInKmH).intValue() + "");
                                Log.d(TAG, "speedText=" + speedText);

                            }

                            private void refreshMusicVolume() {

                                AudioManager am = (AudioManager) applicationContext.getSystemService(AUDIO_SERVICE);
                                int systemVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                                volumeText.setText(String.valueOf(systemVolume));
                                Log.d(TAG, "volume=" + systemVolume);
                            }


                        });
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, e.getMessage(), e);
                }
            }


        };

        t.start();
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
