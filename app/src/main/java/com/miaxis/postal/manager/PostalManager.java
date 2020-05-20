package com.miaxis.postal.manager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.repository.ExpressRepository;
import com.miaxis.postal.data.repository.IDCardRecordRepository;

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
            IDCardRecord idCardRecord = IDCardRecordRepository.getInstance().findOldestIDCardRecord();
            if (idCardRecord == null) throw new MyException("未找到待上传日志");
            TempId tempId = IDCardRecordRepository.getInstance().uploadIDCardRecord(idCardRecord);
            List<Express> expressList = ExpressRepository.getInstance().loadExpressByVerifyId(idCardRecord.getVerifyId());
            for (Express express : expressList) {
                ExpressRepository.getInstance().uploadLocalExpress(express, tempId);
                express.setUpload(true);
                ExpressRepository.getInstance().updateExpress(express);
            }
            idCardRecord.setUpload(true);
            IDCardRecordRepository.getInstance().updateIdCardRecord(idCardRecord);
            Log.e("asd", "Postal成功");
            handler.sendMessage(handler.obtainMessage(0));
        } catch (Exception e) {
            Log.e("asd", "" + e.getMessage());
            handler.sendMessageDelayed(handler.obtainMessage(0), 30 * 60 * 1000);
        } finally {
            uploading = false;
        }


//        handler.removeMessages(0);
//        Observable.create((ObservableOnSubscribe<IDCardRecord>) emitter -> {
//            uploading = true;
//            IDCardRecord idCardRecord = IDCardRecordRepository.getInstance().findOldestIDCardRecord();
//            if (idCardRecord != null) {
//                emitter.onNext(idCardRecord);
//            } else {
//                emitter.onError(new MyException("未找到待上传日志"));
//            }
//        })
//                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
//                .observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
//                .doOnNext(idCardRecord -> {
//                    TempId tempId = IDCardRecordRepository.getInstance().uploadIDCardRecord(idCardRecord);
//                    List<Express> expressList = ExpressRepository.getInstance().loadExpressByVerifyId(idCardRecord.getVerifyId());
//                    for (Express express : expressList) {
//                        ExpressRepository.getInstance().uploadLocalExpress(express, tempId);
//                        express.setUpload(true);
//                        ExpressRepository.getInstance().updateExpress(express);
//                    }
//                    idCardRecord.setUpload(true);
//                    IDCardRecordRepository.getInstance().updateIdCardRecord(idCardRecord);
//                })
//                .subscribe(idCardRecord -> {
//                    Log.e("asd", "Postal成功");
//                    handler.sendMessage(handler.obtainMessage(0));
//                }, throwable -> {
//                    throwable.printStackTrace();
//                    Log.e("asd", "" + throwable.getMessage());
//                    uploading = false;
//                    handler.sendMessageDelayed(handler.obtainMessage(0), 60 * 60 * 1000);
//                });
    }

}
