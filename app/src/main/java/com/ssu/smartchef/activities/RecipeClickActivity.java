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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ssu.smartchef.R;
import com.ssu.smartchef.adapters.IngredientAdapter;
import com.ssu.smartchef.adapters.RecipeOrderAdapter;
import com.ssu.smartchef.data.IngredientData;
import com.ssu.smartchef.data.RecipeStepData;
import com.ssu.smartchef.data.SpiceData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class RecipeClickActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_ENABLE_BT = 1001;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private String DEVICE_NAME = "rasberrypi";
//    private String DEVICE_NAME = "jsw-pc";

    private String TAG = "RecipeClickActivity";
    private ConnectedTask mConnectedTask = null;
    private boolean isConnected = false;

    private IngredientAdapter ingredientAdapter;
    private RecipeOrderAdapter recipeOrderAdapter;
    private DatabaseReference mRootRef;
    private DatabaseReference recipeRef;

    private RecyclerView ingredientRecycler;
    private RecyclerView recipeOrderRecycler;

    private TextView recipeName;
    private TextView nickName;
    private TextView recipeExplain;
    private TextView numPerson;
    private int numPerson_t;
    private int numPerson_base;
    private ImageView recipeFood;

    private ArrayList<Double> ingredientWeight_list;
    private ArrayList<SpiceData> spiceList;
    private ArrayList<sendData> sendDataList = new ArrayList<>();
    String send_msg="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_click);

        recipeName = findViewById(R.id.recipeName);
        nickName = findViewById(R.id.nickName);
        recipeExplain = findViewById(R.id.recipeExplain);
        numPerson = findViewById(R.id.numPerson);
        recipeFood = findViewById(R.id.recipeFood);

        ingredientRecycler = findViewById(R.id.ingredientRecycler);
        ingredientRecycler.setNestedScrollingEnabled(false);
        ingredientRecycler.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        ingredientRecycler.setLayoutManager(linearLayoutManager1);
        ingredientAdapter = new IngredientAdapter();
        ingredientRecycler.setAdapter(ingredientAdapter);

        recipeOrderRecycler = findViewById(R.id.recipeOrderRecycler);
        recipeOrderRecycler.setNestedScrollingEnabled(false);
        recipeOrderRecycler.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        recipeOrderRecycler.setLayoutManager(linearLayoutManager2);
        recipeOrderAdapter = new RecipeOrderAdapter();
        recipeOrderRecycler.setAdapter(recipeOrderAdapter);

        ingredientWeight_list = new ArrayList<>();

        Intent intent = getIntent();
        String recipeID = intent.getStringExtra("recipeID");

        mRootRef = FirebaseDatabase.getInstance().getReference();
        recipeRef = mRootRef.child("recipelist").child(recipeID);
        onSearchData();
        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeName.setText(dataSnapshot.child("title").getValue(String.class));
                nickName.setText(dataSnapshot.child("nickname").getValue(String.class));
                recipeExplain.setText(dataSnapshot.child("explain").getValue(String.class));
                numPerson.setText(dataSnapshot.child("numPerson").getValue(Integer.class) + "인분");
                numPerson_t = dataSnapshot.child("numPerson").getValue(Integer.class);
                numPerson_base = numPerson_t;

                Glide.with(getApplicationContext())
                        .load(dataSnapshot.child("image").getValue(String.class))
                        .into(recipeFood);

                for (DataSnapshot stepList : dataSnapshot.child("stepList").getChildren()) {
                    RecipeStepData recipeStepData = new RecipeStepData();
                    recipeStepData.setStepTitle(stepList.child("stepTitle").getValue(String.class));
                    recipeStepData.setStepExplain(stepList.child("stepExplain").getValue(String.class));
                    recipeStepData.setStepImageURL(stepList.child("stepImage").getValue(String.class));

                    for(DataSnapshot step : stepList.child("ingredientList").getChildren()) {
                        IngredientData data = new IngredientData();
                        data.setEditable(false);
                        data.setIngredientName(step.child("ingredient").getValue(String.class));
                        data.setIngredientWeight(step.child("weight").getValue(Double.class));

                        ingredientAdapter.addItem(data);
                        ingredientWeight_list.add(data.getIngredientWeight());
                        recipeStepData.addIngredientArrayList(data);
                    }

                    recipeOrderAdapter.addItem(recipeStepData);
                }

                ViewGroup.LayoutParams layoutParams = ingredientRecycler.getLayoutParams();
                layoutParams.height = layoutParams.height * ingredientAdapter.getItemCount();
                ingredientRecycler.setLayoutParams(layoutParams);
                ingredientAdapter.notifyDataSetChanged();
                recipeOrderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        IntentFilter searchFilter = new IntentFilter();
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //BluetoothAdapter.ACTION_DISCOVERY_STARTED : 블루투스 검색 시작
        searchFilter.addAction(BluetoothDevice.ACTION_FOUND); //BluetoothDevice.ACTION_FOUND : 블루투스 디바이스 찾음
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //BluetoothAdapter.ACTION_DISCOVERY_FINISHED : 블루투스 검색 종료
        searchFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBluetoothSearchReceiver, searchFilter);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        switch (i) {
            case R.id.addButton:
                numPerson.setText(++numPerson_t + "인분");
                changeIngredientWeight(1);
                break;

            case R.id.subButton:
                if (numPerson_t == 1)
                    Toast.makeText(getApplicationContext(), "인원을 감소할 수 없습니다.", Toast.LENGTH_SHORT).show();
                else {
                    numPerson.setText(--numPerson_t + "인분");
                    changeIngredientWeight(-1);
                }
                break;

            case R.id.resetButton:
                numPerson.setText(numPerson_base + "인분");
                numPerson_t = numPerson_base;
                changeIngredientWeight(0);
                break;

            case R.id.extractButton:
                pressExtractButton();
               // Toast.makeText(getApplicationContext(),send_msg,Toast.LENGTH_SHORT).show();
                break;

            case R.id.playButton:
                Intent intent = new Intent(getApplicationContext(), StepExplainActivity.class);
                ArrayList<RecipeStepData > items = recipeOrderAdapter.getListData();
                intent.putExtra("list", items);
                startActivity(intent);
                break;
        }
    }

    public void changeIngredientWeight(int i) {
        int index = 0;

        for (IngredientData data : ingredientAdapter.getListData()) {
            if (i == 0) {
                data.setIngredientWeight(ingredientWeight_list.get(index++));
            }
            else if (numPerson_t >= numPerson_base && numPerson_t % numPerson_base == 0) {
                data.setIngredientWeight(ingredientWeight_list.get(index++) * (numPerson_t / numPerson_base));
            }
            else {
                data.setIngredientWeight(data.getIngredientWeight() + i * data.getIngredientWeight() / (numPerson_t - i));
            }
        }

        ingredientAdapter.notifyDataSetChanged();
        recipeOrderAdapter.notifyDataSetChanged();
    }

    public void pressExtractButton() {
         mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "블루투스를 지원하지 않는 단말기 입니다.", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
        else {
            if (!isConnected)
                showPairedDevices();
            else
                sendMessage();
        }
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
                isConnected = true;
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
        private OutputStream mOutputStream = null;
        private BluetoothSocket mBluetoothSocket = null;

        ConnectedTask(BluetoothSocket socket){

            mBluetoothSocket = socket;
            try {
                mInputStream = mBluetoothSocket.getInputStream();
                mOutputStream = mBluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "socket not created", e );
            }

            Log.d( TAG, "connected to ");
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            sendMessage();

            byte [] readBuffer = new byte[1024];
            int readBufferPosition = 0;


            while (true) {

                if ( isCancelled() ) return false;

                try {

                    int bytesAvailable = mInputStream.available();

                    if(bytesAvailable > 0) {

                        byte[] packetBytes = new byte[bytesAvailable];

                        mInputStream. read(packetBytes);

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

        public void write(String msg){
            msg += "\n";

            try {
                mOutputStream.write(msg.getBytes());
                mOutputStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "Exception during send", e );
            }
        }
    }

    void sendMessage(){
        String send_msg ="";
      
        compare(spiceList,ingredientAdapter.getListData());

        for(int i = 0 ; i < sendDataList.size(); i++){
            if(i == 0){
                send_msg = send_msg + sendDataList.get(i).tomsg(); //재료번호와 무게 비교문자 /
            }
            else{
                send_msg = send_msg +"," + sendDataList.get(i).tomsg(); //각각의 데이터 비교 문자 ,
            }
            if(i == sendDataList.size() - 1){
                send_msg = send_msg + "&"; //끝을 알리는 문자 &
            }
        }
        if ( mConnectedTask != null ) {
            mConnectedTask.write(send_msg);
            Log.d(TAG, "send message: " + send_msg);
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
    public class sendData {
        int spiceNum;
        double spiceWeight;
        sendData(int num,double weight){
            spiceNum = num;
            spiceWeight = weight;
        }
        public int getSpiceNum() {
            return spiceNum;
        }

        public void setSpiceNum(int spiceNum) {
            this.spiceNum = spiceNum;
        }

        public double getSpiceWeight() {
            return spiceWeight;
        }

        public void setSpiceWeight(int spiceWeight) {
            this.spiceWeight = spiceWeight;
        }

        String tomsg(){
            return spiceNum+"/"+spiceWeight;
        }
    }
    protected void onSearchData() {
        Gson gson = new GsonBuilder().create();
        SharedPreferences sp = getSharedPreferences("spice", MODE_PRIVATE);
        String strSpice = sp.getString("spicedata", null);
        if (strSpice != null) {
            Type listType = new TypeToken<ArrayList<SpiceData>>() {}.getType();
            ArrayList<SpiceData> saveData = gson.fromJson(strSpice, listType);
            spiceList = saveData;
        }
    }
    void compare(ArrayList<SpiceData> sd, ArrayList<IngredientData> id){
        String name;
        int spiceId;
        double weight;
        for(int i = 0 ; i< sd.size() ; i++){
            name = sd.get(i).getName();
            spiceId = sd.get(i).getNum();
            for(int j = 0 ; j < id.size() ; j++){
                if(name.equals(id.get(j).getIngredientName()) == true){
                    weight = id.get(j).getIngredientWeight();
                    sendDataList.add(new sendData(spiceId,weight));
                }
            }
        }
    }
}


