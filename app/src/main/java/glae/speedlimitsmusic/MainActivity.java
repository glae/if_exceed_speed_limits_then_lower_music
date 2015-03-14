package glae.speedlimitsmusic;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            SpeedLimitsFragment speedLimitsFragment = new SpeedLimitsFragment();
            speedLimitsFragment.setContext(getApplicationContext(), this);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, speedLimitsFragment)
                    .commit();
        }
    }



}
