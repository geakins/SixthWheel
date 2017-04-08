package com.sixthwheel.sixthwheelv2;

import java.net.ServerSocket;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class MainActivity extends Activity {

    private final String TAG = "MainActivity";

    //About the ui controls
    EditText editTextDestinationIPAddress = null;
    EditText editTextDestinationPort = null;
    Button buttonConnect = null;
    TextView textViewReceivedData = null;
    EditText editTextOutgoingData;
    Button buttonSend = null;
    TextView textViewMyIP = null;
    //private boolean isConnected = false;

    //About the socket
    Handler handler;
    ClientThread clientThread;
    boolean isClientThreadRunning = false;

    //Receiver server stuff
    TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewMyIP = (TextView) findViewById(R.id.textViewMyIP);
        msg = (TextView) findViewById(R.id.textViewReceived);

        textViewMyIP.setText(getIpAddress());

        editTextDestinationIPAddress = (EditText) this.findViewById(R.id.editTextDestinationIP);
        editTextDestinationPort = (EditText) this.findViewById(R.id.editTextDestinationPort);
        textViewReceivedData = (TextView) this.findViewById(R.id.textViewReceived);
        editTextOutgoingData = (EditText) this.findViewById(R.id.editTextOutgoingData);
        buttonConnect = (Button) this.findViewById(R.id.buttonConnect);
        buttonSend = (Button) this.findViewById(R.id.buttonSendDataToServer);

        init();

        //Click here to connect
        buttonConnect.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {

                String ip = editTextDestinationIPAddress.getText().toString();
                String port = editTextDestinationPort.getText().toString();

                Log.d(TAG, ip + port);

                if (buttonConnect.getText().toString().equals("Connect")) {
                    if (!isClientThreadRunning) {
                        clientThread = new ClientThread(handler, ip, port);
                        new Thread(clientThread).start();
                        isClientThreadRunning = true;

                    }
                    Log.d(TAG, "clientThread is start!!");
                    fastToast("Connected to bridge!");

                    if (clientThread.isConnected) {
                        buttonConnect.setText("Disconnect");
                    }
                }
                else if (buttonConnect.getText().toString().equals("Disconnect")){
                    //clientThread.interrupt();
                    isClientThreadRunning = true;
                    fastToast("Disconnected!");
                    buttonConnect.setText("Connect");
                }
            }});

        //Click here to Send Msg to Server
        buttonSend.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                try
                {
                    Message msg = new Message();
                    msg.what = 0x852;
                    msg.obj = editTextOutgoingData.getText().toString();
                    clientThread.sendHandler.sendMessage(msg);
                    editTextOutgoingData.setText("");
                }
                catch (Exception e)
                {
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }});

    }

    private void init()
    {
        //Load the datas from share preferences
        SharedPreferences sharedata = getSharedPreferences("data", 0);
        String ip = sharedata.getString("ip", "192.168.4.1");
        String port = sharedata.getString("port", "23");
        editTextDestinationIPAddress.setText(ip);
        editTextDestinationPort.setText(port);

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == 0x123)
                {
                    textViewReceivedData.setText(msg.obj.toString());
                }
            }
        };
    }

    public boolean onDestory(){
        return true;
    }

    public void fastToast(String toaster) {
        Toast.makeText(getApplicationContext(), toaster, Toast.LENGTH_SHORT).show();
    }
































    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Local IP: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
}




