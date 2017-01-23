package com.example.nlfox.grabble;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlGeometry;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class MainActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;
    private Map<String, Boolean> collected;
    private List<Marker> markerList;
    private GrabbleApplication app;
    public Marker myLocationMarker;

    LatLng firstLocation;

    public GoogleMap getMap() {
        return mMap;
    }

    /**
     * Demonstrates customizing the info window and/or its contents.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        app = (GrabbleApplication) getApplicationContext();

        // Get initial location from splash screen

        firstLocation = ((Bundle) i.getParcelableExtra("bundle")).getParcelable("position");

        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        FloatingActionButton grabbleBtn = (FloatingActionButton) findViewById(R.id.grabble);

        grabbleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ScrabbleActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton settingButton = (FloatingActionButton) findViewById(R.id.settings);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), InfoActivity.class);
                startActivity(intent);
            }
        });


    }

    // calculate distance between two latlng
    private float distanceBetween(LatLng latLng1, LatLng latLng2) {

        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
        Location loc2 = new Location(LocationManager.GPS_PROVIDER);

        loc1.setLatitude(latLng1.latitude);
        loc1.setLongitude(latLng1.longitude);

        loc2.setLatitude(latLng2.latitude);
        loc2.setLongitude(latLng2.longitude);


        return loc1.distanceTo(loc2);
    }

    // get nearest n markers
    public List<Marker> getNearestNMarkers(int n, LatLng pos) {
        PriorityQueue<Marker> m = new PriorityQueue<Marker>(n, (p, q) -> {
            float dist = distanceBetween(p.getPosition(), pos) - distanceBetween(q.getPosition(), pos);
            if (dist > 0) {
                return 1;
            } else if (dist == 0) {
                return 0;

            } else {
                return -1;
            }

        });
        m.addAll(markerList);
        List<Marker> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            res.add(m.poll());
        }
        return res;

    }

    // animate marker to position. from http://stackoverflow.com/questions/13872803/how-to-animate-marker-in-android-map-api-v2
    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }


    // request for location permission again
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }

    }

    // reseize the character icon
    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "mipmap", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        markerList = new ArrayList<>();


        //First move camera to location get in SplashActivity
        CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(firstLocation, 19);
        myLocationMarker = getMap().addMarker(new MarkerOptions()
                .position(firstLocation)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_person", 300, 300))));
        getMap().moveCamera(myLocation);


        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        // setup location listener
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                LatLng coordinate = new LatLng(latitude, longitude);

                CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 19);
                getMap().animateCamera(myLocation);
                animateMarker(myLocationMarker, coordinate, false);
                for (Marker i : getNearestNMarkers(5, coordinate)) {
                    if (!collected.containsKey(i.getTitle()))
                        i.setVisible(true);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        //check if it's night mode
        if (app.isNightMode()) {
            mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
        }

        // set the limitations to the map fragment
        UiSettings uiSettings = getMap().getUiSettings();
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setScrollGesturesEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMapToolbarEnabled(false);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            requestLocationPermission();

        } else {
            Log.i("Grabble",
                    "loc permission has already been granted.");
        }

        // Define a listener that responds to location updates

        KmlLayer kmlLayer = null;
        String letterMap = app.getWebModel().getLetterMap();
        InputStream letterMapStream = new ByteArrayInputStream(letterMap.getBytes());
        try {
            kmlLayer = new KmlLayer(getMap(), letterMapStream, getApplicationContext());
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        collected = GrabbleApplication.getAppContext(getApplication()).getDataHolder().getCollected();

        if (kmlLayer != null) {
            for (KmlPlacemark placeMark : kmlLayer.getPlacemarks()) {

                KmlGeometry g = placeMark.getGeometry();

                if (g.getGeometryType().equals("Point")) {
                    if (!collected.containsKey(placeMark.getProperty("name"))) {
                        {
                            LatLng point = (LatLng) g.getGeometryObject();
                            Marker marker;
                            if (!app.isHardMode()) {
                                marker = getMap().addMarker(new MarkerOptions()
                                        .position(point)
                                        .title(placeMark.getProperty("name"))
                                        .snippet(placeMark.getProperty("description"))
                                        .icon(BitmapDescriptorFactory.fromResource(
                                                getResources().getIdentifier(
                                                        "marker_" + placeMark.getProperty("description").toLowerCase(),
                                                        "drawable",
                                                        this.getPackageName()
                                                )
                                        )).visible(false)

                                );
                            } else {
                                marker = getMap().addMarker(new MarkerOptions()
                                        .position(point)
                                        .title(placeMark.getProperty("name"))
                                        .snippet(placeMark.getProperty("description"))
                                        .icon(BitmapDescriptorFactory.fromResource(
                                                getResources().getIdentifier(
                                                        "marker_qm",
                                                        "drawable",
                                                        this.getPackageName()
                                                )
                                        )).visible(false)

                                );
                            }
                            marker.setTag(placeMark.getProperty("description").toLowerCase());
                            markerList.add(marker);

                        }

                    } else {
                        Log.v("not collected", placeMark.getProperty("name"));
                    }
                    // Set marker onClick method
                    mMap.setOnMarkerClickListener(this);

                }

            }
        }


        for (Marker i : getNearestNMarkers(5, firstLocation)) {
            if (!collected.containsKey(i.getTitle()))
                i.setVisible(true);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (!PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }


    public boolean onMarkerClick(final Marker marker) {
        if (marker.getTag() == null) {
            return false;
        }
        FragmentManager fm = getFragmentManager();
        InfoDialog dialogFragment = new InfoDialog();
        dialogFragment.setMarker(marker);
        dialogFragment.setView(findViewById(R.id
                .layout));

        dialogFragment.show(fm, "InfoFragment");

        return true;
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}


