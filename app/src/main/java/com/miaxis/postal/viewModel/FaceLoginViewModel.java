package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.entity.MxRGBImage;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.FaceManager;

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
            FaceManager.getInstance().setOrientation(90);
            FaceManager.getInstance().startLoop();
        }
    }

    public void stopFaceVerify() {
        FaceManager.getInstance().stopLoop();
        FaceManager.getInstance().setFeatureListener(null);
    }

    private FaceManager.OnFeatureExtractListener faceListener = (mxRGBImage, mxFaceInfoEx, feature) -> {
        Courier courier = courierLiveData.getValue();
        if (courier != null) {
            try {
                float score = FaceManager.getInstance().matchFeature(feature, Base64.decode(courier.getFaceFeature(), Base64.NO_WRAP));
                if (score >= ConfigManager.getInstance().getConfig().getVerifyScore()) {
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
