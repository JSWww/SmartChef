package com.ssu.smartchef.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ssu.smartchef.R;
import com.ssu.smartchef.adapters.IngredientAdapter;
import com.ssu.smartchef.data.IngredientData;
import com.ssu.smartchef.data.RecipeStepData;
import com.ssu.smartchef.data.SpiceData;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class StepExplainActivity extends AppCompatActivity {
    TextView stepNumber,stepTitle,stepExplain;
    RecyclerView stepIngredient;
    ImageView stepImage;
    CircularProgressIndicator stepScale;
    ImageButton pre,next;
    public ArrayList<RecipeStepData> stepList = new ArrayList<>();
    IngredientAdapter adapter = new IngredientAdapter();
    int index = 0;
    int ingredientIndex = 0;
    boolean isScale=false;
    ArrayList<SpiceData> saveData;

    private static final int REQUEST_ENABLE_BT = 1001;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private ConnectedTask mConnectedTask = null;
    private static final String TAG = "bluetoothClient";
    private String DEVICE_NAME = "jsw-pc";
//    private String DEVICE_NAME = "raspberrypi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_explain);

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
            showPairedDevices();
        }


        Intent intent = getIntent();
        stepList = (ArrayList<RecipeStepData>) intent.getSerializableExtra("list");
        stepNumber  = findViewById(R.id.runStepNumber);
        stepTitle = findViewById(R.id.runStepTitle);
        stepExplain = findViewById(R.id.runStepExplain);
        stepIngredient = findViewById(R.id.stepIngredient);
        stepImage = findViewById(R.id.runStepImage);
        stepScale = findViewById(R.id.runStepScale);
        pre = findViewById(R.id.runStepBackButton);
        next = findViewById(R.id.runStepNextButton);
        onSearchData();
        init();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isScale == true){
                    ingredientIndex++;
                }
                else if(isScale == false){
                    isScale = true;
                }
                if(ingredientIndex >= stepList.get(index).getIngredientArrayList().size()){
                    index++;
                    ingredientIndex = 0;
                    isScale = false;
                }
                if(isScale == true &&stepList.get(index).getIngredientArrayList().size() != 0&&isIn(saveData,stepList.get(index).getIngredientArrayList().get(ingredientIndex).getIngredientName()) == true){
                    this.onClick(v);
                }
                else{
                    dataChange(index,isScale,ingredientIndex);
                }
            }
        });
        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isScale == true){
                    ingredientIndex--;
                    if(ingredientIndex < 0){
                        ingredientIndex = 0 ;
                        isScale = false;
                    }
                }
                else if(isScale == false){
                    index--;
                    ingredientIndex = stepList.get(index).getIngredientArrayList().size() - 1;
                    isScale = true;
                }
                if(isScale == true && stepList.get(index).getIngredientArrayList().size() != 0&&isIn(saveData,stepList.get(index).getIngredientArrayList().get(ingredientIndex).getIngredientName()) == true){
                    this.onClick(v);
                }
                dataChange(index,isScale,ingredientIndex);
            }
        });
        dataChange(0,isScale,0);
    }
    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        stepIngredient.setLayoutManager(linearLayoutManager);
        adapter = new IngredientAdapter();
        stepIngredient.setAdapter(adapter);
    }
    private void dataChange(int index,boolean isScale,int ingredientNumber){
        stepNumber.setText("STEP " + (index+1) +"/"+stepList.size());
        if(isScale == true){
            double weight = stepList.get(index).getIngredientArrayList().get(ingredientIndex).getIngredientWeight();
            stepScale.setMaxProgress(weight);
            stepScale.setCurrentProgress(0);
            stepImage.setVisibility(View.INVISIBLE);
            stepScale.setVisibility(View.VISIBLE);
        }
        else{
            stepImage.setVisibility(View.VISIBLE);
            stepScale.setVisibility(View.INVISIBLE);
            Glide.with(getApplicationContext())
                    .load(stepList.get(index).getStepImageURL())
                    .into(stepImage);
        }
        stepTitle.setText(stepList.get(index).getStepTitle());
        stepExplain.setText(stepList.get(index).getStepExplain());
        adapter.listData = stepList.get(index).getIngredientArrayList();
        if(index == 0 && ingredientIndex == 0 && isScale == false){
            pre.setVisibility(View.INVISIBLE);
        }
        else{
            pre.setVisibility(View.VISIBLE);
        }
        if(index == stepList.size() - 1 && (ingredientIndex == stepList.get(index).getIngredientArrayList().size() - 1 || stepList.get(index).getIngredientArrayList().size() ==0)){
            next.setVisibility(View.INVISIBLE);
        }
        else{
            next.setVisibility(View.VISIBLE);
        }
        if(isScale == true){
            adapter.setPos(ingredientNumber);
        }
        else{
            adapter.setPos(-1);
        }
        adapter.notifyDataSetChanged();
    }

    //블루투스 검색결과 BroadcastReceiver
    BroadcastReceiver mBluetoothSearchReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.

            String action = intent.getAction();

            switch (action) {
                //블루투스 디바이스 검색 시작
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Toast.makeText(getApplicationContext(), "블루투스 검색 시작", Toast.LENGTH_SHORT).show();
                    break;
                //블루투스 디바이스 찾음
                case BluetoothDevice.ACTION_FOUND:
                    //검색한 블루투스 디바이스의 객체를 구한다
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getName() != null)
                        if (device.getName().equals(DEVICE_NAME)) {
                            Toast.makeText(getApplicationContext(), "해당 디바이스 찾음", Toast.LENGTH_SHORT).show();
                            mBluetoothDevice = device;
                            mBluetoothAdapter.cancelDiscovery();
                        }
                    break;
                //블루투스 디바이스 검색 종료
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Toast.makeText(getApplicationContext(), "블루투스 검색 종료", Toast.LENGTH_SHORT).show();

                    if (mBluetoothDevice != null) {
                        try {
                            Method method = mBluetoothDevice.getClass().getMethod("createBond", (Class[]) null);
                            method.invoke(mBluetoothDevice, (Object[]) null);

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
                    if (paired.getBondState() == BluetoothDevice.BOND_BONDED) {
                        Log.d("abc", "paired " + paired.getName());
                        ConnectTask task = new ConnectTask(paired);
                        task.execute();
                    }
                    break;
            }
        }
    };

    protected void onSearchData() {
        Gson gson = new GsonBuilder().create();
        SharedPreferences sp = getSharedPreferences("spice", MODE_PRIVATE);
        String strSpice = sp.getString("spicedata", null);
        if (strSpice != null) {
            Type listType = new TypeToken<ArrayList<SpiceData>>() {}.getType();
            saveData = gson.fromJson(strSpice, listType);
        }
    }
    boolean isIn(ArrayList<SpiceData> data,String name){
        for(int i = 0 ; i < data.size() ; i++){
            if(data.get(i).getName().equals(name) == true){
                return true;
            }
        }
        return false;
    }

    public void showPairedDevices()
    {
        Set<BluetoothDevice> pairedDevice = mBluetoothAdapter.getBondedDevices();
        boolean isPaired = false;

        if(pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice) {
                if (device.getName().equals(DEVICE_NAME)) {
                    Toast.makeText(getApplicationContext(), "이미 페어링 되어 있음", Toast.LENGTH_SHORT).show();
                    isPaired = true;
                    ConnectTask task = new ConnectTask(device);
                    task.execute();
                    break;
                }
            }
        }

        if (!isPaired) {
            Toast.makeText(getApplicationContext(), "페어링 안 되어 있음", Toast.LENGTH_SHORT).show();
            mBluetoothAdapter.startDiscovery();
        }
    }

    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {

        private BluetoothSocket mBluetoothSocket = null;
        private BluetoothDevice mBluetoothDevice = null;

        public ConnectTask(BluetoothDevice bluetoothDevice) {
            mBluetoothDevice = bluetoothDevice;

            //SPP
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

            try {
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                Log.d( TAG, "create socket for ");

            } catch (IOException e) {
                Log.e( TAG, "socket create failed " + e.getMessage());
            }

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            // Always cancel discovery because it will slow down a connection
//            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mBluetoothSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mBluetoothSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " +
                            " socket during connection failure", e2);
                }
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isSucess) {

            if ( isSucess ) {
                connected(mBluetoothSocket);
            }
            else{

                Log.d( TAG,  "Unable to connect device");
                Toast.makeText(getApplicationContext(), "Unable to connect device", Toast.LENGTH_SHORT).show();
            }
        }

        public void connected( BluetoothSocket socket ) {
            mConnectedTask = new ConnectedTask(socket);
            mConnectedTask.execute();
        }
    }

    private class ConnectedTask extends AsyncTask<Void, String, Boolean> {

        private InputStream mInputStream = null;
        private BluetoothSocket mBluetoothSocket = null;

        ConnectedTask(BluetoothSocket socket){

            mBluetoothSocket = socket;
            try {
                mInputStream = mBluetoothSocket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "socket not created", e );
            }

            Log.d( TAG, "connected to ");
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            byte [] readBuffer = new byte[1024];
            int readBufferPosition = 0;


            while (true) {

                if ( isCancelled() ) return false;

                try {

                    int bytesAvailable = mInputStream.available();

                    if(bytesAvailable > 0) {

                        byte[] packetBytes = new byte[bytesAvailable];

                        mInputStream.read(packetBytes);

                        for(int i=0;i<bytesAvailable;i++) {

                            byte b = packetBytes[i];
                            if(b == '\n')
                            {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0,
                                        encodedBytes.length);
                                String recvMessage = new String(encodedBytes, "UTF-8");

                                readBufferPosition = 0;

                                Log.d(TAG, "recv message: " + recvMessage);
                                publishProgress(recvMessage);
                            }
                            else
                            {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                } catch (IOException e) {

                    Log.e(TAG, "disconnected", e);
                    return false;
                }
            }

        }

        @Override
        protected void onProgressUpdate(String... recvMessage) {
            double weight = Double.parseDouble(recvMessage[0]);
            stepScale.setCurrentProgress(weight);
        }

        @Override
        protected void onPostExecute(Boolean isSucess) {
            super.onPostExecute(isSucess);

            if ( !isSucess ) {
                closeSocket();
                Log.d(TAG, "Device connection was lost");
                Toast.makeText(getApplicationContext(),"Device connection was lost",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);

            closeSocket();
        }

        void closeSocket(){

            try {
                mBluetoothSocket.close();
                Log.d(TAG, "close socket()");

            } catch (IOException e2) {

                Log.e(TAG, "unable to close() " +
                        " socket during connection failure", e2);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                showPairedDevices();
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "failed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mBluetoothSearchReceiver);

        if (mConnectedTask != null) {
            mConnectedTask.cancel(true);
        }
    }
}
