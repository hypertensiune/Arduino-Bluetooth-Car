package com.example.arduinocontroller;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

import java.util.List;

public class DeviceAdapterActivity extends RecyclerView.Adapter<DeviceAdapterActivity.ViewHolder> {

    private List<DeviceInfo> devicesList;
    private Context mcontext;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textName, textAddress;
        LinearLayout linearLayout;

        public ViewHolder(View view){
            super(view);
            textName = (TextView)view.findViewById(R.id.deviceName);
            textAddress = (TextView)view.findViewById(R.id.deviceAddress);
            linearLayout = view.findViewById(R.id.linearLayoutDevices);
        }
    }

    public DeviceAdapterActivity(Context context, List<DeviceInfo> DEVICES_LIST){
        devicesList = DEVICES_LIST;
        mcontext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_element, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int pos){
        String name = devicesList.get(pos).deviceName;
        String address = devicesList.get(pos).deviceAddress;
        viewHolder.textName.setText("Name: " + name);
        viewHolder.textAddress.setText("MAC Address: " + address);

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent intent = new Intent(mcontext, MainActivity.class);
                intent.putExtra("deviceName", name);
                intent.putExtra("deviceAddress", address);
                Log.e("Chosed", name + address);
                mcontext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount(){
        return devicesList.size();
    }
}
