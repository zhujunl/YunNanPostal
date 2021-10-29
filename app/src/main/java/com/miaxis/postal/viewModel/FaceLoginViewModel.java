package com.miaxis.postal.viewModel;

import android.text.TextUtils;
import android.util.Base64;

import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.FaceManager;
import com.miaxis.postal.util.ValueUtil;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

public class FaceLoginViewModel extends BaseViewModel {

    public MutableLiveData<Courier> courierLiveData = new MutableLiveData<>();
    public ObservableField<String> hint = new ObservableField<>("请看镜头");
    public MutableLiveData<Boolean> verifyFlag = new SingleLiveEvent<>();

    public FaceLoginViewModel() {
    }

    public void startFaceVerify() {
        if (courierLiveData.getValue() != null) {
            FaceManager.getInstance().setFeatureListener(faceListener);
            FaceManager.getInstance().setNeedNextFeature(true);
            FaceManager.getInstance().setOrientation(CameraManager.getInstance().getPreviewOrientation());
            FaceManager.getInstance().startLoop();
        }
    }

    public void stopFaceVerify() {
        FaceManager.getInstance().stopLoop();
        FaceManager.getInstance().setFeatureListener(null);
    }

    private FaceManager.OnFeatureExtractListener faceListener = (mxRGBImage, mxFaceInfoEx, feature, mask) -> {
        Courier courier = courierLiveData.getValue();
        if (courier != null) {
            try {
                float score = 0f;
                if (mask && !TextUtils.isEmpty(courier.getMaskFaceFeature())) {
                    score = FaceManager.getInstance().matchMaskFeature(feature, Base64.decode(courier.getMaskFaceFeature(), Base64.NO_WRAP));
                } else if (!mask && !TextUtils.isEmpty(courier.getFaceFeature())) {
                    score = FaceManager.getInstance().matchFeature(feature, Base64.decode(courier.getFaceFeature(), Base64.NO_WRAP));
                }
                Config config = ConfigManager.getInstance().getConfig();
                if (mask ? score >= ValueUtil.DEFAULT_MASK_VERIFY_SCORE : score >= ValueUtil.DEFAULT_VERIFY_SCORE) {
                    verifyFlag.postValue(Boolean.TRUE);
                    stopFaceVerify();
                    return;
                } else {
                    hint.set("比对失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            FaceManager.getInstance().setNeedNextFeature(true);
        }
    };

}
