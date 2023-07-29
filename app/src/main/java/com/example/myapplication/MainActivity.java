package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice hc05Device;
    private ConnectThread connectThread;

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
                        OutputStream outputStream = mmSocket.getOutputStream();

                        // Chuỗi dữ liệu cần gửi
                        String dataToSend = "3";

                        // Gửi dữ liệu qua OutputStream
                        outputStream.write(dataToSend.getBytes());

                        // Bạn có thể thêm các xử lý khác sau khi gửi dữ liệu thành công
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
