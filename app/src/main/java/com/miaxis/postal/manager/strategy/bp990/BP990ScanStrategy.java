package com.miaxis.postal.manager.strategy.bp990;

import android.content.Context;
import android.util.Log;

import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.miaxis.postal.manager.GpioManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class BP990ScanStrategy {

    private static final String SCAN_SERIAL_PORT = "ttyHSL1";
    private SerialPortManager mSerialPortManager;
    private OnScanStatusListener scanStatusListener;
    private OnScanReadListener scanReadListener;

    /**
     * 初始化，含上电
     *
     * @param context  无用
     * @param listener
     */
    public void initDevice(Context context, OnScanStatusListener listener) {
        this.scanStatusListener = listener;
        initSerialPort();
    }

    public boolean getDeviceStatus() {
        boolean status1 = getScanDeviceStatus1();
        boolean status2 = getScanDeviceStatus2();
        return status1 && status2;
    }

    public void setScanReadListener(OnScanReadListener listener) {
        this.scanReadListener = listener;
    }

    public void scan() {
        startScan();
    }

    public void release() {
        closeSerialPort();
    }

    private void startScan() {
        if (mSerialPortManager != null) {
            byte[] sendBytes = new byte[]{0x02, 0x07, 0x00, 0x53, 0x57, 0x30, 0x30, 0x30, 0x30, 0x30, (byte) 0x9A, 0x01, 0x03};
            boolean sendResult = mSerialPortManager.sendBytes(sendBytes);
            Log.e("asd", "开启扫描   发送结果" + sendResult);
        }
    }

    private boolean getScanDeviceStatus1() {
        String status = "";
        try {
            FileReader fr = new FileReader("/sys/nexgo/power_ctl/scan_power_en");
            BufferedReader localBufferedReader = new BufferedReader(fr, 2);
            status = localBufferedReader.readLine();
            localBufferedReader.close();
            fr.close();
        } catch (IOException e) {
            return status.toUpperCase().contains("ON");
        }
        return status.toUpperCase().contains("ON");
    }

    private boolean getScanDeviceStatus2() {
        String status = "";
        try {
            FileReader fr = new FileReader("/sys/nexgo/power_ctl/scan_trip_en");
            BufferedReader localBufferedReader = new BufferedReader(fr, 2);
            status = localBufferedReader.readLine();
            localBufferedReader.close();
            fr.close();
        } catch (IOException e) {
            return status.toUpperCase().contains("ON");
        }
        return status.toUpperCase().contains("ON");
    }

    private void initSerialPort() {
        boolean isFind = false;
        try {
            if (mSerialPortManager != null) {
                return;
            }
            mSerialPortManager = new SerialPortManager();
            mSerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
                @Override
                public void onSuccess(File file) {
                    Log.d("SerialPortManager", "onSuccess:" + file);
                }

                @Override
                public void onFail(File file, Status status) {
                    Log.d("SerialPortManager", "onFail:" + file+"    status:"+status);
                }
            });
            mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
                @Override
                public void onDataReceived(byte[] bytes) {
                    Log.d("SerialPortManager", "onDataReceived:" + bytes2hex(bytes));

                    String data = "";
                    //                    boolean result = false;
                    if (bytes[0] == 0x02) {
                        data += new String(subBytes(bytes, 3, bytes.length - 6)).replaceAll("\r|\n", "");
                        //                        result = true;
                    } else if (bytes[0] == 0x05) {
                        return;
                    } else {
                        data += new String(bytes).replaceAll("\r|\n", "");
                        //                        result = true;
                    }
                    if (scanReadListener != null) {
                        scanReadListener.onScanRead(true, data);
                    }
                }

                @Override
                public void onDataSent(byte[] bytes) {
                    Log.d("SerialPortManager", "onDataSent:" + bytes2hex(bytes));
                }
            });
            SerialPortFinder serialPortFinder = new SerialPortFinder();
            ArrayList<Device> devices = serialPortFinder.getDevices();
            for (Device device : devices) {
                if (SCAN_SERIAL_PORT.equals(device.getName())) {
                    isFind = true;
                    boolean result = mSerialPortManager.openSerialPort(device.getFile(), 115200);
                    if (scanStatusListener != null) {
                        GpioManager.getInstance().scanDevicePowerControl(true);
                        boolean deviceStatus = getDeviceStatus();
                        scanStatusListener.onScanStatus(result && deviceStatus);
                    }
                    if (result) {
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isFind) {
            scanStatusListener.onScanStatus(false);
        }
    }

    public static String bytes2hex(byte[] hex) {
        StringBuilder sb = new StringBuilder();
        if (hex != null) {
            for (byte b : hex) {
                sb.append(String.format("%02x ", b).toUpperCase());
            }
        }
        return sb.toString();
    }

    public void closeSerialPort() {
        if (mSerialPortManager != null) {
            try {
                mSerialPortManager.closeSerialPort();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mSerialPortManager = null;
            }
        }
    }

    public static byte[] subBytes(byte[] data, int start, int len) {
        byte[] res = new byte[len];
        for (int i = 0; i < len; i++) {
            res[i] = data[start + i];
        }
        return res;
    }

    public interface OnScanStatusListener {
        void onScanStatus(boolean result);
    }

    public interface OnScanReadListener {
        void onScanRead(boolean result, String code);
    }

}
