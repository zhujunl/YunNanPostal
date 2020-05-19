package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.manager.FingerManager;

import java.util.ArrayList;
import java.util.List;

public class FingerVerifyViewModel extends BaseViewModel {

    public MutableLiveData<IDCardRecord> idCardRecordLiveData = new MutableLiveData<>();
    public ObservableField<String> countDown = new ObservableField<>();
    public ObservableField<String> hint = new ObservableField<>("");

    public MutableLiveData<Status> initFingerResult = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> fingerResultFlag = new SingleLiveEvent<>();

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

}
