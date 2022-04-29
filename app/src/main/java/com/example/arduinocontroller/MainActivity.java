package com.example.arduinocontroller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

//  ==========================     Arduino     ===================================
public class MainActivity extends AppCompatActivity {

    private String deviceName = null;
    private String deviceAddress = null;

    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;

    private final static int CONNECTING_STATUS = 1;
    private final static int MESSAGE_READ = 2;

    public static Button btnUp;
    public static Button btnLeft;
    public static Button btnRight;
    public static Button btnDown;
    public static Button connectButton;

    public static TextView monitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        monitor = (TextView)findViewById(R.id.textView);
        monitor.setMovementMethod(new ScrollingMovementMethod());

        connectButton = (Button)findViewById(R.id.buttonConnect);

        btnUp = (Button)findViewById(R.id.buttonUp2);
        btnLeft = (Button)findViewById(R.id.buttonLeft);
        btnRight = (Button)findViewById(R.id.buttonRight2);
        btnDown = (Button)findViewById(R.id.buttonDown2);

        deviceName = getIntent().getStringExtra("deviceName");
        if(deviceName != null){
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            Log.e("Connecting to", deviceName + deviceAddress);
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

            if(!btAdapter.isEnabled())
                btAdapter.enable();

            createConnectThread = new CreateConnectThread(btAdapter, deviceAddress);
            createConnectThread.start();
        }

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SelectDeviceActivity.class);
                startActivity(intent);
            }
        });

        btnUp.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent mEvent){
                switch (mEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        connectedThread.write("1");
                        break;
                    case MotionEvent.ACTION_UP:
                        connectedThread.write("/");
                }
                return true;
            }
        });

        btnDown.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent mEvent){
                switch (mEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        connectedThread.write("3");
                        break;
                    case MotionEvent.ACTION_UP:
                        connectedThread.write("-");
                }
                return true;
            }
        });

        btnLeft.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event){
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            connectedThread.write("4");
                            break;
                        case MotionEvent.ACTION_UP:
                            connectedThread.write(",");
                            break;
                }
                return true;
            }
        });

        btnRight.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event){
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            connectedThread.write("2");
                            break;
                        case MotionEvent.ACTION_UP:
                            connectedThread.write(".");
                            break;
                }
                return true;
            }
        });
    }

    public static class CreateConnectThread extends Thread{

        String ADDRESS;
        public CreateConnectThread(BluetoothAdapter btAdapter, String address){
            ADDRESS = address;
            BluetoothDevice btDevice = btAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            try{
                tmp = btDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            }catch (Exception e){
                Log.e("CreateConnect", "Socket create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run(){
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            btAdapter.cancelDiscovery();

            try{
                mmSocket.connect();
                monitor.append("\t\t>Connected to: " + ADDRESS + "\n\t\t>");
                Log.e("Status", "Device Connected");
                //connectButton.setEnabled(false);
            }catch (IOException connectExp){
                try{
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device", connectExp);
                    monitor.append("\t\t>Cannot connect to: " + ADDRESS + " ==== RESTART ====\n>");
                }catch (IOException closeExp){
                    Log.e("CreateConnect", "Could not close socket");
                }
                return;
            }

            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        public void cancel(){
            try{
                mmSocket.close();
            }catch (IOException e){ }
        }
    }

    public static class ConnectedThread extends Thread{

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }catch (IOException e){ }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[2048];
            int bytes = 0;
            while(true){
                try{
                    buffer[bytes] = (byte)mmInStream.read();
                    if(buffer[bytes] == '\n'){
                        String readMsg = new String(buffer, 0, bytes);
                        Log.e("MSG", "Recevied Message" + readMsg);
                        bytes = 0;
                    }
                    else{
                        bytes++;
                    }
                }catch (IOException e){
                    Log.e("Could not", "read");
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(String input){
            byte[] out = input.getBytes();
            try{
                mmOutStream.write(out);
                Log.e("Sent", input);
            }catch (IOException e){
                Log.e("Send Error", "Unable to send message", e);
            }
        }

        public void cancel(){
            try{
                mmSocket.close();
            }catch (IOException e){ }
        }
    }
}