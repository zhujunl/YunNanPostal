package com.miaxis.postal.manager.scan;

public class BP990S_ScanManager {

    private BP990S_ScanManager() {
    }

//    public static BP990S_ScanManager getInstance() {
//        return SingletonHolder.instance;
//    }
//
//    private static class SingletonHolder {
//        private static final BP990S_ScanManager instance = new BP990S_ScanManager();
//    }
//
//    /**
//     * ================================ 静态内部类单例 ================================
//     **/
//
//    private ScanInterface scanDecode;
//    private OnScanListener listener;
//
//    public void powerOn() {
//        scanControl(true);
//    }
//
//    public void powerOff() {
////        scanControl(false);
//    }
//
//    public void initDevice(@NonNull Context context, @NonNull OnScanListener listener) {
//        this.listener = listener;
//        scanDecode = new ScanDecode(context);
//        scanDecode.initService("true");
//        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
//            @Override
//            public void getBarcode(String data) {
//                listener.onScan(data);
//            }
//
//            @Override
//            public void getBarcodeByte(byte[] bytes) {
//            }
//        });
//    }
//
//    public void startScan() {
//        if (scanDecode != null) {
//            scanDecode.starScan();
//        }
//    }
//
//    public void stopScan() {
//        try {
//            if (scanDecode != null) {
//                scanDecode.stopScan();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void closeDevice() {
//        try {
//            if (scanDecode != null) {
//                scanDecode.onDestroy();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void scanControl(boolean scan) {
//        if (scan) {
//            SystemProperties.set("persist.sys.keyreport", "true");
////            SystemProperties.set("persist.sys.keyreportshow", "true");
//            SystemProperties.set("persist.sys.iscamera","close");
//            SystemProperties.set("persist.sys.scanstopimme","false");
//            Intent Barcodeintent = new Intent();
//            Barcodeintent.setPackage("com.geomobile.oemscanservice");
//            App.getInstance().startService(Barcodeintent);
//        } else {
//            SystemProperties.set("persist.sys.keyreport", "false");
////            SystemProperties.set("persist.sys.keyreportshow", "false");
//            SystemProperties.set("persist.sys.iscamera","close");
//            SystemProperties.set("persist.sys.scanstopimme","true");
//            Intent intentstop = new Intent();
//            intentstop.setAction("com.geomobile.se4500barcodestop");
//            App.getInstance().sendBroadcast(intentstop,null);
//            Intent Barcodeintent = new Intent();
//            Barcodeintent.setPackage("com.geomobile.oemscanservice");
//            App.getInstance().stopService(Barcodeintent);
//        }
//    }
}
