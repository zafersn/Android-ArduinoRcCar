/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stackcuriosity.com.vmecontrolrccar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothDemo extends Activity implements OnKeyListener,
        OnTouchListener,SensorEventListener {



    //GazPedalıı---------------
    private class UpdateCounterTask implements Runnable {
        private boolean mInc;

        public UpdateCounterTask(boolean inc) {
            mInc = inc;
        }

        public void run() {
            if (mInc) {
                mHandlerGaz.sendEmptyMessage(MSG_INC);
            } else {
                mHandlerGaz.sendEmptyMessage(MSG_DEC);
            }
        }
    }
    private static final int MSG_INC = 0;
    private static final int MSG_DEC = 1;
    private Handler mHandlerGaz;
    private ScheduledExecutorService mUpdater;
    private Button mIncButton;
    private Button mDecButton;

    //İvme sensörleri ile yönverme
    private SensorManager sensorManager;
    float x,y,z;


    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Layout Views
    private TextView mTitle,ivme_sol,ivme_sag,textHiz;
    private ListView mConversationView;
    //Uygulamada geçirilen vakit:
    private TextView textTimer;
    private Button startButton;
    private Button pauseButton;
    private long startTime = 0L;
    private Handler myHandler = new Handler();
    long timeInMillies = 0L;
    long timeSwap = 0L;
    long finalTime = 0L;


    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    //private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothDemoService mChatService = null;


    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if(D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        //GazPedalı OnCreate
//        getActionBar().hide();

        try {
            mHandlerGaz = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_INC:
                            decGeriGitme();
                            Log.i("zms","zms case MSG_INC: ");
                            return;
                        case MSG_DEC:
                            incGazVerme();
                            Log.i("zms","zms case MSG_DEC: ");
                            return;
                    }
                    super.handleMessage(msg);
                }
            };



            mIncButton = (Button) findViewById(R.id.button5);
            mDecButton = (Button)findViewById(R.id.button6);
            mIncButton.setOnTouchListener(this);
            mIncButton.setOnKeyListener(this);
            //  mIncButton.setOnClickListener(this);
            mDecButton.setOnTouchListener(this);
            mDecButton.setOnKeyListener(this);
            //  mDecButton.setOnClickListener(this);
