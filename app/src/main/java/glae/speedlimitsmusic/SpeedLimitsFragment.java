package glae.speedlimitsmusic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static android.location.LocationManager.GPS_PROVIDER;
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

            Runnable labelsUpdater = new LabelsUpdater(audioManager, locationManager, speedText, limitText, volumeText, detailText);

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        SECONDS.sleep(REFRESH_TIME_IN_SECONDS);
                        mainActivity.runOnUiThread(labelsUpdater);
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, e.getMessage(), e);
                }
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
