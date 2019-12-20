package com.miaxis.postal.manager;

import android.content.Context;
import android.os.SystemProperties;

import androidx.annotation.NonNull;

import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;

public class ScanManager {

    private ScanManager() {
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

    private ScanInterface scanDecode;
    private OnScanListener listener;

    public void initDevice(@NonNull Context context, @NonNull OnScanListener listener) {
        this.listener = listener;
        scanControl(true);
        scanDecode = new ScanDecode(context);
        scanDecode.initService("true");
        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @Override
            public void getBarcode(String data) {
                listener.onScan(data);
            }

            @Override
            public void getBarcodeByte(byte[] bytes) {
            }
        });
    }

    public void startScan() {
        if (scanDecode != null) {
            scanDecode.starScan();
        }
    }

    public void stopScan() {
        try {
            if (scanDecode != null) {
                scanDecode.stopScan();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeDevice() {
        try {
            if (scanDecode != null) {
                scanDecode.onDestroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scanControl(boolean scan) {
//        if (scan) {
//            SystemProperties.set("persist.sys.keyreport", "true");
//            SystemProperties.set("persist.sys.keyreportshow", "true");
//        } else {
//            SystemProperties.set("persist.sys.keyreport", "false");
//            SystemProperties.set("persist.sys.keyreportshow", "false");
//        }
    }

    public interface OnScanListener {
        void onScan(String code);
    }

}
