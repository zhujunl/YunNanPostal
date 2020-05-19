package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.PhotoFaceFeature;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.FaceManager;
import com.miaxis.postal.manager.TTSManager;
import com.miaxis.postal.manager.ToastManager;

import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FaceVerifyViewModel extends BaseViewModel {

    public MutableLiveData<IDCardRecord> idCardRecordLiveData = new MutableLiveData<>();
    public ObservableField<String> hint = new ObservableField<>("");
    public MutableLiveData<IDCardRecord> verifyFlag = new SingleLiveEvent<>();

    public ObservableField<String> countDown = new ObservableField<>();

    private PhotoFaceFeature cardFeature;

    public FaceVerifyViewModel() {
    }

    public void startFaceVerify(IDCardRecord idCardRecord) {
        cardFeature = null;
        hint.set("身份证证件照处理中");
        Disposable disposable = Observable.create((ObservableOnSubscribe<PhotoFaceFeature>) emitter -> {
            PhotoFaceFeature photoFaceFeature = FaceManager.getInstance().getCardFaceFeatureByBitmapPosting(idCardRecord.getCardBitmap());
            if (photoFaceFeature.getFaceFeature() != null && photoFaceFeature.getMaskFaceFeature() != null) {
                emitter.onNext(photoFaceFeature);
            } else {
                throw new MyException(photoFaceFeature.getMessage());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(photoFaceFeature -> {
                    cardFeature = photoFaceFeature;
                    FaceManager.getInstance().setFeatureListener(faceListener);
                    FaceManager.getInstance().setNeedNextFeature(true);
                    FaceManager.getInstance().setOrientation(CameraManager.getInstance().getPreviewOrientation());
                    FaceManager.getInstance().startLoop();
                    hint.set("请将镜头朝向寄件人");
                }, throwable -> {
                    if (throwable instanceof MyException) {
                        hint.set(throwable.getMessage());
                    } else {
                        hint.set("出现错误");
                    }
                });
    }

    public void stopFaceVerify() {
        FaceManager.getInstance().stopLoop();
        FaceManager.getInstance().setFeatureListener(null);
    }

    private FaceManager.OnFeatureExtractListener faceListener = (mxRGBImage, mxFaceInfoEx, feature, mask) -> {
        if (cardFeature != null) {
            try {
                float score;
                if (mask) {
                    score = FaceManager.getInstance().matchMaskFeature(feature, cardFeature.getMaskFaceFeature());
                } else {
                    score = FaceManager.getInstance().matchFeature(feature, cardFeature.getFaceFeature());
                }
                Config config = ConfigManager.getInstance().getConfig();
                if (mask ? score >= config.getVerifyMaskScore() : score >= config.getVerifyScore()) {
                    byte[] fileImage = FaceManager.getInstance().imageEncode(mxRGBImage.getRgbImage(), mxRGBImage.getWidth(), mxRGBImage.getHeight());
                    Bitmap header = BitmapFactory.decodeByteArray(fileImage, 0, fileImage.length);
                    hint.set("人证核验成功");
                    TTSManager.getInstance().playVoiceMessageFlush("核验通过");
                    stopFaceVerify();
                    IDCardRecord value = idCardRecordLiveData.getValue();
                    if (value != null) {
                        value.setFaceBitmap(header);
                        value.setVerifyTime(new Date());
                        verifyFlag.postValue(value);
                    } else {
                        toast.postValue(ToastManager.getToastBody("遇到错误，请退出后重试", ToastManager.ERROR));
                    }
                    return;
                } else {
                    hint.set("识别不通过");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            FaceManager.getInstance().setNeedNextFeature(true);
        }
    };

}
