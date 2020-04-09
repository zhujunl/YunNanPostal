package com.miaxis.postal.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.serialport.DeviceControlSpd;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.util.ValueUtil;
import com.speedata.libid2.IDInfor;
import com.speedata.libid2.IDManager;
import com.speedata.libid2.IID2Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    private IID2Service idManager;
    private Context context;
    private Handler handler;
    private IDCardListener listener;

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
        new Thread(() -> {
            try {
                idManager = IDManager.getInstance();
                boolean result = idManager.initDev(context, info -> {
                    if (info.isSuccess()) {
                        handler.sendMessage(handler.obtainMessage(0, transform(info)));
                        release();
//                        idManager.getIDInfor(false, false);
                    } else {
                        handler.sendMessage(handler.obtainMessage(1, info.getErrorMsg()));
                        startReadCard();
                    }
                }, "dev/ttyMT1", 115200, DeviceControlSpd.PowerType.MAIN, 93, 94);
                startReadCard();
                listener.onIDCardInitResult(result);
            } catch (IOException e) {
                e.printStackTrace();
                listener.onIDCardInitResult(false);
            }
        }).start();
    }

    public void startReadCard() {
        if (idManager != null) {
            idManager.getIDInfor(true, true);
        }
    }

    public void release() {
        try {
            //退出 释放二代证模块
            if (idManager != null) {
                idManager.releaseDev();
                idManager = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private IDCardRecord transform(IDInfor idInfor) {
        String startDay = "";
        String endDay = "";
        try {
            startDay = idInfor.getStartYear().trim() + "-" + idInfor.getStartMonth().trim() + "-" + idInfor.getStartDay().trim();
            endDay = idInfor.getEndYear().trim() + "-" + idInfor.getEndMonth().trim() + "-" + idInfor.getEndDay().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        IDCardRecord idCardRecord = new IDCardRecord.Builder()
                .name(idInfor.getName().trim())
                .birthday(idInfor.getYear().trim() + "-" + idInfor.getMonth().trim() + "-" + idInfor.getDay().trim())
                .address(idInfor.getAddress().trim())
                .cardNumber(idInfor.getNum().trim())
                .issuingAuthority(idInfor.getQianFa().trim())
                .validateStart(startDay)
                .validateEnd(endDay)
                .sex(idInfor.getSex().trim())
                .nation(idInfor.getNation().trim())
                .cardBitmap(idInfor.getBmps())
                .build();
        if (idInfor.isWithFinger()) {
            byte[] fingerprStringer = idInfor.getFingerprStringer();
            byte[] bFingerData0 = new byte[512];
            byte[] bFingerData1 = new byte[512];
            System.arraycopy(fingerprStringer, 1, bFingerData0, 0, bFingerData0.length);
            System.arraycopy(fingerprStringer, 513, bFingerData1, 0, bFingerData1.length - 1);
            idCardRecord.setFingerprintPosition0(ValueUtil.fingerPositionCovert(bFingerData0[5]));
            idCardRecord.setFingerprint0(Base64.encodeToString(bFingerData0, Base64.NO_WRAP));
            idCardRecord.setFingerprintPosition1(ValueUtil.fingerPositionCovert(bFingerData1[5]));
            idCardRecord.setFingerprint1(Base64.encodeToString(bFingerData1, Base64.NO_WRAP));
        }
        return idCardRecord;
    }

    /**
     * 检查身份证是否已经过期
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

    public interface IDCardListener {
        void onIDCardInitResult(boolean result);
        void onIDCardReceive(IDCardRecord idCardRecord, String message);
    }

}
