package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.util.Base64;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.event.FingerRegisterEvent;
import com.miaxis.postal.manager.FingerManager;

import org.greenrobot.eventbus.EventBus;

public class FingerRegisterViewModel extends BaseViewModel {

    public String mark;

    public MutableLiveData<Status> initFingerResult = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> fingerResultFlag = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> fingerImageUpdate = new SingleLiveEvent<>();
    public ObservableField<String> status = new ObservableField<>();

    private int progress = 1;

    public Bitmap fingerImageCache;

    private byte[] feature1;
    private byte[] feature2;
    private byte[] feature3;

    public byte[] template;

    public FingerRegisterViewModel() {
    }

    public void initFingerDevice() {
        initFingerResult.setValue(Status.LOADING);
        FingerManager.getInstance().init(App.getInstance(), listener);
    }

    public void registerFeature() {
        progress = 1;
        status.set("请按手指（0/3）");
        FingerManager.getInstance().getFingerFeatureAndImage();
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
            if (feature == null) {
                FingerManager.getInstance().getFingerFeatureAndImage();
                return;
            }
            if (hasImage) {
                fingerImageCache = image;
                fingerImageUpdate.postValue(true);
            }
            if (progress == 1) {
                feature1 = feature;
                status.set("请按手指（1/3）");
                progress = 2;
                FingerManager.getInstance().getFingerFeatureAndImage();
            } else if (progress == 2) {
                feature2 = feature;
                status.set("请按手指（2/3）");
                progress = 3;
                FingerManager.getInstance().getFingerFeatureAndImage();
            } else if (progress == 3) {
                feature3 = feature;
                if (feature1 != null && feature2 != null) {
                    template = FingerManager.getInstance().mergeFeature(feature1, feature2, feature3);
                    if (template != null) {
                        status.set("指纹采集成功，请验证手指");
                        progress = 4;
                        FingerManager.getInstance().getFingerFeatureAndImage();
                        return;
                    }
                }
                progress = 1;
                status.set("指纹模板合成失败，请重新采集\n请按手指（0/3）");
                FingerManager.getInstance().getFingerFeatureAndImage();
            } else if (progress == 4) {
                if (template != null) {
                    boolean result = FingerManager.getInstance().matchFeature(template, feature);
                    if (result) {
                        status.set("指纹采集成功");
                        EventBus.getDefault().postSticky(new FingerRegisterEvent(mark, Base64.encodeToString(template, Base64.NO_WRAP)));
                        fingerResultFlag.postValue(Boolean.TRUE);
                    } else {
                        status.set("验证失败，请重新验证手指");
                        FingerManager.getInstance().getFingerFeatureAndImage();
                    }
                } else {
                    progress = 1;
                    status.set("指纹模板合成失败，请重新采集\n请按手指（0/3）");
                    FingerManager.getInstance().getFingerFeatureAndImage();
                }
            }
        }
    };

}
