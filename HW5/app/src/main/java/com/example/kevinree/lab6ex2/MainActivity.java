package com.example.kevinree.lab6ex2;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.ws4d.coap.core.enumerations.CoapMediaType;
import org.ws4d.coap.core.rest.BasicCoapResource;
import org.ws4d.coap.core.rest.CoapResourceServer;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity implements LocationListener {
    //CoAP attributes
    private CoapResourceServer resourceServer;
    private BasicCoapResource bcs_battery;
    private BasicCoapResource bcs_gps;

    private String TAG = "*******";
    private String serviceType = "_coap._udp.";
    private NsdManager.RegistrationListener mRegistrationListener;
    private String mServiceName = "sensor_service";
    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private InetAddress host;

    LocationManager locationManager;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 100 * 1 * 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        moveTaskToBack(true);

        // CoAP resource server remove
        if (this.resourceServer != null) {
            this.resourceServer.stop();
        }
        // CoAP resource server new init
        this.resourceServer = new CoapResourceServer();
        // CoAP add resource
        bcs_battery = new BasicCoapResource("/battery", "82",
                CoapMediaType.text_plain);
        this.resourceServer.createResource(bcs_battery);
        bcs_gps = new BasicCoapResource("/gps", "1111",
                CoapMediaType.text_plain);
        this.resourceServer.createResource(bcs_gps);

        bcs_battery.registerServerListener(this.resourceServer);
        bcs_gps.registerServerListener(this.resourceServer);

        // CoAP Server start
        try {
            Log.v("*******", "Starting server...");
            this.resourceServer.start(5683);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeRegistrationListener();
        registerService(5683);

        String[] requestedPermissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET
        };

        ActivityCompat.requestPermissions(this, requestedPermissions, 1);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

        registerReceiver(mBatInfoReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        Intent intent = new Intent(this, Client.class);
        startActivity(intent);
    }

    //---------------------------------------------------------------------- SENSORS
    @Override
    public void onLocationChanged(Location location) {
        Log.v("******","location Changed" );
        bcs_gps.setValue(""+location.getLatitude()+","+location.getLongitude());
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

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            int level = i.getIntExtra("level", 0);
            Log.e("*******", "B level: " + level);
            bcs_battery.setValue(""+level);
        }
    };


    //---------------------------------------------------------------------- SERVER
    public void registerService(int port) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName("sensor_service");
        serviceInfo.setServiceType("_coap._udp.");
        serviceInfo.setPort(port);
        mNsdManager = (NsdManager)
                this.getSystemService(Context.NSD_SERVICE);
        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }
    private void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onServiceRegistered(NsdServiceInfo
                                                    NsdServiceInfo) {
                mServiceName = NsdServiceInfo.getServiceName();
            }
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo,
                                             int errorCode) {
            }
            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
            }
            @Override
            public void onUnregistrationFailed(NsdServiceInfo
                                                       serviceInfo, int errorCode) {
            }
        };
    }
}