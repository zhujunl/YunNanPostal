package com.miaxis.postal.manager.scan;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.miaxis.postal.manager.strategy.bp990.BP990ScanStrategy;


public class BP990_ScanManager implements IScanManager {

    private BP990_ScanManager() {
    }

    public static BP990_ScanManager getInstance() {
        return SingletonHolder.instance;
    }


    private static class SingletonHolder {
        private static final BP990_ScanManager instance = new BP990_ScanManager();
    }

    @Override
    public void powerOn() {
// 忽略
    }

    @Override
    public void powerOff() {
// 忽略
    }
    private final BP990ScanStrategy scanStrategy = new BP990ScanStrategy();

    private static final String TAG = "Mx-BpScanManager";
    @Override
    public void initDevice(@NonNull Context context, @NonNull OnScanListener listener) {
        scanStrategy.initDevice(context, new BP990ScanStrategy.OnScanStatusListener() {
            @Override
            public void onScanStatus(boolean result) {

                Log.i(TAG, "onScanStatus: "+result);
            }
        });
        scanStrategy.setScanReadListener(new BP990ScanStrategy.OnScanReadListener() {
            @Override
            public void onScanRead(boolean result, String code) {
                Log.i(TAG, "onScanRead() called with: result = [" + result + "], code = [" + code + "]");
                listener.onScan(code);
            }
        });
    }

    @Override
    public void closeDevice() {
        scanStrategy.release();
        // 不下电？
        // GpioManager.getInstance().scanDevicePowerControl(false);
    }

    @Override
    public void startScan() {
        scanStrategy.scan();
    }

    @Override
    public void stopScan() {
        //忽略
    }

}
