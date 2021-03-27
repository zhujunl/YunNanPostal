package com.miaxis.postal.manager;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.miaxis.postal.manager.scan.BP990S_ScanManager;
import com.miaxis.postal.manager.scan.BP990_ScanManager;
import com.miaxis.postal.manager.scan.IScanManager;

import java.util.Objects;

import androidx.annotation.NonNull;

public class ScanManager {

    IScanManager iScanManager;
    private static final String TAG = "ScanManager";

    private ScanManager() {
        Log.i(TAG, "MANUFACTURER: " + Build.MANUFACTURER);
        Log.i(TAG, "MODEL: " + Build.MODEL);
        if (Objects.equals(Build.MANUFACTURER, "QUALCOMM")) {
            //            Objects.equals(Build.MODEL,"BP-900")
            iScanManager = BP990_ScanManager.getInstance();
        } else {
            iScanManager = BP990S_ScanManager.getInstance();
        }
    }

    public static ScanManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final ScanManager instance = new ScanManager();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/


    public void powerOn() {
        iScanManager.powerOn();
    }

    public void powerOff() {
        iScanManager.powerOff();
    }

    Handler handler = new Handler(Looper.getMainLooper());

    public void initDevice(@NonNull Context context, @NonNull OnScanListener listener) {
        iScanManager.initDevice(context, code -> handler.post(() -> listener.onScan(code)));
    }

    public void startScan() {
        iScanManager.startScan();
    }

    public void stopScan() {
        iScanManager.stopScan();
    }

    public void closeDevice() {
        iScanManager.closeDevice();
    }

    public interface OnScanListener {

        void onScan(String code);

    }

}
