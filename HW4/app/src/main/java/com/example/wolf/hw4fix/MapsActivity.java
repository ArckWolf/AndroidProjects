package com.example.wolf.hw4fix;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private int MY_LOCATION_REQUEST_CODE = 1;
    LocationManager locationManager;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String[] requestedPermissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET
        };

        ActivityCompat.requestPermissions(this, requestedPermissions, MY_LOCATION_REQUEST_CODE);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();

        // Add a marker in Sydney and move the camera
        LatLng residence = new LatLng(58.3783138, 26.7135149);
        mMap.addMarker(new MarkerOptions().position(residence).title("Hard corded map coordinates: J Liivi 2"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(residence));
        // kaubamaja: 58.3779015,26.7252275

        Location myLocation = getLocation();
        if (myLocation != null) {
            LatLng myPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(myPosition).title("My position"));

            try {
                showDirections(myPosition, residence);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Location getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissions NOT granted!", Toast.LENGTH_SHORT).show();
            return null;
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void showDirections(LatLng start, LatLng end) throws IOException {
        //String str_start = "origin=" + start.latitude + "," + start.longitude;
        //String str_end = "destination=" + end.latitude + "," + end.longitude;
        //str_url = "https://maps.googleapis.com/maps/api/directions/json?" + str_start + "&" + str_end + "&sensor=false&mode=driving&key=AIzaSyCfmPXByA90G7O43ukvbapUOByeII96dsU";

        String str_start = "waypoint0=geo!" + start.latitude + "," + start.longitude;
        String str_end = "waypoint1=geo!" + end.latitude + "," + end.longitude;
        String str_url = "https://route.api.here.com/routing/7.2/calculateroute.json?app_id=Fg874ATte8Aa9hyUCBT9&app_code=HBlVmXJ2jKxLk6Qyan2Y4g&" +str_start +"&" + str_end +"&mode=fastest;car;traffic:disabled";

        PolylineOptions rectOptions = null;
        try {
            rectOptions = new GetRoute().execute(str_url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mMap.addPolyline(rectOptions);
    }
}
