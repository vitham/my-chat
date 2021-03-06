package vit.vn.mychat;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HotspotActivity extends AppCompatActivity {

    @BindView(R.id.hotspot_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.hotspot_input_username)
    TextInputLayout mInputUser;

    @BindView(R.id.hotspot_button_create)
    ImageView mCreateBtn;

    @BindView(R.id.hotspot_button_join)
    ImageView mJoinBtn;

    @BindView(R.id.hotspot_button_chat)
    Button mChatBtn;

    private String networkSSID = "My Chat";
    private String networkPass = "pass";
    private boolean hotspot = false;
    private boolean wifi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot);
        ButterKnife.bind(this);

        setToolbar();
    }

    @OnClick(R.id.hotspot_button_create)
    void onClickCreate(View view) {
        CreateWifiAccessPoint createOne = new CreateWifiAccessPoint();
        createOne.execute((Void) null);
    }

    @OnClick(R.id.hotspot_button_join)
    void onClickJoin(View view) {
        JoinWifiNetwork joinOne = new JoinWifiNetwork();
        joinOne.execute((Void) null);
    }

    @OnClick(R.id.hotspot_button_create)
    void onClickChat(View view) {
        String userName = mInputUser.getEditText().getText().toString();
        if (userName.equals("")) {
            Toast.makeText(getApplicationContext(), "Make Sure to Fill UserName Text & ChatName Text", Toast.LENGTH_SHORT).show();
        } else if (hotspot == false && wifi == false) {
            Toast.makeText(getApplicationContext(), "Make Sure to Connect to the WiFi network", Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent(HotspotActivity.this, HotspotChatActivity.class);
            i.putExtra("name", userName);
            startActivity(i);
        }
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Setup Connect");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //create a wifi hotspot from app
    private class CreateWifiAccessPoint extends AsyncTask<Void, Void, Boolean> {
        {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            WifiManager wifiManager = (WifiManager) getBaseContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
            Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();
            boolean methodFound = false;

            for (Method method : wmMethods) {
                if (method.getName().equals("setWifiApEnabled")) {
                    methodFound = true;
                    WifiConfiguration netConfig = new WifiConfiguration();
                    netConfig.SSID = networkSSID;
                    netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                    netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    try {
                        final boolean apStatus = (Boolean) method.invoke(wifiManager, netConfig, true);
                        for (Method isWifiApEnabledMethod : wmMethods)
                            if (isWifiApEnabledMethod.getName().equals("isWifiApEnabled")) {
                                while (!(Boolean) isWifiApEnabledMethod.invoke(wifiManager)) {
                                }
                                for (Method method1 : wmMethods) {
                                    if (method1.getName().equals("getWifiApState")) {
                                    }
                                }
                            }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (apStatus) {
                                    System.out.println("SUCCESS ");
                                    hotspot = true;
                                    Toast.makeText(getApplicationContext(), "Wifi Hotspot Created", Toast.LENGTH_SHORT).show();

                                } else {
                                    System.out.println("FAILED");
                                    hotspot = false;
                                    Toast.makeText(getApplicationContext(), "Wifi Hotspot Creation Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            return methodFound;

        }
    }

    //Enable wifi and join to server
    private class JoinWifiNetwork extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

            WifiManager wifiManager = (WifiManager) getBaseContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.addNetwork(conf);
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
                wifiManager.startScan();
            }

            int netId = wifiManager.addNetwork(conf);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Joined to " + networkSSID, Toast.LENGTH_SHORT).show();
                    System.out.println("SUCCESS ");
                    wifi = true;
                }
            });
            return null;
        }
    }

}
