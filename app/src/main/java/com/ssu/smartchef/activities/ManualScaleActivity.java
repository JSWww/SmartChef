package com.ssu.smartchef.activities;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.ssu.smartchef.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ManualScaleActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1001;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mdevice;
    private BluetoothSocket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private byte[] readBuffer;
    private Thread mWorkerThread;
    private int readBufferPositon;
    private byte mDelimiter = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_scale);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "블루투스를 지원하지 않는 단말기 입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //블루투스 브로드캐스트 리시버 등록
        //리시버1
        IntentFilter stateFilter = new IntentFilter();
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
        registerReceiver(mBluetoothStateReceiver, stateFilter);
        //리시버2
        IntentFilter searchFilter = new IntentFilter();
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //BluetoothAdapter.ACTION_DISCOVERY_STARTED : 블루투스 검색 시작
        searchFilter.addAction(BluetoothDevice.ACTION_FOUND); //BluetoothDevice.ACTION_FOUND : 블루투스 디바이스 찾음
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //BluetoothAdapter.ACTION_DISCOVERY_FINISHED : 블루투스 검색 종료
        searchFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBluetoothSearchReceiver, searchFilter);


        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
        else {
            searchDevice();
        }
    }

    //블루투스 상태변화 BroadcastReceiver
    BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //BluetoothAdapter.EXTRA_STATE : 블루투스의 현재상태 변화
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

            //블루투스 활성화
            if(state == BluetoothAdapter.STATE_ON){
                Log.d("abc","블루투스 활성화");
            }
            //블루투스 활성화 중
            else if(state == BluetoothAdapter.STATE_TURNING_ON){
                Log.d("abc","블루투스 활성화 중...");
            }
            //블루투스 비활성화
            else if(state == BluetoothAdapter.STATE_OFF){
                Log.d("abc","블루투스 비활성화");
            }
            //블루투스 비활성화 중
            else if(state == BluetoothAdapter.STATE_TURNING_OFF){
                Log.d("abc","블루투스 비활성화 중...");
            }
        }
    };

    //블루투스 검색결과 BroadcastReceiver
    BroadcastReceiver mBluetoothSearchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch(action){
                //블루투스 디바이스 검색 시작
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Toast.makeText(getApplicationContext(), "블루투스 검색 시작", Toast.LENGTH_SHORT).show();
                    break;
                //블루투스 디바이스 찾음
                case BluetoothDevice.ACTION_FOUND:
                    //검색한 블루투스 디바이스의 객체를 구한다
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getName() != null)
                     if (device.getName().equals("jsw-pc")) {
                          Toast.makeText(getApplicationContext(), "해당 디바이스 찾음", Toast.LENGTH_SHORT).show();
                          mdevice = device;
                          mBluetoothAdapter.cancelDiscovery();
                      }
                    break;
                //블루투스 디바이스 검색 종료
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Toast.makeText(getApplicationContext(), "블루투스 검색 종료", Toast.LENGTH_SHORT).show();

                    if (mdevice != null) {
                        try {
                            Method method = mdevice.getClass().getMethod("createBond", (Class[]) null);
                            method.invoke(mdevice, (Object[]) null);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                //블루투스 디바이스 페어링 상태 변화
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    BluetoothDevice paired = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(paired.getBondState()==BluetoothDevice.BOND_BONDED){
                        Log.d("abc", "paired " + paired.getName());
                    }
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                searchDevice();
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "failed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void searchDevice(){
        Set<BluetoothDevice> pairedDevice = mBluetoothAdapter.getBondedDevices();

        boolean isPaired = false;

        if(pairedDevice.size() > 0){
            for(BluetoothDevice device : pairedDevice){
                if (device.getName().equals("jsw-pc")) {
                    Toast.makeText(getApplicationContext(), "이미 페어링 되어 있음", Toast.LENGTH_SHORT).show();
                    isPaired = true;
                    connectToSelectedDevice(mdevice);
                    break;
                }
            }
        }

        if (!isPaired) {
            Toast.makeText(getApplicationContext(), "페어링 안 되어 있음", Toast.LENGTH_SHORT).show();
            mBluetoothAdapter.startDiscovery();
        }
    }

    private void connectToSelectedDevice(final BluetoothDevice device) {
        //블루투스 기기에 연결하는 과정이 시간이 걸리기 때문에 그냥 함수로 수행을 하면 GUI에 영향을 미친다
        //따라서 연결 과정을 thread로 수행하고 thread의 수행 결과를 받아 다음 과정으로 넘어간다.

        //handler는 thread에서 던지는 메세지를 보고 다음 동작을 수행시킨다.
        final Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) // 연결 완료
                {
                    try {
                        Toast.makeText(getApplicationContext(),"연결 성공", Toast.LENGTH_SHORT).show();
                        //연결이 완료되면 소켓에서 outstream과 inputstream을 얻는다. 블루투스를 통해
                        //데이터를 주고 받는 통로가 된다.
                        mOutputStream = mSocket.getOutputStream();
                        mInputStream = mSocket.getInputStream();
                        // 데이터 수신 준비
                        beginListenForData();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {    //연결 실패
                    Toast.makeText(getApplicationContext(),"Please check the device", Toast.LENGTH_SHORT).show();

                }
            }
        };

        //연결과정을 수행할 thread 생성
        Thread thread = new Thread(new Runnable() {
            public void run() {
                //선택된 기기의 이름을 갖는 bluetooth device의 object
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

                try {
                    // 소켓 생성
                    mSocket = device.createRfcommSocketToServiceRecord(uuid);
                    // RFCOMM 채널을 통한 연결, socket에 connect하는데 시간이 걸린다. 따라서 ui에 영향을 주지 않기 위해서는
                    // Thread로 연결 과정을 수행해야 한다.
                    mSocket.connect();
                    mHandler.sendEmptyMessage(1);
                } catch (Exception e) {
                    // 블루투스 연결 중 오류 발생
                    mHandler.sendEmptyMessage(-1);
                }
            }
        });

        //연결 thread를 수행한다
        thread.start();
    }


    //블루투스 데이터 수신 Listener
    protected void beginListenForData() {
        final Handler handler = new Handler();
        readBuffer = new byte[1024];  //  수신 버퍼
        readBufferPositon = 0;        //   버퍼 내 수신 문자 저장 위치

        // 문자열 수신 쓰레드
        mWorkerThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!Thread.currentThread().isInterrupted()) {

                    try {

                        int bytesAvailable = mInputStream.available();
                        if (bytesAvailable > 0) { //데이터가 수신된 경우
                            byte[] packetBytes = new byte[bytesAvailable];
                            mInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == mDelimiter) {
                                    byte[] encodedBytes = new byte[readBufferPositon];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPositon = 0;
                                    handler.post(new Runnable() {
                                        public void run() {
                                            //수신된 데이터는 data 변수에 string으로 저장!! 이 데이터를 이용하면 된다.

                                            char[] c_arr = data.toCharArray(); // char 배열로 변환
                                            if (c_arr[0] == 'a') {
                                                if (c_arr[1] == '1') {

                                                    //a1이라는 데이터가 수신되었을 때

                                                }
                                                if (c_arr[1] == '2') {

                                                    //a2라는 데이터가 수신 되었을 때
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPositon++] = b;
                                }
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //데이터 수신 thread 시작
        mWorkerThread.start();
    }

    public void SendResetSignal(){
        String msg = "bs00000";
        try {
            mOutputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBluetoothStateReceiver);
        unregisterReceiver(mBluetoothSearchReceiver);
        super.onDestroy();
    }
}
