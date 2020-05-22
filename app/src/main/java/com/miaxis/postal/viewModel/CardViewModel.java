package com.miaxis.postal.viewModel;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.amap.api.location.AMapLocation;
import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.WarnLog;
import com.miaxis.postal.data.repository.WarnLogRepository;
import com.miaxis.postal.manager.AmapManager;
import com.miaxis.postal.manager.CardManager;
import com.miaxis.postal.manager.DataCacheManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.manager.TTSManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.DateUtil;

import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CardViewModel extends BaseViewModel {

    public MutableLiveData<Status> initCardResult = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> readCardFlag = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> saveFlag = new SingleLiveEvent<>();
    public ObservableField<String> cardMessage = new ObservableField<>();

    private IDCardRecord idCardRecord;

    public CardViewModel() {
    }

    public void startReadCard() {
        initCardResult.setValue(Status.LOADING);
        CardManager.getInstance().init(App.getInstance(), listener);
    }

    public void stopReadCard() {
        CardManager.getInstance().stopReadCard();
    }

    private CardManager.IDCardListener listener = new CardManager.IDCardListener() {
        @Override
        public void onIDCardInitResult(boolean result) {
            initCardResult.postValue(result ? Status.SUCCESS : Status.FAILED);
            if (result) {
                TTSManager.getInstance().playVoiceMessageFlush("请放置身份证");
            }
        }

        @Override
        public void onIDCardReceive(IDCardRecord data, String message) {
            initCardResult.postValue(Status.SUCCESS);
            if (data != null) {
                if (!CardManager.getInstance().checkIsOutValidate(data)) {
                    idCardRecord = data;
                    cardMessage.set(message);
                    TTSManager.getInstance().playVoiceMessageFlush("读卡成功");
                    if (idCardRecord != null) {
                        readCardFlag.setValue(Boolean.TRUE);
                    }
                } else {
                    cardMessage.set("身份证已过期");
                    toast.setValue(ToastManager.getToastBody("身份证已过期", ToastManager.INFO));
                    TTSManager.getInstance().playVoiceMessageFlush("身份证已过期");
                }
            } else {
                cardMessage.set(message);
            }
        }
    };

    public void alarm() {
        waitMessage.setValue("操作执行中");
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            WarnLog warnLog = makeWarnLogWithoutIdCardRecord();
            WarnLogRepository.getInstance().saveWarnLog(warnLog);
            emitter.onNext(Boolean.TRUE);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(flag -> {
                    waitMessage.setValue("");
                    resultMessage.setValue("数据已缓存，将于后台自动传输");
                    saveFlag.setValue(Boolean.TRUE);
                    PostalManager.getInstance().startPostal();
                }, throwable -> {
                    waitMessage.setValue("");
                    resultMessage.setValue("数据缓存失败，失败原因：\n" + throwable.getMessage());
                });
    }

    private WarnLog makeWarnLogWithoutIdCardRecord() {
        Courier courier = DataCacheManager.getInstance().getCourier();
        AMapLocation aMapLocation = AmapManager.getInstance().getaMapLocation();
        return new WarnLog.Builder()
                .sendAddress(aMapLocation != null ? aMapLocation.getAddress() : "")
                .expressmanId(courier.getId())
                .expressmanName(courier.getName())
                .expressmanPhone(courier.getPhone())
                .createTime(DateUtil.DATE_FORMAT.format(new Date()))
                .upload(false)
                .build();
    }

    public IDCardRecord getIdCardRecord() {
        return idCardRecord;
    }
}
