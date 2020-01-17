package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.util.Base64;

import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.PostalApp;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.manager.FingerManager;

import java.util.ArrayList;
import java.util.List;

public class FingerVerifyDialogViewModel extends BaseViewModel {

    public MutableLiveData<Status> initFingerResult = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> fingerResultFlag = new SingleLiveEvent<>();

    private List<String> featureList = new ArrayList<>();

    public FingerVerifyDialogViewModel() {
    }

    public void initFingerDevice() {
        initFingerResult.setValue(Status.LOADING);
        FingerManager.getInstance().init(PostalApp.getInstance(), listener);
    }

    public void verifyFinger(List<String> featureList) {
        this.featureList = featureList;
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
            if (featureList.isEmpty()) return;
            if (feature == null) {
                FingerManager.getInstance().getFingerFeature();
            } else {
                for (String value : featureList) {
                    boolean result = false;
                    try {
                        result = FingerManager.getInstance().matchFeature(feature, Base64.decode(value, Base64.NO_WRAP));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result) {
                        fingerResultFlag.postValue(Boolean.TRUE);
                        return;
                    }
                }
                fingerResultFlag.postValue(Boolean.FALSE);
            }
        }
    };

}
