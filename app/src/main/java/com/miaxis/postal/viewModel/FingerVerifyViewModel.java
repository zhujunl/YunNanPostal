package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.amap.api.location.AMapLocation;
import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.WarnLog;
import com.miaxis.postal.data.repository.IDCardRecordRepository;
import com.miaxis.postal.data.repository.WarnLogRepository;
import com.miaxis.postal.manager.AmapManager;
import com.miaxis.postal.manager.DataCacheManager;
import com.miaxis.postal.manager.FingerManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FingerVerifyViewModel extends BaseViewModel {

    public MutableLiveData<IDCardRecord> idCardRecordLiveData = new MutableLiveData<>();
    public ObservableField<String> countDown = new ObservableField<>();
    public ObservableField<String> hint = new ObservableField<>("");

    public MutableLiveData<Status> initFingerResult = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> fingerResultFlag = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> saveFlag = new SingleLiveEvent<>();

    public FingerVerifyViewModel() {
    }

    public void initFingerDevice() {
        initFingerResult.setValue(Status.LOADING);
        FingerManager.getInstance().init(App.getInstance(), listener);
    }

    public void verifyFinger() {
        hint.set("请按手指");
        FingerManager.getInstance().getFingerFeature();
    }

    public void releaseFingerDevice() {
        FingerManager.getInstance().release();
    }

    private FingerManager.FingerListener listener = new FingerManager.FingerListener() {
        @Override
        public void onFingerInitResult(boolean result) {
            initFingerResult.postValue(result ? Status.SUCCESS : Status.FAILED);
        }

        @Override
        public void onFingerReceive(byte[] feature, Bitmap image, boolean hasImage) {
            IDCardRecord idCardRecord = idCardRecordLiveData.getValue();
            if (idCardRecord == null) return;
            if (TextUtils.isEmpty(idCardRecord.getFingerprint0()) || TextUtils.isEmpty(idCardRecord.getFingerprint1())) return;
            if (feature == null) {
                FingerManager.getInstance().getFingerFeature();
            } else {
                List<String> featureList = new ArrayList<>();
                featureList.add(idCardRecord.getFingerprint0());
                featureList.add(idCardRecord.getFingerprint1());
                for (String value : featureList) {
                    boolean result = false;
                    try {
                        result = FingerManager.getInstance().matchFeature(feature, Base64.decode(value, Base64.NO_WRAP));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result) {
                        hint.set("比对成功");
                        fingerResultFlag.postValue(Boolean.TRUE);
                        return;
                    }
                }
                fingerResultFlag.postValue(Boolean.FALSE);
                hint.set("比对失败，请重按手指");
                FingerManager.getInstance().getFingerFeature();
            }
        }
    };

    public void alarm() {
        IDCardRecord cardMessage = idCardRecordLiveData.getValue();
        if (cardMessage != null) {
            waitMessage.setValue("操作执行中");
            Observable.create((ObservableOnSubscribe<String>) emitter -> {
                cardMessage.setUpload(false);
                String verifyId = IDCardRecordRepository.getInstance().saveIdCardRecord(cardMessage);
                emitter.onNext(verifyId);
            })
                    .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                    .observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                    .doOnNext(verifyId -> {
                        Courier courier = DataCacheManager.getInstance().getCourier();
                        AMapLocation aMapLocation = AmapManager.getInstance().getaMapLocation();
                        WarnLog warnLog = new WarnLog.Builder()
                                .verifyId(verifyId)
                                .sendCardNo(cardMessage.getCardNumber())
                                .sendName(cardMessage.getName())
                                .sendAddress(aMapLocation != null ? aMapLocation.getAddress() : "")
                                .expressmanId(courier.getId())
                                .expressmanName(courier.getName())
                                .expressmanPhone(courier.getPhone())
                                .createTime(DateUtil.DATE_FORMAT.format(new Date()))
                                .upload(false)
                                .build();
                        WarnLogRepository.getInstance().saveWarnLog(warnLog);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        waitMessage.setValue("");
                        resultMessage.setValue("数据已缓存，将于后台自动传输");
                        saveFlag.setValue(Boolean.TRUE);
                        PostalManager.getInstance().startPostal();
                    }, throwable -> {
                        waitMessage.setValue("");
                        resultMessage.setValue("数据缓存失败，失败原因：\n" + throwable.getMessage());
                    });
        }
    }

}
