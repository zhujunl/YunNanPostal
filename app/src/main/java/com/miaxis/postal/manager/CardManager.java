package com.miaxis.postal.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.speedata.libid2.IDInfor;
import com.speedata.libid2.IDManager;
import com.speedata.libid2.IID2Service;

import java.io.IOException;

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
                listener.onIDCardReceive((IDInfor) msg.obj);
            }
        };
        initDevice();
    }

    private void initDevice() {
        new Thread(() -> {
            try {
                boolean result = idManager.initDev(context, info -> {
                    if (info.isSuccess()) {
                        handler.sendMessage(handler.obtainMessage(0, info));
//                        release();
                    } else {
                        startReadCard();
                    }
                });
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

    public interface IDCardListener {
        void onIDCardInitResult(boolean result);

        void onIDCardReceive(IDInfor idInfor);
    }

}
