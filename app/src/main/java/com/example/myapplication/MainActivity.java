package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice hc05Device;
    private ConnectThread connectThread;
    public static OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kiểm tra xem thiết bị có hỗ trợ Bluetooth hay không
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Thiết bị không hỗ trợ Bluetooth
            Toast.makeText(this, "Thiết bị không hỗ trợ Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Đảm bảo Bluetooth đã được bật
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
        }

        // Đăng ký sự kiện click nút để kết nối
        Button connectButton = findViewById(R.id.btnFindBluetooth);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBluetoothConnection();
            }
        });

        Button stopButton = findViewById(R.id.btnSTOP);

        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendDataToRemoteDevice("0");
            }
        });

        Button straightButton = findViewById(R.id.btnSTRAIGHT);
        straightButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendDataToRemoteDevice("5");
            }
        });
        Button rightButton = findViewById(R.id.btnRIGHT);
        rightButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendDataToRemoteDevice("2");
            }
        });
        Button leftButton = findViewById(R.id.btnLEFT);
        leftButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendDataToRemoteDevice("3");
            }
        });
        Button backButton = findViewById(R.id.btnBACK);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendDataToRemoteDevice("4");
            }
        });

        Button autoButton = findViewById(R.id.btnAUTO1);
        autoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendDataToRemoteDevice("1");
            }
        });

    }

    public void speak(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK){
//            String test = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
//            Toast.makeText(this, data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0), Toast.LENGTH_SHORT).show();
            switch (data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0)){
                case "đi thẳng":
                    sendDataToRemoteDevice("5");
                    break;
                case "rẽ phải":
                    sendDataToRemoteDevice("2");
                    break;
                case "rẽ trái":
                    sendDataToRemoteDevice("3");
                    break;
                case "đi lùi":
                    sendDataToRemoteDevice("4");
                    break;
                case "dừng lại":
                    sendDataToRemoteDevice("0");
                    break;
                case "tự động":
                    sendDataToRemoteDevice("1");
                    break;
            }
        }
    }

    private void sendDataToRemoteDevice(String data) {
        if(outputStream != null){
            try{
                outputStream.write(data.getBytes());
            }catch(Exception e){

            }
        }
    }
    private void startBluetoothConnection() {
        // Tìm thiết bị HC-05 đã ghép nối trước đó (paired devices)
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals("HC-05")) { // Đổi tên "HC-05" thành tên của module HC-05 nếu cần thiết
                hc05Device = device;
                break;
            }
        }

        if (hc05Device == null) {
            Toast.makeText(this, "Không tìm thấy thiết bị HC-05 đã ghép nối trước đó", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kết nối đến thiết bị HC-05
        connectThread = new ConnectThread(hc05Device);
        connectThread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Tạo một BluetoothSocket cho kết nối với thiết bị HC-05
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Hủy tìm kiếm các thiết bị Bluetooth nếu đang trong quá trình tìm kiếm (discovery)
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }

            try {
                // Bắt đầu kết nối
                mmSocket.connect();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Hiển thị thông báo khi kết nối thành công
                        Toast.makeText(MainActivity.this, "Kết nối thành công với HC-05", Toast.LENGTH_SHORT).show();
                    }
                });

                // Nếu không có lỗi, bạn đã thành công kết nối đến module HC-05
                // Tiếp tục thực hiện các thao tác với module HC-05 ở đây
                if (mmSocket.isConnected()) {
                    try {
                        // Lấy OutputStream từ BluetoothSocket để gửi dữ liệu
                        MainActivity.outputStream = mmSocket.getOutputStream();

//                        // Chuỗi dữ liệu cần gửi
//                        String dataToSend = "3";
//
//                        // Gửi dữ liệu qua OutputStream
//                        outputStream.write(dataToSend.getBytes());
//
//                        // Bạn có thể thêm các xử lý khác sau khi gửi dữ liệu thành công
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Không thể gửi dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } catch (IOException e) {
                // Không thể kết nối, xử lý lỗi ở đây
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Không thể kết nối đến HC-05", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đảm bảo hủy kết nối khi thoát khỏi ứng dụng
        if (connectThread != null) {
            connectThread.cancel();
        }
    }
}
