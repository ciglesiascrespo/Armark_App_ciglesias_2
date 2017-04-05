package com.feedhenry.armark;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.services.camera.CameraLifecycleListener;
import com.wikitude.common.camera.CameraSettings;

import java.io.IOException;

import android.Manifest;
import android.util.Log;
import android.widget.Toast;

import static com.feedhenry.armark.R.id.architectView;
import static com.google.ads.AdRequest.LOGTAG;

public class ArAlmacenActivity extends AppCompatActivity implements ArchitectViewHolderInterface, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private ArchitectView av;
    protected ArchitectView.SensorAccuracyChangeListener sensorAccuracyListener;
    protected ArchitectViewHolderInterface.ILocationProvider locationProvider;

    private static final int PETICION_PERMISO_LOCALIZACION = 101;

    private GoogleApiClient apiClient;

    protected ArchitectView.ArchitectWorldLoadedListener worldLoadedListener;

    protected boolean hasGeo;
    protected boolean hasIR;
    protected boolean hasInstant;

    private double Latitud;
    private double Longitud;
    private double Altura;
    private float Exactitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_almacen);

        av = (ArchitectView) this.findViewById(R.id.architectView);

        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setLicenseKey(Constantes.KEY_APP);

        config.setFeatures(this.getFeatures());
        config.setCameraResolution(this.getCameraResolution());
        config.setCamera2Enabled(this.getCamera2Enabled());

        this.av.setCameraLifecycleListener(getCameraLifecycleListener());
        try {
            /* first mandatory life-cycle notification */
            this.av.onCreate(config);
        } catch (RuntimeException rex) {
            this.av = null;
            Toast.makeText(getApplicationContext(), "can't create Architect View", Toast.LENGTH_SHORT).show();
            Log.e(this.getClass().getName(), "Exception in ArchitectView.onCreate()", rex);
        }

        // register valid world loaded listener in av, ensure this is set before content is loaded to not miss any event
        if (this.worldLoadedListener != null && this.av != null) {
            this.av.registerWorldLoadedListener(worldLoadedListener);
        }

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    private int getFeatures() {
        int features = (hasGeo ? ArchitectStartupConfiguration.Features.Geo : 0) |
                (hasIR ? ArchitectStartupConfiguration.Features.ImageTracking : 0) |
                (hasInstant ? ArchitectStartupConfiguration.Features.InstantTracking : 0);
        return features;
    }

    protected CameraSettings.CameraResolution getCameraResolution() {
        return CameraSettings.CameraResolution.SD_640x480;
    }

    protected boolean getCamera2Enabled() {
        return false;
    }

    protected CameraLifecycleListener getCameraLifecycleListener() {
        return null;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (this.av != null) {
            this.av.onPostCreate();
            try {
                Log.e("ArAlmacenActivity", "PostCreate");
                this.av.load("file:///android_asset/Radar/index.html");

                if (this.getInitialCullingDistanceMeters() != ArchitectViewHolderInterface.CULLING_DISTANCE_DEFAULT_METERS) {

                    this.av.setCullingDistance(this.getInitialCullingDistanceMeters());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.av != null) {
            this.av.onResume();

            // register accuracy listener in architectView, if set
            if (this.sensorAccuracyListener != null) {
                this.av.registerSensorAccuracyChangeListener(this.sensorAccuracyListener);
            }
        }

        // tell locationProvider to resume, usually location is then (again) fetched, so the GPS indicator appears in status bar
        if (this.av != null) {
            this.av.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // call mandatory live-cycle method of architectView
        if (this.av != null) {
            this.av.clearCache();
            this.av.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (this.av != null) {
            this.av.onLowMemory();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        // call mandatory live-cycle method of architectView
        if (this.av != null) {
            this.av.onPause();

            // unregister accuracy listener in architectView, if set
            if (this.sensorAccuracyListener != null) {
                this.av.unregisterSensorAccuracyChangeListener(this.sensorAccuracyListener);
            }
        }

        // tell locationProvider to pause, usually location is then no longer fetched, so the GPS indicator disappears in status bar
        if (this.locationProvider != null) {
            this.locationProvider.onPause();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }
    }

    private void updateUI(Location loc) {
        if (loc != null) {
            Latitud = loc.getLatitude();
            Longitud = loc.getLongitude();
            Exactitud = loc.getAccuracy();
            Altura = loc.getAltitude();

            try {
                av.setLocation(Latitud, Longitud, Altura, Exactitud);
            } catch (Exception e) {
                Log.e(LOGTAG, e.getMessage());
            }

        } else {
            Latitud = 0;
            Longitud = 0;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public String getARchitectWorldPath() {
        return null;
    }

    @Override
    public ArchitectView.ArchitectUrlListener getUrlListener() {
        return null;
    }

    @Override
    public int getContentViewId() {
        return 0;
    }

    @Override
    public String getWikitudeSDKLicenseKey() {
        return null;
    }

    @Override
    public int getArchitectViewId() {
        return 0;
    }

    @Override
    public ILocationProvider getLocationProvider(LocationListener locationListener) {
        return null;
    }

    @Override
    public ArchitectView.SensorAccuracyChangeListener getSensorAccuracyListener() {
        return null;
    }

    @Override
    public float getInitialCullingDistanceMeters() {
        return 0;
    }

    @Override
    public ArchitectView.ArchitectWorldLoadedListener getWorldLoadedListener() {
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Permiso concedido

                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);

                updateUI(lastLocation);

            } else {
                //Permiso denegado:
                Log.e(LOGTAG, "Permiso denegado");
            }
        }
    }


}
