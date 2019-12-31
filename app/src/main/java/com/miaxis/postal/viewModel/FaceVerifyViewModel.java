package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.MxRGBImage;
import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.repository.PostalRepository;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.FaceManager;
import com.miaxis.postal.manager.ToastManager;
import com.speedata.libid2.IDInfor;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FaceVerifyViewModel extends BaseViewModel {

    public MutableLiveData<IDCardRecord> idCardRecordLiveData = new MutableLiveData<>();
    public ObservableField<String> hint = new ObservableField<>("");
    public MutableLiveData<IDCardRecord> verifyFlag = new SingleLiveEvent<>();

    private byte[] cardFeature;

    public FaceVerifyViewModel() {
    }

    public void startFaceVerify(IDCardRecord idCardRecord) {
        cardFeature = null;
        hint.set("身份证证件照处理中");
        Disposable disposable = Observable.create((ObservableOnSubscribe<byte[]>) emitter -> {
            byte[] feature = FaceManager.getInstance().getCardFeatureByBitmapPosting(idCardRecord.getCardBitmap());
            if (feature != null) {
                emitter.onNext(feature);
            } else {
                throw new MyException(FaceManager.getInstance().getErrorMessage());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(bytes -> {
                    cardFeature = bytes;
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

    private FaceManager.OnFeatureExtractListener faceListener = (mxRGBImage, mxFaceInfoEx, feature) -> {
        if (cardFeature != null) {
            try {
                float score = FaceManager.getInstance().matchFeature(feature, cardFeature);
                if (score >= ConfigManager.getInstance().getConfig().getVerifyScore()) {
                    byte[] fileImage = FaceManager.getInstance().imageEncode(mxRGBImage.getRgbImage(), mxRGBImage.getWidth(), mxRGBImage.getHeight());
                    Bitmap header = BitmapFactory.decodeByteArray(fileImage, 0, fileImage.length);
                    hint.set("人证核验成功");
                    stopFaceVerify();
                    IDCardRecord value = idCardRecordLiveData.getValue();
                    if (value != null) {
                        value.setFaceBitmap(header);
                        verifyFlag.postValue(value);
                    } else {
                        toast.postValue(ToastManager.getToastBody("遇到错误，请退出后重试", ToastManager.ERROR));
                    }
                    return;
                } else {
                    hint.set("请继续对准寄件人");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            FaceManager.getInstance().setNeedNextFeature(true);
        }
    };

//    public void uploadVerify() {
//        IDInfor idInfor = idInforLiveData.getValue();
//        if (idInfor != null && headerCache != null) {
//            waitMessage.postValue("正在上传，请稍后...");
//            Observable.create((ObservableOnSubscribe<TempId>) emitter -> {
//                TempId tempId = PostalRepository.getInstance().savePersonFromAppSync(idInfor, headerCache);
//                emitter.onNext(tempId);
//            })
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(tempId -> {
//                        waitMessage.setValue("");
//                        tempIdLiveData.setValue(tempId);
//                    }, throwable -> {
//                        waitMessage.setValue("");
//                        resultMessage.setValue(hanleError(throwable));
//                        hint.set("核验结果上传失败");
//                    });
//        }
//    }

}