//İvme sensörü veri
        }catch (Exception e){
            e.printStackTrace();
        }
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        // add listener. The listener will be HelloAndroid (this) class
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        ivme_sag = (TextView) findViewById(R.id.sag_ivme);
        ivme_sol = (TextView) findViewById(R.id.sol_ivme);
        textHiz = (TextView) findViewById(R.id.textView);

        // Set up the custom title

        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }
    private void incGazVerme() {
        if (mChatService.getState() != BluetoothDemoService.STATE_CONNECTED) {
            //   Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        String message="803";
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);


        }
        Log.i("zms","zms message ++++++++++++++++++++++++++++");

    }

    private void decGeriGitme() {
        if (mChatService.getState() != BluetoothDemoService.STATE_CONNECTED) {
            //   Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        String message="804";
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

        }
        Log.i("zms","zms message-----------------------");

    }
    private void startUpdating(boolean inc) {
        if (mUpdater != null) {
            Log.e(getClass().getSimpleName(), "Another executor is still active");
            return;
        }
        mUpdater = Executors.newSingleThreadScheduledExecutor();
        mUpdater.scheduleAtFixedRate(new UpdateCounterTask(inc), 10, 200,
                TimeUnit.MILLISECONDS);

    }

    private void stopUpdating() {
        mUpdater.shutdownNow();
        mUpdater = null;
        if (mChatService.getState() != BluetoothDemoService.STATE_CONNECTED) {
            // Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        String message="805";
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);


        }
        Log.i("zms","zms message STOOOOOOOOOOOOOOOOOOOOPPPPPPPPPPPPP");
    }

 /*  public void onClick(View v) {
        if (mUpdater == null) {
            if (v == mIncButton) {
                incGazVerme();
            } else if(v==mDecButton){
                decGeriGitme();
                Log.i("zms","zms sonMu 2");

            }
        }
    }*/

    private Runnable updateTimerMethod = new Runnable() {

        public void run() {
            timeInMillies = SystemClock.uptimeMillis() - startTime;
            finalTime = timeSwap + timeInMillies;

            int seconds = (int) (finalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (finalTime % 1000);
            textHiz.setText("" + minutes + ":"
                    + String.format("%02d", seconds) + ":"
                    + String.format("%03d", milliseconds));
            myHandler.postDelayed(this, 0);
        }

    };
    public void onAccuracyChanged(Sensor sensor,int accuracy){

    }

    public void onSensorChanged(SensorEvent event){

        // check sensor type
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

            // assign directions
            x= event.values[0];
            y=event.values[1];
            z=event.values[2];
            if(Math.round(y)<0) {
                ivme_sol.setText(""+Math.round(y)*(-1));
            }
            else if(Math.round(y)>0){
                ivme_sag.setText(""+Math.round(y));
            }
            else{
                ivme_sol.setText("0");
                ivme_sag.setText("0");
            }
            switch (Math.round(y)){
                case -9:
                    sendMessage("909");

                    break;
                case -8:
                    sendMessage("908");

                    break;
                case -7:
                    sendMessage("907");

                    break;
                case -6:
                    sendMessage("906");

                    break;
                case -5:
                    sendMessage("905");

                    break;
                case -4:
                    sendMessage("904");

                    break;
                case -3:
                    sendMessage("903");

                    break;
                case -2:
                    sendMessage("902");

                    break;
                case -1:
                    sendMessage("901");

                    break;
                case 0:


                    break;
                case 1:
                    sendMessage("701");

                    break;
                case 2:
                    sendMessage("702");

                    break;
                case 3:
                    sendMessage("703");

                    break;
                case 4:
                    sendMessage("704");

                    break;
                case 5:
                    sendMessage("705");
                    break;
                case 6:
                    sendMessage("706");

                    break;
                case 7:
                    sendMessage("707");

                    break;
                case 8:
                    sendMessage("708");

                    break;
                case 9:
                    sendMessage("709");

                    break;
                default:
                    break;

            }


        }
    }


    public boolean onKey(View v, int keyCode, KeyEvent event) {
        boolean isKeyOfInterest = keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER;
        boolean isReleased = event.getAction() == KeyEvent.ACTION_UP;
        boolean isPressed = event.getAction() == KeyEvent.ACTION_DOWN
                && event.getAction() != KeyEvent.ACTION_MULTIPLE;
        try {


            if (isKeyOfInterest && isReleased) {
                stopUpdating();
                Log.i("zms", "zms onKey  isKeyOfInterest && isReleased ");
            } else if (isKeyOfInterest && isPressed) {
                startUpdating(v == mIncButton);
                Log.i("zms", "zms onKey elseİF  isKeyOfInterest && isReleased ");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean onTouch(View v, MotionEvent event) {
        boolean isReleased = event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL;
        boolean isPressed = event.getAction() == MotionEvent.ACTION_DOWN;
        try {


            if (isReleased) {
                stopUpdating();
                Log.i("zms","zms toch isReleased ");


            } else if (isPressed) {
                Log.i("zms","zms toch isPressed startUpdating  ");
                startUpdating(v == mIncButton);
                Log.i("zms","zms sonMu ");

            }
        }catch(Exception e){
            e.printStackTrace();

        }
        return false;

    }


    @SuppressLint("NewApi")
    @Override
    public void onStart() {
        super.onStart();
        //if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        try {

            if (mChatService != null) {
                // Only if the state is STATE_NONE, do we know that we haven't started already
                if (mChatService.getState() == BluetoothDemoService.STATE_NONE) {
                    // Start the Bluetooth chat services
                    mChatService.start();
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private void setupChat() {
        try {


            //Log.d(TAG, "setupChat()");

            // Initialize the array adapter for the conversation thread
            mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
            mConversationView = (ListView) findViewById(R.id.in);
            mConversationView.setAdapter(mConversationArrayAdapter);


            // Initialize the BluetoothChatService to perform bluetooth connections
            mChatService = new BluetoothDemoService(this, mHandler);

            // Initialize the buffer for outgoing messages
            //mOutStringBuffer = new StringBuffer("");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        //if(D) Log.e(TAG, "- ON PAUSE -");

    }

    @Override
    public void onStop() {
        super.onStop();
        //if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        //if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    @SuppressLint("NewApi")
    private void ensureDiscoverable() {
        //if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private void sendData(byte[] send){
        if (mChatService.getState() != BluetoothDemoService.STATE_CONNECTED) {
            // Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }


        if(send.length>0)
            mChatService.write(send);
    }


    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothDemoService.STATE_CONNECTED) {
            //  Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);


        }
    }



    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    //if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothDemoService.STATE_CONNECTED:
                            mTitle.setText(R.string.title_connected_to);
                            startTime = SystemClock.uptimeMillis();
                            myHandler.postDelayed(updateTimerMethod, 0);
                            mTitle.append(mConnectedDeviceName);
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothDemoService.STATE_CONNECTING:
                            mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothDemoService.STATE_LISTEN:
                        case BluetoothDemoService.STATE_NONE:
                            mTitle.setText(R.string.title_not_connected);
                            timeSwap += timeInMillies;
                            myHandler.removeCallbacks(updateTimerMethod);
                            break;
                    }
                    break;

                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
 /*burada patlıyo */              Toast.makeText(getApplicationContext(), "Connected to "
                        + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    //Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
    }

}