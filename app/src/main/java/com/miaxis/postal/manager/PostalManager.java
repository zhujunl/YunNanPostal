package com.miaxis.postal.manager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.miaxis.postal.app.App;
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
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;

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

    private volatile AtomicBoolean uploading = new AtomicBoolean(false);
    private volatile AtomicBoolean updating = new AtomicBoolean(false);

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
                if (!App.getInstance().uploadEnable) {
                    return;
                }
                postal();
            }
        };
        handler.sendMessage(handler.obtainMessage(0));
    }

    public void startPostal() {
        if (uploading.get()) {
            return;
        }
        handler.removeMessages(0);
        handler.sendMessage(handler.obtainMessage(0));
    }

    public interface OnPostalInterruptListener {
        void onPostalInterrupt();
    }

    public void stopPostal(@NonNull OnPostalInterruptListener listener) {
        handler.removeMessages(0);
        updating.set(true);
        if (!uploading.get()) {
            listener.onPostalInterrupt();
        }
    }

    public void postal() {
        try {
            uploading.set(true);
            handler.removeMessages(0);
            if (warnLogRepository.loadWarnLogCount() > 0) {
                Log.d("asd", "loadWarnLogCount() > 0");
                postalWarnRecord();
                Log.d("asd", "postalWarnRecord 成功");
            } else {
                Log.d("asd", "loadWarnLogCount() <= 0");
                postalNormalRecord();
                Log.d("asd", "postalNormalRecord 成功");
            }
            handler.sendMessage(handler.obtainMessage(0));
        } catch (Exception e) {
            Log.e("asd", "Postal Exception:" + e.getMessage());
            handler.sendMessageDelayed(handler.obtainMessage(0), 30 * 60 * 1000);
            uploading.set(false);
        }
    }

    private void postalWarnRecord() throws IOException, MyException, NetResultFailedException {
        WarnLog warnLog = warnLogRepository.findOldestWarnLog();
        if (warnLog != null) {
            IDCardRecord idCardRecord = null;
            if (!TextUtils.isEmpty(warnLog.getVerifyId())) {
                idCardRecord = idCardRecordRepository.loadIDCardRecordByVerifyId(warnLog.getVerifyId());
            }
            if (idCardRecord != null) {
                TempId tempId = null;
                Integer warnId = null;
                try {
                    tempId = getIdCardRecordTempId(idCardRecord);
                    warnId = getWarnId(warnLog, tempId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List<Express> expressList = expressRepository.loadExpressByVerifyId(idCardRecord.getVerifyId());
                for (Express express : expressList) {
                    if (tempId == null) {
                        processException(express, new NetResultFailedException("请求服务器错误"));
                    }
                    try {
                        expressRepository.uploadLocalExpress(express, tempId, warnId, idCardRecord.getName(), idCardRecord.getChekStatus());
                        expressRepository.deleteExpress(express);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("asd", "PostalWarnRecord Exception:" + e.getMessage());
                        processException(express, e);
                    }
                }
                idCardRecordRepository.deleteIDCardRecord(idCardRecord);
            } else {
                warnLogRepository.uploadWarnLog(warnLog, null);
            }
            warnLogRepository.deleteWarnLog(warnLog);
        }
    }

    private void postalNormalRecord() throws MyException, IOException, NetResultFailedException {
        IDCardRecord idCardRecord = idCardRecordRepository.findOldestIDCardRecord();
        if (idCardRecord == null) {
            throw new MyException("未找到待上传日志");
        }
        TempId tempId = null;
        try {
            tempId = getIdCardRecordTempId(idCardRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Express> expressList = expressRepository.loadExpressByVerifyId(idCardRecord.getVerifyId());
        for (Express express : expressList) {
            if (tempId == null) {
                processException(express, new NetResultFailedException("请求服务器错误"));
            }
            try {
                expressRepository.uploadLocalExpress(express, tempId, null, idCardRecord.getName(), idCardRecord.getChekStatus());
                expressRepository.deleteExpress(express);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("asd", "PostalNormalRecord Exception:" + e.getMessage());
                processException(express, e);
            }
        }
        idCardRecordRepository.deleteIDCardRecord(idCardRecord);
    }

    private void processException(Express express, Exception e) throws MyException {
        express.setUploadError("" + e.getMessage());
        expressRepository.updateExpress(express);
        throw new MyException("上传失败");
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
