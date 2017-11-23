package com.wustrans.bukaiei.inavigator;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rockcode.har.HumanActivity;
import com.wustrans.bukaiei.inavigator.base.LocationCollector;
import com.wustrans.bukaiei.inavigator.base.UserActivityInfo;
import com.wustrans.bukaiei.inavigator.util.LogUtil;
import com.wustrans.bukaiei.inavigator.util.StrUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
        , GoogleMap.OnMarkerClickListener
        , GoogleMap.OnMapClickListener
        , GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;

    private boolean mIsHarRunning = false;

    private static final float DEFAULT_ZOOM = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("debug", "permission granted");
            mMap = googleMap;

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setOnMapClickListener(this);
            mMap.setOnMarkerClickListener(this);

         //   mMap.setInfoWindowAdapter(mInfoWindowAdapter);
            Location location = LocationCollector.getLastLocation(this.getApplicationContext());
            if (location != null) {
                setMapCenter(location, DEFAULT_ZOOM);
            } else {
                setMapCenter(30.75363117, 103.92886356, DEFAULT_ZOOM);
            }
        } else {
            Log.d("debug", "permission error");
            return;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        if(!mIsHarRunning) {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(!gps) {
                showAlertDialogToOpenGPS();
            } else {
                mIsHarRunning = true;
                HarService.startHar(getApplicationContext());
            }
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        HarService.startService(getApplicationContext());
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        if(!mIsHarRunning){
            HarService.stopService(getApplicationContext());
        }

        super.onDestroy();
    }

    private void showAlertDialogToOpenGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle(R.string.alert_dialog_title_open_gps);
        builder.setMessage(R.string.alert_dialog_message_open_gps);
        builder.setPositiveButton(
                getString(R.string.alert_dialog_postive),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MapsActivity.this.startActivity(myIntent);
                    }
                });
        builder.setNegativeButton(getString(R.string.alert_dialog_negative), null);
        builder.create().show();
    }

    public void setMapCenter(Location location, float zoom) {
        setMapCenter(location.getLatitude(), location.getLongitude(), zoom);
    }

    public void setMapCenter(double latitude, double longitude, float zoom) {
        LatLng newLocation = new LatLng(latitude, longitude);
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngZoom(newLocation,zoom);
        mMap.moveCamera(mCameraUpdate);
    }

    public void setMapCenter(double latitude, double longitude) {
        LatLng newLocation = new LatLng(latitude, longitude);
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLng(newLocation);
        mMap.moveCamera(mCameraUpdate);
    }

    public void drawMarkers(List<UserActivityInfo> harList) {
        for (UserActivityInfo ha : harList) {
            drawMarker(ha);
        }
        LogUtil.info("MapFragment - Draw "+ harList.size() + " Dots On MAP");
    }

    public void drawMarker(UserActivityInfo data) {
        LatLng point = new LatLng(data.mLatitude, data.mLongitude);
        // add marker to map
        int resId = getResId(data.mActivity);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(resId);
        MarkerOptions markerOption = new MarkerOptions();
     //   markerOption.setGps(true);
        markerOption.position(point);
        markerOption.icon(bitmap);
        markerOption.title(data.mActivity + "\n" + StrUtil.strTime(data.mGpsTime, "yyyy-MM-dd HH:mm:ss"));
        mMap.addMarker(markerOption);
      //  mMarkerActivityMap.put(markerOption.getTitle(), data);
    }

    public int getResId(String activity) {
        switch(activity) {
            case HumanActivity.ACTIVITY_NOLABEL:
                return R.drawable.dot_grey_24;
            case HumanActivity.ACTIVITY_WALKING:
                return R.drawable.dot_green_24;
            case HumanActivity.ACTIVITY_JOGGING:
                return R.drawable.dot_cyan_24;
            case HumanActivity.ACTIVITY_CYCLING:
                return R.drawable.dot_orange_24;
            case HumanActivity.ACTIVITY_STAIRS:
                return R.drawable.dot_yellow_24;
            case HumanActivity.ACTIVITY_STANDING:
                return R.drawable.dot_blue_24;
            case HumanActivity.ACTIVITY_SITTING:
                return R.drawable.dot_red_24;
            default:
                return R.drawable.dot_grey_24;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleHarUpdate(HarService.HarDataUpdateEvent event) {
        LogUtil.info("MapFragment - handleHarUpdate()");
        UserActivityInfo ha = event.harData;
        drawMarker(ha);
        setMapCenter(ha.mLatitude, ha.mLongitude);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
}
