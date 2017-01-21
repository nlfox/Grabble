package com.example.nlfox.grabble;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

public class SplashActivity extends AppCompatActivity {

    int count = 0;
    RoundCornerProgressBar progress1;

    protected void moveProgressBar(boolean finish) {
        progress1.setProgress(progress1.getProgress() + 30.0f);
        count += 1;
        if (count == 3 && finish) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progress1 = (RoundCornerProgressBar) findViewById(R.id.progress_1);
        progress1.setMax(90.0f);
        progress1.setProgress(0.0f);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            requestLocationPermission();

        } else {
            Log.i("Grabble",
                    "1st move.");
            Log.i("Grabble",
                    "loc permission has already been granted. Displaying camera preview.");
            moveProgressBar(false);
            isFirstLogin();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // permission was granted, yay! Do the
            // contacts-related task you need to do.
            isFirstLogin();

        } else {
            finish();
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
        }
        return;


        // other 'case' lines to check for other
        // permissions this app might request
    }


    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }

    }

    protected void isFirstLogin() {
        boolean isFirstOpen = GrabbleApplication.getAppContext(getApplication()).getWebModel().has_token();
        // if first start, go to login activity
        if (GrabbleApplication.getAppContext(getApplication()).getWebModel().has_token()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            //return;
        } else {
            InitTask a = new InitTask();
            a.execute();
            moveProgressBar(false);
            //finish();
        }
    }


    @Override
    public void onBackPressed() {
    }

    private class InitTask extends AsyncTask<Object, Void, Boolean> {
        protected Boolean doInBackground(Object... params) {
            try {

                GrabbleApplication.getAppContext(getApplication()).initialize();
            } catch (Exception e) {
                return false;
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            moveProgressBar(true);
        }

    }
}