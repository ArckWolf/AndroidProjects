package com.example.kevinree.lab6ex2;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.ws4d.coap.core.CoapClient;
import org.ws4d.coap.core.CoapConstants;
import org.ws4d.coap.core.connection.BasicCoapChannelManager;
import org.ws4d.coap.core.connection.api.CoapChannelManager;
import org.ws4d.coap.core.connection.api.CoapClientChannel;
import org.ws4d.coap.core.enumerations.CoapRequestCode;
import org.ws4d.coap.core.messages.api.CoapRequest;
import org.ws4d.coap.core.messages.api.CoapResponse;
import org.ws4d.coap.core.rest.BasicCoapResource;
import org.ws4d.coap.core.rest.CoapResourceServer;
import org.ws4d.coap.core.tools.Encoder;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client extends AppCompatActivity  implements CoapClient {
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
    private boolean first = true;
    TextView txtBattery;
    TextView txtGPS;
    public static Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        txtGPS = findViewById(R.id.txtGPS);
        txtBattery = findViewById(R.id.txtBattery);
    }

    @Override
    protected void onResume() {
        Log.e("Contacts","onResume: ");
        getState();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.e("Contacts","onDestroy: ");
        saveState();
        super.onDestroy();
    }

    public void saveState() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.MY_PREFERENCE_FILE), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        txtGPS = findViewById(R.id.txtGPS);
        txtBattery = findViewById(R.id.txtBattery);

        String bt = txtBattery.getText().toString();
        if (!bt.isEmpty() || !bt.equals(""))
            editor.putString("txtBattery", bt);

        String gps = txtGPS.getText().toString();
        if (!gps.isEmpty() || !gps.equals(""))
            editor.putString("txtGPS", gps);

        editor.commit();
    }

    public void getState() {
        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.MY_PREFERENCE_FILE),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        txtGPS = findViewById(R.id.txtGPS);
        txtBattery = findViewById(R.id.txtBattery);
        if (prefs.contains("txtBattery")){
            String bat = prefs.getString("txtBattery","");
            txtBattery.setText(bat);
        }
        if (prefs.contains("txtGPS")){
            String gps = prefs.getString("txtGPS","");
            txtGPS.setText(gps);
        }

        editor.clear();
        editor.apply();
    }

    //--------------------------------------------------------------- GET DATA

    public void startGetData(String serverAddress, int serverPort) {
        System.out.println("===START=== (Run Test Client)");
        String sAddress = serverAddress;
        int sPort = serverPort;
        CoapChannelManager channelManager =
                BasicCoapChannelManager.getInstance();
        CoapClientChannel clientChannel = null;
        try {
            clientChannel = (CoapClientChannel) channelManager.connect(this, InetAddress.getByName(sAddress), sPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        CoapRequest request;
        if (null == clientChannel) {
            return;
        }
        request = clientChannel.createRequest(CoapRequestCode.GET,
                "/battery", true);
        clientChannel.sendMessage(request);

        request = clientChannel.createRequest(CoapRequestCode.GET,
                "/gps", true);
        clientChannel.sendMessage(request);
    }
    @Override
    public void onResponse(CoapClientChannel channel, CoapResponse
            response) {
        if (response.getPayload() != null) {
            Log.v("*******",
                    "Response: " + response.toString() + " payload:" + Encoder.ByteToString(response.getPayload()));

            final String payload = Encoder.ByteToString(response.getPayload());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(first){
                        txtBattery.setText(payload);
                        first = false;
                    }else{
                        first = true;
                        txtGPS.setText(payload);
                    }
                }
            });
        } else {
            System.out.println("Response: " + response.toString());
        }
    }

    @Override
    public void onMCResponse(CoapClientChannel channel, CoapResponse
            response, InetAddress srcAddress, int srcPort) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onConnectionFailed(CoapClientChannel channel,
                                   boolean notReachable, boolean resetByServer) {
        Log.v("*******","Connection Failed");
        System.exit(-1);
    }

    public void onClickRefresh(View view) {
        //startGetData("localhost",  CoapConstants.COAP_DEFAULT_PORT);
       /* Client.handler.post(new Runnable() {
            @Override
            public void run() {
                // Do Whatever
                startGetData("localhost",  CoapConstants.COAP_DEFAULT_PORT);
            }
        });*/

        Thread t = new Thread(new Runnable() {
            public void run() {
                startDiscover();
            }
        });
        t.start();


       /* Thread thread = new Thread(tmp);
        thread.start();*/
       /* Intent intent = new Intent(this, TestClient.class);
        startActivity(intent);*/
    }

    //-------------------------------------------------------- DISCOVER
    public void startDiscover(){
        Log.d(TAG, "Discovering services...");
        mNsdManager = (NsdManager) this.getSystemService(Context.NSD_SERVICE);
        initializeDiscoveryListener();
        mNsdManager.discoverServices(serviceType,
                NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void initializeDiscoveryListener() {
        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }
            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! Do something with it.
                initializeResolveListener();
                Log.d(TAG, "Service discovery success" + service);
                mNsdManager.resolveService(service, mResolveListener);
            }
            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "service lost: " + service);
            }
            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }
            @Override
            public void onStartDiscoveryFailed(String serviceType, int
                    errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
            @Override
            public void onStopDiscoveryFailed(String serviceType, int
                    errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }
    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int
                    errorCode) {

                Log.e(TAG, "Resolve failed: " + errorCode);
            }
            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);
                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same IP.");
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            startGetData("localhost",  CoapConstants.COAP_DEFAULT_PORT);
                        }
                    });
                    t.start();
                    return;
                }
                NsdServiceInfo mService = serviceInfo;
                int port = mService.getPort();
                InetAddress host = mService.getHost();
            }
        };
    }
}


