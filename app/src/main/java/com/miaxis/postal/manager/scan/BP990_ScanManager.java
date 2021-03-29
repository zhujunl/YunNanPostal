package com.miaxis.postal.manager.scan;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kongqw.serialportlibrary.SerialPort;
import com.kongqw.serialportlibrary.thread.SerialPortReadThread;
import com.miaxis.postal.manager.GpioManager;
import com.mx.finger.utils.HexStringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class BP990_ScanManager implements IScanManager {
    private static final String TAG = "Mx-ScanManager-990";
    private static final String SCAN_SERIAL_PORT = "/dev/ttyHSL1";
    private OnScanListener listener;

    private BP990_ScanManager() {
    }

    public static BP990_ScanManager getInstance() {
        return SingletonHolder.instance;
    }


    private static class SingletonHolder {
        private static final BP990_ScanManager instance = new BP990_ScanManager();
    }


    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> stopScanForPowerOn;

    @Override
    public void powerOn() {
        // 忽略
        Log.d(TAG, "powerOn() called");
        GpioManager.getInstance().scanDevicePowerControl(true);
        stopScanForPowerOn = executorService.schedule(this::stopScan, 3000, TimeUnit.MILLISECONDS);

    }

    @Override
    public void powerOff() {
        Log.d(TAG, "powerOff() called");
        GpioManager.getInstance().scanDevicePowerControl(false);
    }


    SerialPort serialPort;
    FileInputStream inputStream;
    FileOutputStream outputStream;
    SerialPortReadThread readThread;

    @Override
    public void initDevice(@NonNull Context context, @NonNull OnScanListener listener) {
        Log.d(TAG, "initDevice() called with: context = [" + context + "], listener = [" + listener + "]");
        this.listener = listener;
        try {
            serialPort = new SerialPort(new File(SCAN_SERIAL_PORT), 115200, 0);
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            readThread = new SerialPortReadThread(inputStream) {
                @Override
                public void onDataReceived(byte[] bytes) {
                    Log.i(TAG, "onDataReceived: " + HexStringUtils.bytesToHexString(bytes));
                    String data = "";
                    if (bytes[0] == 0x02) {
                        data += new String(bytes, 3, bytes.length - 6).replaceAll("[\r\n]", "");
                    } else if (bytes[0] == 0x05) {
                        return;
                    } else if (bytes[0] == 0x15) {
                        Log.e(TAG, "onDataReceived: ERROR 0x15");
                    } else {
                        data += new String(bytes).replaceAll("[\r\n]", "");
                    }
                    Log.e(TAG, "onDataReceived: " + data);
                    listener.onScan(data);
                }
            };
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeDevice() {
        Log.d(TAG, "closeDevice() called");
        readThread.release();
        try {
            readThread.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        serialPort.close();
    }

    @Override
    public void startScan() {
        if (stopScanForPowerOn != null && (!stopScanForPowerOn.isDone() || !stopScanForPowerOn.isCancelled())) {
            stopScanForPowerOn.cancel(true);
        }
        Log.d(TAG, "startScan() called");
        byte[] sendBytes = new byte[]{0x02, 0x07, 0x00, 0x53, 0x57, 0x30, 0x30, 0x30, 0x30, 0x31, (byte) 0x9B, 0x01, 0x03};
        try {
            outputStream.write(sendBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopScan() {
        Log.d(TAG, "stopScan() called");
        byte[] sendBytes = new byte[]{0x02, 0x07, 0x00, 0x53, 0x57, 0x46, 0x46, 0x46, 0x46, 0x46, (byte) 0x08, 0x02, 0x03};
        try {
            outputStream.write(sendBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
