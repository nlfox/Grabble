package com.example.nlfox.grabble;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SplashActivity extends AppCompatActivity {

    int count = 0;
    RoundCornerProgressBar splashProgbar;
    LatLng coordinate;
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            coordinate = new LatLng(latitude, longitude);
            // First time move the progressBar when first location is get.
            moveProgressBar(false);
            // Then call the next step
            isFirstLogin();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
    TextView splashText;

    // control the progressBar
    protected void moveProgressBar(boolean finish) {
        splashProgbar.setProgress(splashProgbar.getProgress() + 30.0f);
        count += 1;
        if (count == 3 && finish) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            Bundle args = new Bundle();
            args.putParcelable("position",coordinate);
            intent.putExtra("bundle",args);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashText = (TextView) findViewById(R.id.splash_text);
        splashProgbar = (RoundCornerProgressBar) findViewById(R.id.splash_progbar);
        splashProgbar.setMax(90.0f);
        splashProgbar.setProgress(0.0f);
        splashText.setText("Loading Location");

        // ****  Step One  ****


        // First check if we have the location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // If we don't have the permission, request it
            requestLocationPermission();

        } else {

            // Init the location Manager (We are granted to have location permission here)
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Request update
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        }


    }

    // request the permission
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

    // callback of permission request
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);

            // Second check the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        } else {
            finish();
            // Permission denied, exit now.
        }

    }


    // ****  Step Two  ****
    // check login

    protected void isFirstLogin() {
        // if first start, go to login activity
        if (GrabbleApplication.getAppContext(getApplication()).getWebModel().has_token()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {

            // if already have token, just start to get info from the server

            InitTask a = new InitTask();
            a.execute();
            splashText.setText("Checking login token...");
            moveProgressBar(false);

        }
    }


    // just make back button not working in Splash screen
    @Override
    public void onBackPressed() {
    }


    // ****  Last Step  ****
    // Init the game data from server


    private class InitTask extends AsyncTask<Object, Void, Boolean> {
        protected Boolean doInBackground(Object... params) {
            try {
                // call the singleton progress and to initialize using the data from the server side
                GrabbleApplication.getAppContext(getApplication()).initialize();
            } catch (Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // show a alert dialog for network error
                        new AlertDialog.Builder(SplashActivity.this)
                                .setTitle("Network Error")
                                .setMessage("Network Error")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        new InitTask().execute();
                                    }
                                })
                                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        finish();
                                    }
                                })
                                .show();
                    }
                });
                return false;
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
                moveProgressBar(true);
                splashText.setText("Loading Map...");

        }

    }
}