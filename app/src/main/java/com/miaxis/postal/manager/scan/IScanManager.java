package com.miaxis.postal.manager.scan;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * IScanManager
 *
 * @author zhangyw
 * Created on 2021/3/21.
 */
public interface IScanManager {

    public void powerOn();

    public void powerOff();

    public void initDevice(@NonNull Context context, @NonNull OnScanListener listener);

    public void startScan();

    public void stopScan();

    public void closeDevice();

    public interface OnScanListener {
        void onScan(String code);
    }

}