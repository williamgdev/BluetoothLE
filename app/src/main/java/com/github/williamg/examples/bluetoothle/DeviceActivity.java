package com.github.williamg.examples.bluetoothle;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class DeviceActivity extends AppCompatActivity {
    public static final String EXTRA_DEVICE_KEY = "bluetooh_device_key";
    private BluetoothDevice mBluetoothDevice;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBluetoothDevice = getIntent().getParcelableExtra(EXTRA_DEVICE_KEY);
        textView = (TextView) findViewById(R.id.content_txt);
        textView.setText(mBluetoothDevice.getName());

    }

}
