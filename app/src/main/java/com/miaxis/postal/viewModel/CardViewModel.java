package com.miaxis.postal.viewModel;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.manager.CardManager;
import com.miaxis.postal.manager.TTSManager;
import com.miaxis.postal.manager.ToastManager;

public class CardViewModel extends BaseViewModel {

    public MutableLiveData<Status> initCardResult = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> readCardFlag = new SingleLiveEvent<>();
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

    public IDCardRecord getIdCardRecord() {
        return idCardRecord;
    }
}
