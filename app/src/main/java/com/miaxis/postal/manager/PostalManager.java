package com.miaxis.postal.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.data.entity.WarnLog;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.data.repository.ExpressRepository;
import com.miaxis.postal.data.repository.IDCardRecordRepository;
import com.miaxis.postal.data.repository.IDCardRepository;
import com.miaxis.postal.data.repository.WarnLogRepository;
import com.miaxis.postal.util.FileUtil;
import com.miaxis.postal.util.StringUtils;
import com.miaxis.postal.util.ValueUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

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

    //去掉MainActivity HomeFragment 在onResume中的刷新 减少刷新频率
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
            //是否草稿
            if (idCardRecord!=null&&idCardRecord.isDraft()){
                return;
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
                        throw new MyException("服务器请求错误");
                    }
                    try {
                        //是否是草稿 草稿不进行上传
                        if (!express.isDraft()) {
                            Log.e("asd", "数据发送 true");
                            expressRepository.uploadLocalExpress(express, tempId, warnId, idCardRecord.getName(), idCardRecord.getChekStatus());
                            expressRepository.deleteExpress(express);
                        }
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
        //是否草稿
        if (idCardRecord.isDraft()){
            return;
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
            try {//是否草稿
                if (!express.isDraft()) {
                    Log.e("asd 1", "数据发送 true");
                    expressRepository.uploadLocalExpress(express, tempId, null, idCardRecord.getName(), idCardRecord.getChekStatus());
                    expressRepository.deleteExpress(express);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("asd", "PostalNormalRecord Exception:" + e.getMessage());
                processException(express, e);
            }
        }
        idCardRecordRepository.deleteIDCardRecord(idCardRecord);
    }

    //抛出异常后就无法执行下面的请求了 所以去掉
    private void processException(Express express, Exception e) {
        express.setUploadError("" + e.getMessage());
        expressRepository.updateExpress(express);
    }

    //根据身份证信息和人证核验信息得到核验编号（不能使用数据库保存读取  因为TempId checkId 得到的是不固定的值）
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

    //得到报警Id
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

    /**
     * 判断网络连接是否可用
     */
    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        } else {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
