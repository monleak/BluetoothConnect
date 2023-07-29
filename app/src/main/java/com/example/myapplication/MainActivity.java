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

//public class MainActivity extends AppCompatActivity  {
//    private BluetoothAdapter mBluetoothAdapter = null;
//    private static final int REQUEST_ENABLE_BT = 3;
//    private static final int REQUEST_PERMISSIONS = 1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(receiver, filter);
//
//        Button btnFindBluetooth = (Button) findViewById(R.id.btnFindBluetooth);
//        btnFindBluetooth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                    if(mBluetoothAdapter!=null)
//                    {
//                        if(mBluetoothAdapter.isDiscovering())
//                        {
//                            mBluetoothAdapter.cancelDiscovery();
//                        }
//                        mBluetoothAdapter.startDiscovery();
//                    }
//
//            }
//        });
//    }
//    private final BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                // Discovery has found a device. Get the BluetoothDevice
//                // object and its info from the Intent.
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//                Log.d(TAG,deviceName+" "+deviceHardwareAddress);
//                // if DESKTOP-SGIE2S0 40:74:E0:90:08:43
//                if(deviceName!=null && deviceName.equals("DESKTOP-SGIE2S0"))
//                {
//                   // mBluetoothAdapter.cancelDiscovery();
//                    Thread thread = new ConnectThread(device);
//                    thread.start();
//                }
//
//            }
//        }
//    };
//    @Override
//    public void onStart() {
//        super.onStart();
//        checkPermissions();
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//            // Otherwise, setup the chat session
//        } else{
//
//        }
//    }
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void checkPermissions(){
//       requestPermissions(new String[]{"android.permission.BLUETOOTH_CONNECT",
//                                       "android.permission.BLUETOOTH_SCAN",
//                                       "android.permission.BLUETOOTH_ADMIN",
//                                       "android.permission.BLUETOOTH",
//                                       "android.permission.ACCESS_FINE_LOCATION"//Cho startDiscovery Bluetooth Devices
//       },REQUEST_PERMISSIONS);
//
//    }
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case REQUEST_ENABLE_BT:
//                break;
//            case REQUEST_PERMISSIONS:
//                break;
//        }
//    }
//    private class ConnectThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final BluetoothDevice mmDevice;
//        private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//
//
//        public ConnectThread(BluetoothDevice device) {
//            // Use a temporary object that is later assigned to mmSocket
//            // because mmSocket is final.
//            BluetoothSocket tmp = null;
//            mmDevice = device;
//
//            try {
//                // Get a BluetoothSocket to connect with the given BluetoothDevice.
//                // MY_UUID is the app's UUID string, also used in the server code.
//
//
//              //  tmp = device.createRfcommSocketToServiceRecord(myUUID);
//
//                final String PBAP_UUID = "0000112f-0000-1000-8000-00805f9b34fb";
//                tmp=device.createInsecureRfcommSocketToServiceRecord(ParcelUuid.fromString(PBAP_UUID).getUuid());
//
//            } catch (IOException e) {
//                Log.e(TAG, "Socket's create() method failed", e);
//            }
//            mmSocket = tmp;
//        }
//
//        public void run() {
//            // Cancel discovery because it otherwise slows down the connection.
//            mBluetoothAdapter.cancelDiscovery();
//
//            try {
//                // Connect to the remote device through the socket. This call blocks
//                // until it succeeds or throws an exception.
//                mmSocket.connect();
//            } catch (IOException connectException) {
//                // Unable to connect; close the socket and return.
//                try {
//                    mmSocket.close();
//                } catch (IOException closeException) {
//                    Log.e(TAG, "Could not close the client socket", closeException);
//                }
//                return;
//            }
//
//            // The connection attempt succeeded. Perform work associated with
//            // the connection in a separate thread.
//         //   manageMyConnectedSocket(mmSocket);
//            Thread thread = new ConnectedThread(mmSocket);
//            thread.start();
//        }
//
//        // Closes the client socket and causes the thread to finish.
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) {
//                Log.e(TAG, "Could not close the client socket", e);
//            }
//        }
//    }
//    private class ConnectedThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final InputStream mmInStream;
//        private final OutputStream mmOutStream;
//        private byte[] mmBuffer; // mmBuffer store for the stream
//
//        public ConnectedThread(BluetoothSocket socket) {
//            mmSocket = socket;
//            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
//
//            // Get the input and output streams; using temp objects because
//            // member streams are final.
//            try {
//                tmpIn = socket.getInputStream();
//            } catch (IOException e) {
//                Log.e(TAG, "Error occurred when creating input stream", e);
//            }
//            try {
//                tmpOut = socket.getOutputStream();
//            } catch (IOException e) {
//                Log.e(TAG, "Error occurred when creating output stream", e);
//            }
//
//            mmInStream = tmpIn;
//            mmOutStream = tmpOut;
//        }
//
//        public void run() {
//            mmBuffer = new byte[1024];
//            int numBytes; // bytes returned from read()
//
//            // Keep listening to the InputStream until an exception occurs.
//            while (true) {
//                try {
//                    // Read from the InputStream.
//                    numBytes = mmInStream.read(mmBuffer);
//                    String str = new String(mmBuffer,0,numBytes);
//                    Log.d(TAG,str);
//
//                }
//                catch (Exception e)
//                {
//                    Log.d(TAG, "Input stream was disconnected", e);
//                    break;
//                }
//            }
//        }
//
//        // Call this from the main activity to send data to the remote device.
//        public void write(byte[] bytes) {
//            try {
//                mmOutStream.write(bytes);
//            } catch (IOException e) {
//                Log.e(TAG, "Error occurred when sending data", e);
//            }
//        }
//
//        // Call this method from the main activity to shut down the connection.
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) {
//                Log.e(TAG, "Could not close the connect socket", e);
//            }
//        }
//    }
//}

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
