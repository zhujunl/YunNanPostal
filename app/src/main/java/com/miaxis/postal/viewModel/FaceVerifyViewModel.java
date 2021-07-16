package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.amap.api.location.AMapLocation;
import com.bumptech.glide.load.model.StringLoader;
import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.bean.PhotoFaceFeature;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.WarnLog;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.repository.ExpressRepository;
import com.miaxis.postal.data.repository.IDCardRecordRepository;
import com.miaxis.postal.data.repository.IDCardRepository;
import com.miaxis.postal.data.repository.WarnLogRepository;
import com.miaxis.postal.manager.AmapManager;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.DataCacheManager;
import com.miaxis.postal.manager.FaceManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.manager.TTSManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.FileUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FaceVerifyViewModel extends BaseViewModel {

    public MutableLiveData<IDCardRecord> idCardRecordLiveData = new MutableLiveData<>();
    public ObservableField<String> hint = new ObservableField<>("");

    public MutableLiveData<IDCardRecord> verifyFlag = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> verifyFailedFlag = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> saveFlag = new SingleLiveEvent<>();

    public ObservableField<String> countDown = new ObservableField<>();

    private PhotoFaceFeature cardFeature;

    public String readCardNum="";

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
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
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
                int verify;
                Config config = ConfigManager.getInstance().getConfig();
                if (mask ? score >= config.getVerifyMaskScore() : score >= config.getVerifyScore()) {
                    verify = 1;
                    TTSManager.getInstance().playVoiceMessageFlush("核验通过");
                    hint.set("人证核验成功");
                    verifyFailedFlag.postValue(Boolean.TRUE);
                } else {
                    verify = 2;
                    hint.set("识别不通过");
                    verifyFailedFlag.postValue(Boolean.FALSE);
                }
                stopFaceVerify();
                byte[] fileImage = FaceManager.getInstance().imageEncode(mxRGBImage.getRgbImage(), mxRGBImage.getWidth(), mxRGBImage.getHeight());
                Bitmap header = BitmapFactory.decodeByteArray(fileImage, 0, fileImage.length);
                IDCardRecord value = idCardRecordLiveData.getValue();
                if (value != null) {
                    IDCardRepository.getInstance().addNewIDCard(value);
                    value.setFaceBitmap(header);
                    value.setVerifyTime(new Date());
                    value.setChekStatus(verify);
                    verifyFlag.postValue(value);
                    PostalManager.getInstance().saveImage(header,value,readCardNum);
                    return;
                } else {
                    toast.postValue(ToastManager.getToastBody("遇到错误，请退出后重试", ToastManager.ERROR));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            FaceManager.getInstance().setNeedNextFeature(true);
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
                                .expressmanId(courier.getCourierId())
                                .expressmanName(courier.getName())
                                .expressmanPhone(courier.getPhone())
                                .createTime(new Date())
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
