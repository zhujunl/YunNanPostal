package com.miaxis.postal.manager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.data.entity.WarnLog;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.repository.ExpressRepository;
import com.miaxis.postal.data.repository.IDCardRecordRepository;
import com.miaxis.postal.data.repository.WarnLogRepository;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PostalManager {

    private PostalManager() {
    }

    public static PostalManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final PostalManager instance = new PostalManager();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    private HandlerThread handlerThread;
    private Handler handler;

    private volatile boolean uploading = false;

    public void init() {
        handlerThread = new HandlerThread("UploadExpress");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                postal();
            }
        };
        handler.sendMessage(handler.obtainMessage(0));
    }

    public void startPostal() {
        if (uploading) return;
        handler.removeMessages(0);
        handler.sendMessage(handler.obtainMessage(0));
    }

    public void postal() {
        try {
            uploading = true;
            handler.removeMessages(0);
            if (WarnLogRepository.getInstance().loadWarnLogCount() > 0) {
                postalWarnRecord();
            } else {
                postalNormalRecord();
            }
            Log.e("asd", "Postal成功");
            handler.sendMessage(handler.obtainMessage(0));
        } catch (Exception e) {
            Log.e("asd", "" + e.getMessage());
            handler.sendMessageDelayed(handler.obtainMessage(0), 30 * 60 * 1000);
        } finally {
            uploading = false;
        }
    }

    private void postalWarnRecord() throws IOException, MyException, NetResultFailedException {
        WarnLog warnLog = WarnLogRepository.getInstance().findOldestWarnLog();
        if (warnLog != null) {
            IDCardRecord idCardRecord = null;
            if (!TextUtils.isEmpty(warnLog.getVerifyId())) {
                idCardRecord = IDCardRecordRepository.getInstance().loadIDCardRecord(warnLog.getVerifyId());
            }
            if (idCardRecord != null) {
                TempId tempId = IDCardRecordRepository.getInstance().uploadIDCardRecord(idCardRecord);
                Integer warnId = WarnLogRepository.getInstance().uploadWarnLog(warnLog, tempId);
                List<Express> expressList = ExpressRepository.getInstance().loadExpressByVerifyId(idCardRecord.getVerifyId());
                for (Express express : expressList) {
                    ExpressRepository.getInstance().uploadLocalExpress(express, tempId, warnId);
                    express.setUpload(true);
                    ExpressRepository.getInstance().updateExpress(express);
                }
                idCardRecord.setUpload(true);
                IDCardRecordRepository.getInstance().updateIdCardRecord(idCardRecord);
                warnLog.setUpload(true);
                WarnLogRepository.getInstance().updateWarnLog(warnLog);
            } else {
                WarnLogRepository.getInstance().uploadWarnLog(warnLog, null);
                warnLog.setUpload(true);
                WarnLogRepository.getInstance().updateWarnLog(warnLog);
            }
        }
    }

    private void postalNormalRecord() throws MyException, IOException, NetResultFailedException {
        IDCardRecord idCardRecord = IDCardRecordRepository.getInstance().findOldestIDCardRecord();
        if (idCardRecord == null) throw new MyException("未找到待上传日志");
        TempId tempId = IDCardRecordRepository.getInstance().uploadIDCardRecord(idCardRecord);
        List<Express> expressList = ExpressRepository.getInstance().loadExpressByVerifyId(idCardRecord.getVerifyId());
        for (Express express : expressList) {
            ExpressRepository.getInstance().uploadLocalExpress(express, tempId, null);
            express.setUpload(true);
            ExpressRepository.getInstance().updateExpress(express);
        }
        idCardRecord.setUpload(true);
        IDCardRecordRepository.getInstance().updateIdCardRecord(idCardRecord);
    }

}
