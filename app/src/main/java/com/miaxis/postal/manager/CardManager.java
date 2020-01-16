package com.miaxis.postal.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.serialport.DeviceControlSpd;

import androidx.annotation.NonNull;

import com.miaxis.postal.data.entity.IDCardRecord;
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

    private IID2Service idManager = IDManager.getInstance();
    private Context context;
    private Handler handler;
    private IDCardListener listener;
    private volatile boolean release = false;

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
        idManager.getIDInfor(false, true);
    }

    public void release() {
        try {
            release = false;
//            idManager.getIDInfor(false, false);
            //退出 释放二代证模块
            if (idManager != null) {
                idManager.releaseDev();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private IDCardRecord transform(IDInfor idInfor) {
        return IDCardRecord.IDCardRecordBuilder.anIDCardRecord()
                .name(idInfor.getName().trim())
                .birthday(idInfor.getYear().trim() + "-" + idInfor.getMonth().trim() + "-" + idInfor.getDay().trim())
                .address(idInfor.getAddress().trim())
                .cardNumber(idInfor.getNum().trim())
                .issuingAuthority(idInfor.getQianFa().trim())
                .validateStart(idInfor.getStartYear().trim() + "-" + idInfor.getStartMonth().trim() + "-" + idInfor.getStartDay().trim())
                .validateEnd(idInfor.getEndYear().trim() + "-" + idInfor.getEndMonth().trim() + "-" + idInfor.getEndDay().trim())
                .sex(idInfor.getSex().trim())
                .nation(idInfor.getNation().trim())
                .cardBitmap(idInfor.getBmps())
                .build();
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
