package com.github.williamg.examples.bluetoothle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener{
    private static final String TAG = "MainActivity";
    private static final long SCAN_PERIOD = 1 * 1000;
    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayList<BluetoothDevice> mBluetoothDevices;
    private BluetoothLeScanner mBluetoothScanner;
    private Handler mHandler;
    private ListView listView;
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        checkIfBLisSupported();
        initializeBLAdapter();
        checkIfBLEnabled();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch (state){
                        case BluetoothAdapter.STATE_ON:
                            Toast.makeText(context, "BT ON", Toast.LENGTH_SHORT).show();
                            break;

                        case BluetoothAdapter.STATE_OFF:
                            Toast.makeText(context, "BT OFF", Toast.LENGTH_SHORT).show();
                            break;

                        case BluetoothAdapter.STATE_TURNING_ON:
                            Toast.makeText(context, "BT TURNING ON", Toast.LENGTH_SHORT).show();
                            break;

                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Toast.makeText(context, "BT TURNING OFF", Toast.LENGTH_SHORT).show();
                            break;

                        case BluetoothAdapter.STATE_CONNECTING:
                            Toast.makeText(context, "BT CONNECTING", Toast.LENGTH_SHORT).show();
                            break;

                        case BluetoothAdapter.ERROR:
                            Toast.makeText(context, "BT OFF", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };
        intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void scanLeDevice(final boolean enable) {

        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothScanner.stopScan(mLeScanCallback);
                    Toast.makeText(getApplicationContext(), "Finish Scan", Toast.LENGTH_SHORT).show();
                }
            }, SCAN_PERIOD);
            mBluetoothScanner.startScan(mLeScanCallback);
            Toast.makeText(getApplicationContext(), "Scanning Devices...", Toast.LENGTH_SHORT).show();
        } else {
            mBluetoothScanner.startScan(mLeScanCallback);
            Toast.makeText(getApplicationContext(), "Finish Scan", Toast.LENGTH_SHORT).show();
        }
    }

    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice bluetoothDevice = result.getDevice();
            String name = bluetoothDevice.getAddress() + " " + bluetoothDevice.getName();
            if (!mBluetoothDevices.contains(bluetoothDevice)){
                mBluetoothDevices.add(bluetoothDevice);
                mArrayAdapter.add(name);
                mArrayAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d(TAG, "onBatchScanResults: " + results.toString());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(TAG, "onScanFailed: " + errorCode);
        }
    };

    private void checkIfBLEnabled() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void initializeBLAdapter() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    private void checkIfBLisSupported() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.bluetooth_nosupported, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT){
            if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Please active the Bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    public void OnScan(View view) {
        mHandler = new Handler();
        mBluetoothDevices = new ArrayList();
        listView = (ListView) findViewById(R.id.main_listview);
        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(mArrayAdapter);
        listView.setOnItemClickListener(this);
        scanLeDevice(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DeviceActivity.class);
        intent.putExtra(DeviceActivity.EXTRA_DEVICE_KEY, mBluetoothDevices.get(position));
        startActivity(intent);
    }
}
