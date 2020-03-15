package com.example.btble;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static int REQUEST_ENABLE_BT       = 233;
    private static int REQUEST_DISCOVERABLE_BT = 234;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    public static UUID UUID_SERVICE = UUID.fromString("0000fff8-0000-1000-8000-00805f9b34fb");
    public static UUID UUID_SERVICE1 = UUID.fromString("0000fff9-0000-1000-8000-00805f9b34fb");

    private Button   mSearchBt;
    private Button   mAdvertiseBt;
    private Button   mSearchBle;
    private Button   mAdvertiseBle;
    private TextView mArroudBle;
    private TextView mPairedBt;
    private TextView mFoundBt;
    private Handler  mHandler  = new Handler();
    private boolean  mScanning = false;

    HashMap<String, String> mBleMap      = new HashMap<>();
    HashMap<String, String> mPairedBtMap = new HashMap<>();
    HashMap<String, String> mFounddBtMap = new HashMap<>();

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver foundReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "foundDevices mac: " + deviceHardwareAddress + ", name: " + deviceName);
                if (!mFounddBtMap.containsKey(deviceHardwareAddress)) {
                    mFounddBtMap.put(deviceHardwareAddress, deviceName);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFoundBt.setText(map2String("found bt list", mFounddBtMap));
                        }
                    });
                }
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver discoverableReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1);
                Log.d(TAG, "BluetoothAdapter scan mode: " + mode);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBt = findViewById(R.id.search_bt);
        mAdvertiseBt = findViewById(R.id.advertise_bt);
        mSearchBle = findViewById(R.id.search_ble);
        mAdvertiseBle = findViewById(R.id.advertise_ble);
        mArroudBle = findViewById(R.id.arround_ble);
        mPairedBt = findViewById(R.id.paired_bt);
        mFoundBt = findViewById(R.id.found_bt);

        mSearchBt.setOnClickListener(this);
        mAdvertiseBt.setOnClickListener(this);
        mSearchBle.setOnClickListener(this);
        mAdvertiseBle.setOnClickListener(this);

        PermissionHelper.runOnPermissionGranted(MainActivity.this,
                null,
                new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },
                Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.search_bt:
                Log.d(TAG, "click SEARCH_BT");
                searchBt();
                break;

            case R.id.search_ble:
                Log.d(TAG, "click SEARCH_BLE");
                searchBle();
                break;

            case R.id.advertise_bt:
                Log.d(TAG, "click ADVERTISE_BT");
                advertiseBt();
                break;

            case R.id.advertise_ble:
                Log.d(TAG, "click ADVERTISE_BLE");
                advertiseBle();
                break;
        }
    }

    private void advertiseBle() {
        //初始化广播设置
        AdvertiseSettings mAdvertiseSettings = new AdvertiseSettings.Builder()
                //设置广播模式，以控制广播的功率和延迟。
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                //发射功率级别
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                //不得超过180000毫秒。值为0将禁用时间限制。
                .setTimeout(3000)
                //设置是否可以连接
                .setConnectable(false)
                .build();

        //初始化广播包
        AdvertiseData mAdvertiseData = new AdvertiseData.Builder()
                //设置广播设备名称
                .setIncludeDeviceName(true)
                //设置发射功率级别
                .setIncludeDeviceName(true)
                .addServiceData(new ParcelUuid(UUID_SERVICE1), "456".getBytes())
                .build();

        //初始化扫描响应包
        AdvertiseData mScanResponseData = new AdvertiseData.Builder()
                //隐藏广播设备名称
                .setIncludeDeviceName(false)
                //隐藏发射功率级别
                .setIncludeDeviceName(false)
                //设置广播的服务UUID
                .addServiceUuid(new ParcelUuid(UUID_SERVICE))
                //设置厂商数据
                .addManufacturerData(0x11, "123".getBytes())
                .build();

        //获取蓝牙设配器
        BluetoothManager bluetoothManager = (BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        //设置设备蓝牙名称
        //mBluetoothAdapter.setName("knox-ble");

        //获取BLE广播的操作对象。
        //如果蓝牙关闭或此设备不支持蓝牙LE广播，则返回null。
        final BluetoothLeAdvertiser mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        //mBluetoothLeAdvertiser不为空，且蓝牙已开打
        final MyAdvertiseCallback myAdvertiseCallback = new MyAdvertiseCallback();
        if (mBluetoothAdapter.isEnabled()) {
            if (mBluetoothLeAdvertiser != null) {
                //开启广播
                Log.d(TAG, "start ble advertise");
                mBluetoothLeAdvertiser.startAdvertising(mAdvertiseSettings,
                        mAdvertiseData, mScanResponseData, myAdvertiseCallback);
            } else {
                Log.e(TAG, "don't support ble advertise");
            }
        } else {
            Log.e(TAG, "bluetooth need to be open");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeAdvertiser.stopAdvertising(myAdvertiseCallback);
                Log.d(TAG, "stop ble advertise");

            }
        }).start();
    }

    private class MyAdvertiseCallback extends AdvertiseCallback {
        //开启广播成功回调
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.d(TAG, "ble advertise start success");
        }

        //无法启动广播回调。
        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.e(TAG, "ble advertise start failed，err code: " + errorCode);
        }
    }

    private void scanLeDevice(final BluetoothAdapter bluetoothAdapter, final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    Log.d(TAG, "stopLeScan");
                }
            }, SCAN_PERIOD);

            mBleMap.clear();
            mScanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
            Log.d(TAG, "startLeScan");
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
            Log.d(TAG, "stopLeScan");
        }
    }


    // Device scan callback.
    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mBleMap.containsKey(device.getAddress())) {
                                ParcelUuid[] uuids = device.getUuids();
                                String sUUID = "";
                                if (uuids != null && uuids.length > 0) {
                                    for (ParcelUuid uuid : uuids) {
                                        sUUID = uuid.toString();
                                        Log.d(TAG, "uuid: " + sUUID);
                                    }
                                }
                                Log.d(TAG, "onLeScan, device name: " + device.getName() + ", device mac: " + device.getAddress()
                                        + ", uuid: " + sUUID + ", rssi: " + rssi + ", scan record: " + bytes2hex(scanRecord));
                                mBleMap.put(device.getAddress(), device.getName());
                                String bleMap = map2String("ble list", mBleMap);
                                //Log.d(TAG, "ble map: " + bleMap);
                                //Toast.makeText(MainActivity.this, bleMap,Toast.LENGTH_LONG).show();
                                mArroudBle.setText(bleMap);
                            }
                        }
                    });

                }
            };

    private void searchBle() {
        // 1 设置 BLE
        BluetoothAdapter bluetoothAdapter = openBleInternal();

        // 2 查找 BLE 设备
        searchBleInternal(bluetoothAdapter);
    }

    private void searchBleInternal(BluetoothAdapter bluetoothAdapter) {
        scanLeDevice(bluetoothAdapter, true);
    }

    private BluetoothAdapter openBleInternal() {
        // A.获取 BluetoothAdapter
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        // B.启用蓝牙
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        return bluetoothAdapter;
    }

    private void advertiseBt() {
        // 1 启用可检测性
        IntentFilter discoverableFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(discoverableReceiver, discoverableFilter);
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
        startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT);
    }

    private void searchBt() {
        // 1 设置蓝牙
        BluetoothAdapter bluetoothAdapter = openBluetoothInternal();

        // 2 查找设备
        searchBluetoothInternal(bluetoothAdapter);
    }

    private void searchBluetoothInternal(final BluetoothAdapter bluetoothAdapter) {

        // A.查询已配对设备
        mPairedBtMap.clear();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "pairedDevices mac: " + deviceHardwareAddress + ", name: " + deviceName);
                if (!mPairedBtMap.containsKey(deviceHardwareAddress)) {
                    mPairedBtMap.put(deviceHardwareAddress, deviceName);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPairedBt.setText(map2String("paired bt list", mPairedBtMap));
                }
            });
        }

        // B.发现设备
        // Register for broadcasts when a device is discovered.
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(foundReceiver, foundFilter);
        mFounddBtMap.clear();
        bluetoothAdapter.startDiscovery();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bluetoothAdapter.cancelDiscovery();
            }
        }).start();
    }

    private BluetoothAdapter openBluetoothInternal() {
        // A.获取 BluetoothAdapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.e(TAG, "Device doesn't support Bluetooth");
        }
        Log.d(TAG, "Device supports Bluetooth");

        // B. 启用蓝牙
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        return bluetoothAdapter;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            Log.d(TAG, "open bluetooth: " + ((resultCode == RESULT_OK) ? "OK" : "NG"));
        }

        if (requestCode == REQUEST_DISCOVERABLE_BT) {
            Log.d(TAG, "discoverable bluetooth: " + ((resultCode == RESULT_OK) ? "OK" : "NG"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(foundReceiver);

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(discoverableReceiver);
    }

    private static String bytes2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            sb.append(tmp);
        }
        return sb.toString();
    }

    private String map2String(String header, HashMap<String, String> map) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        sb.append(header + "\n");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + " <=> " + value + "\n");
        }
        return sb.toString();
    }
}
