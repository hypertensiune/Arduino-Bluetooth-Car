package com.example.arduinocontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SelectDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.paired_devices_list);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        List<DeviceInfo> deviceList = new ArrayList<>();

        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
                DeviceInfo deviceInfo = new DeviceInfo(device.getName(), device.getAddress());
                deviceList.add(deviceInfo);
            }

            DeviceAdapterActivity deviceAdapter = new DeviceAdapterActivity(this, deviceList);
            RecyclerView recyclerView = findViewById(R.id.recyclerviewDevices);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(deviceAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        else{
            View view = findViewById(R.id.recyclerviewDevices);
            Snackbar snackbar = Snackbar.make(view, "Activate Bluetooth or pair a bluetooth device", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) { }
            });
            snackbar.show();
        }
    }

}
