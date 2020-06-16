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
    
    private ExpressRepository expressRepository = ExpressRepository.getInstance();
    private IDCardRecordRepository idCardRecordRepository = IDCardRecordRepository.getInstance();
    private WarnLogRepository warnLogRepository = WarnLogRepository.getInstance();

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
            if (warnLogRepository.loadWarnLogCount() > 0) {
                postalWarnRecord();
            } else {
                postalNormalRecord();
            }
            Log.e("asd", "Postal成功");
            handler.sendMessage(handler.obtainMessage(0));
        } catch (Exception e) {
            Log.e("asd", "" + e.getMessage());
            handler.sendMessageDelayed(handler.obtainMessage(0), 30 * 60 * 1000);
            uploading = false;
        }
    }

    private void postalWarnRecord() throws IOException, MyException, NetResultFailedException {
        WarnLog warnLog = warnLogRepository.findOldestWarnLog();
        if (warnLog != null) {
            IDCardRecord idCardRecord = null;
            if (!TextUtils.isEmpty(warnLog.getVerifyId())) {
                idCardRecord = idCardRecordRepository.loadIDCardRecord(warnLog.getVerifyId());
            }
            if (idCardRecord != null) {
                TempId tempId = getIdCardRecordTempId(idCardRecord);
                Integer warnId = getWarnId(warnLog, tempId);
                List<Express> expressList = expressRepository.loadExpressByVerifyId(idCardRecord.getVerifyId());
                for (Express express : expressList) {
                    expressRepository.uploadLocalExpress(express, tempId, warnId, idCardRecord.getName());
                    expressRepository.deleteExpress(express);
                }
                idCardRecordRepository.deleteIDCardRecord(idCardRecord);
                warnLogRepository.deleteWarnLog(warnLog);
            } else {
                warnLogRepository.uploadWarnLog(warnLog, null);
                warnLogRepository.deleteWarnLog(warnLog);
            }
        }
    }

    private void postalNormalRecord() throws MyException, IOException, NetResultFailedException {
        IDCardRecord idCardRecord = idCardRecordRepository.findOldestIDCardRecord();
        if (idCardRecord == null) throw new MyException("未找到待上传日志");
        TempId tempId = getIdCardRecordTempId(idCardRecord);
        List<Express> expressList = expressRepository.loadExpressByVerifyId(idCardRecord.getVerifyId());
        for (Express express : expressList) {
            expressRepository.uploadLocalExpress(express, tempId, null, idCardRecord.getName());
            expressRepository.deleteExpress(express);
        }
        idCardRecordRepository.deleteIDCardRecord(idCardRecord);
    }

    private TempId getIdCardRecordTempId(IDCardRecord idCardRecord) throws IOException, MyException, NetResultFailedException {
        TempId tempId;
        if (TextUtils.isEmpty(idCardRecord.getPersonId()) || TextUtils.isEmpty(idCardRecord.getCheckId())) {
            tempId = idCardRecordRepository.uploadIDCardRecord(idCardRecord);
            idCardRecord.setPersonId(tempId.getPersonId());
            idCardRecord.setCheckId(tempId.getCheckId());
            idCardRecordRepository.updateIdCardRecord(idCardRecord);
        } else {
            tempId = new TempId(idCardRecord.getPersonId(), idCardRecord.getCheckId());
        }
        return tempId;
    }

    private Integer getWarnId(WarnLog warnLog, TempId tempId) throws IOException, MyException, NetResultFailedException {
        Integer warnId;
        if (warnLog.getWarnId() == null) {
            warnId = warnLogRepository.uploadWarnLog(warnLog, tempId);
            warnLog.setWarnId(warnId);
            warnLogRepository.saveWarnLog(warnLog);
        } else {
            warnId = warnLog.getWarnId();
        }
        return warnId;
    }

}
