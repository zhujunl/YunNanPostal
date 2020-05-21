package com.miaxis.postal.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.serialport.DeviceControlSpd;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.util.ValueUtil;
import com.zz.impl.IDCardDeviceImpl;
import com.zz.impl.IDCardInterfaceService;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class CardManager {

    private CardManager() {
    }

    public static CardManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final CardManager instance = new CardManager();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    private static final String SERIAL_PORT = "/dev/ttyMT1";
    private static final int BAUD_RATE = 115200;

    private DeviceControlSpd deviceControl;
    private IDCardInterfaceService cardManager;

    private Context context;
    private Handler handler;
    private IDCardListener listener;

    private volatile AtomicBoolean running = new AtomicBoolean(false);

    public void init(@NonNull Context context, @NonNull IDCardListener listener) {
        this.context = context;
        this.listener = listener;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    listener.onIDCardReceive((IDCardRecord) msg.obj, "读卡成功");
                } else if (msg.what == 1) {
                    listener.onIDCardReceive(null, (String) msg.obj);
                }
            }
        };
        initDevice();
    }

    private void initDevice() {
        App.getInstance().getThreadExecutor().execute(() -> {
            try {
                deviceControl = new DeviceControlSpd(DeviceControlSpd.PowerType.MAIN, 93, 94);
                deviceControl.PowerOnDevice();
                Thread.sleep(500);
                cardManager = new IDCardDeviceImpl();
                if (isDeviceOpen()) {
                    listener.onIDCardInitResult(true);
                } else {
                    listener.onIDCardInitResult(false);
                }
                startReadCard();
            } catch (Exception e) {
                e.printStackTrace();
                listener.onIDCardInitResult(false);
            }
        });
    }

    private boolean isDeviceOpen() throws InterruptedException {
        int count = 4;
        String samId = readSamId();
        Log.e("asd", "ssssssssss" + samId);
        while (TextUtils.isEmpty(samId)) {
            Thread.sleep(100);
            samId = readSamId();
            count--;
            Log.e("asd", "sadadsadsadsa" + count);
            if (!TextUtils.isEmpty(samId)) {
                return true;
            }
            if (count == 0) {
                return false;
            }
        }
        return true;
    }

    private void startReadCard() {
        running.set(true);
        new ReadCardThread().start();
    }

    public void stopReadCard() {
        try {
            running.set(false);
            cardManager = null;
            if (deviceControl != null) {
                deviceControl.PowerOffDevice();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ReadCardThread extends Thread {

        @Override
        public void run() {
            while (running.get()) {
                if (cardManager != null) {
                    byte[] message = new byte[100];
                    try {
                        int result = cardManager.readIDCard(SERIAL_PORT, BAUD_RATE, 10, message);
                        if (result == 0x90) {
                            IDCardRecord transform;
                            int cardType = cardManager.getIDCardType();
                            if (cardType == 0 || cardType == 1 || cardType == 2) {
                                if (cardType == 0) {
                                    transform = transformID(cardManager);
                                } else if (cardType == 1) {
                                    transform = transformGreen(cardManager);
                                } else {
                                    transform = transformGAT(cardManager);
                                }
                                transformFingerprint(cardManager, transform);
                                if (listener != null) {
                                    stopReadCard();
                                    handler.sendMessage(handler.obtainMessage(0, transform));
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("asd", "读卡异常" + new String(message));
                    }
                } else {
                    stopReadCard();
                    break;
                }
            }
        }
    }

    private IDCardRecord transformID(IDCardInterfaceService cardManager) {
        return new IDCardRecord.Builder()
                .cardType("")
                .name(cardManager.getName())
                .birthday(cardManager.getBorn())
                .address(cardManager.getAddress())
                .cardNumber(cardManager.getIdNumber())
                .issuingAuthority(cardManager.getIssueOffice())
                .validateStart(cardManager.getBeginDate())
                .validateEnd(cardManager.getEndDate())
                .sex(cardManager.getSex())
                .nation(cardManager.getNation())
                .passNumber("")
                .issueCount("")
                .chineseName("")
                .version("")
                .cardBitmap(cardManager.getPhotoBmp())
                .build();
    }

    private IDCardRecord transformGreen(IDCardInterfaceService cardManager) {
        return new IDCardRecord.Builder()
                .cardType("I")
                .name(cardManager.getEnglishName())
                .birthday(cardManager.getBorn())
                .cardNumber(cardManager.getIdNumber())
                .issuingAuthority(cardManager.getIssueOffice())
                .validateStart(cardManager.getBeginDate())
                .validateEnd(cardManager.getEndDate())
                .sex(cardManager.getSex())
                .nation(cardManager.getAreaCode())
                .chineseName(cardManager.getName())
                .version(cardManager.getCardVersionNum())
                .cardBitmap(cardManager.getPhotoBmp())
                .build();
    }

    private IDCardRecord transformGAT(IDCardInterfaceService cardManager) {
        return new IDCardRecord.Builder()
                .cardType("J")
                .name(cardManager.getName())
                .birthday(cardManager.getBorn())
                .address(cardManager.getAddress())
                .cardNumber(cardManager.getIdNumber())
                .issuingAuthority(cardManager.getIssueOffice())
                .validateStart(cardManager.getBeginDate())
                .validateEnd(cardManager.getEndDate())
                .sex(cardManager.getSex())
                .nation(cardManager.getNation())
                .passNumber(cardManager.getPassportNum())
                .issueCount(cardManager.getIssueCount())
                .cardBitmap(cardManager.getPhotoBmp())
                .build();
    }

    private void transformFingerprint(IDCardInterfaceService cardManager, IDCardRecord idCardRecord) {
        byte[] fingerData = new byte[1024];
        int i = cardManager.getFingerData(fingerData);
        if (i == 0) {
            byte[] bFingerData0 = new byte[512];
            byte[] bFingerData1 = new byte[512];
            System.arraycopy(fingerData, 0, bFingerData0, 0, bFingerData0.length);
            System.arraycopy(fingerData, 512, bFingerData1, 0, bFingerData1.length);
            idCardRecord.setFingerprint0(Base64.encodeToString(bFingerData0, Base64.NO_WRAP));
            idCardRecord.setFingerprintPosition0(ValueUtil.fingerPositionCovert(bFingerData0[5]));
            idCardRecord.setFingerprint1(Base64.encodeToString(bFingerData1, Base64.NO_WRAP));
            idCardRecord.setFingerprintPosition1(ValueUtil.fingerPositionCovert(bFingerData1[5]));
        }
    }

    /**
     * 检查身份证是否已经过期
     *
     * @return true - 已过期 false - 未过期
     */
    public boolean checkIsOutValidate(IDCardRecord idCardRecord) {
        try {
            SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date validEndDate = myFmt.parse(idCardRecord.getValidateEnd());
            return validEndDate.getTime() < System.currentTimeMillis();
        } catch (ParseException e) {
            return false;
        }
    }

    private String readSamId() {
        try {
            byte[] message = new byte[201];
            byte[] samVersion = new byte[201];
            int result = cardManager.getSAMID(SERIAL_PORT, BAUD_RATE, samVersion, message);
            if (result == 0x90) {
                return new String(samVersion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public interface IDCardListener {
        void onIDCardInitResult(boolean result);

        void onIDCardReceive(IDCardRecord idCardRecord, String message);
    }

//    private IID2Service idManager;
//    private Context context;
//    private Handler handler;
//    private IDCardListener listener;
//
//    public void init(@NonNull Context context, @NonNull IDCardListener listener) {
//        this.context = context;
//        this.listener = listener;
//        handler = new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(@NonNull Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == 0) {
//                    listener.onIDCardReceive((IDCardRecord) msg.obj, "读卡成功");
//                } else if (msg.what == 1) {
//                    listener.onIDCardReceive(null, (String) msg.obj);
//                }
//            }
//        };
//        initDevice();
//    }
//
//    private void initDevice() {
//        new Thread(() -> {
//            try {
//                idManager = IDManager.getInstance();
//                boolean result = idManager.initDev(context, info -> {
//                    if (info.isSuccess()) {
//                        handler.sendMessage(handler.obtainMessage(0, transform(info)));
//                        release();
////                        idManager.getIDInfor(false, false);
//                    } else {
//                        handler.sendMessage(handler.obtainMessage(1, info.getErrorMsg()));
//                        startReadCard();
//                    }
//                }, "dev/ttyMT1", 115200, DeviceControlSpd.PowerType.MAIN, 93, 94);
//                startReadCard();
//                listener.onIDCardInitResult(result);
//            } catch (IOException e) {
//                e.printStackTrace();
//                listener.onIDCardInitResult(false);
//            }
//        }).start();
//    }
//
//    public void startReadCard() {
//        if (idManager != null) {
//            idManager.getIDInfor(true, true);
//        }
//    }
//
//    public void release() {
//        try {
//            //退出 释放二代证模块
//            if (idManager != null) {
//                idManager.releaseDev();
//                idManager = null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private IDCardRecord transform(IDInfor idInfor) {
//        String startDay = "";
//        String endDay = "";
//        try {
//            startDay = idInfor.getStartYear().trim() + "-" + idInfor.getStartMonth().trim() + "-" + idInfor.getStartDay().trim();
//            endDay = idInfor.getEndYear().trim() + "-" + idInfor.getEndMonth().trim() + "-" + idInfor.getEndDay().trim();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        IDCardRecord idCardRecord = new IDCardRecord.Builder()
//                .name(idInfor.getName().trim())
//                .birthday(idInfor.getYear().trim() + "-" + idInfor.getMonth().trim() + "-" + idInfor.getDay().trim())
//                .address(idInfor.getAddress().trim())
//                .cardNumber(idInfor.getNum().trim())
//                .issuingAuthority(idInfor.getQianFa().trim())
//                .validateStart(startDay)
//                .validateEnd(endDay)
//                .sex(idInfor.getSex().trim())
//                .nation(idInfor.getNation().trim())
//                .cardBitmap(idInfor.getBmps())
//                .build();
//        if (idInfor.isWithFinger()) {
//            byte[] fingerprStringer = idInfor.getFingerprStringer();
//            byte[] bFingerData0 = new byte[512];
//            byte[] bFingerData1 = new byte[512];
//            System.arraycopy(fingerprStringer, 1, bFingerData0, 0, bFingerData0.length);
//            System.arraycopy(fingerprStringer, 513, bFingerData1, 0, bFingerData1.length - 1);
//            idCardRecord.setFingerprintPosition0(ValueUtil.fingerPositionCovert(bFingerData0[5]));
//            idCardRecord.setFingerprint0(Base64.encodeToString(bFingerData0, Base64.NO_WRAP));
//            idCardRecord.setFingerprintPosition1(ValueUtil.fingerPositionCovert(bFingerData1[5]));
//            idCardRecord.setFingerprint1(Base64.encodeToString(bFingerData1, Base64.NO_WRAP));
//        }
//        return idCardRecord;
//    }
//
//    /**
//     * 检查身份证是否已经过期
//     * @return true - 已过期 false - 未过期
//     */
//    public boolean checkIsOutValidate(IDCardRecord idCardRecord) {
//        try {
//            SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
//            Date validEndDate = myFmt.parse(idCardRecord.getValidateEnd());
//            return validEndDate.getTime() < System.currentTimeMillis();
//        } catch (ParseException e) {
//            return false;
//        }
//    }
//
//    public interface IDCardListener {
//        void onIDCardInitResult(boolean result);
//        void onIDCardReceive(IDCardRecord idCardRecord, String message);
//    }

}
