package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.event.FaceRegisterEvent;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.FaceManager;
import com.miaxis.postal.manager.ToastManager;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class FaceRegisterViewModel extends BaseViewModel {

    public MutableLiveData<Status> shootFlag = new MutableLiveData<>(Status.FAILED);
    public ObservableField<String> hint = new ObservableField<>("请自拍一张大头照");
    public MutableLiveData<Boolean> confirmFlag = new SingleLiveEvent<>();

    private String featureCache;
    private Bitmap headerCache;

    public FaceRegisterViewModel() {
    }

    public void takePicture() {
        shootFlag.setValue(Status.LOADING);
        hint.set("处理中");
        Camera frontCamera = CameraManager.getInstance().getFrontCamera();
        if (frontCamera != null) {
            frontCamera.takePicture(null, null, this::handlePhoto);
        } else {
            toast.setValue(ToastManager.getToastBody("摄像头未正常启动，请退出后重试", ToastManager.ERROR));
        }
    }

    public void retry() {
        featureCache = null;
        headerCache = null;
        shootFlag.setValue(Status.FAILED);
        hint.set("请自拍一张大头照");
        Camera frontCamera = CameraManager.getInstance().getFrontCamera();
        if (frontCamera != null) {
            frontCamera.startPreview();
        } else {
            toast.setValue(ToastManager.getToastBody("摄像头未正常启动，请退出后重试", ToastManager.ERROR));
        }
    }

    public void confirm() {
        if (!TextUtils.isEmpty(featureCache) && headerCache != null) {
            EventBus.getDefault().postSticky(new FaceRegisterEvent(featureCache, headerCache));
            confirmFlag.setValue(Boolean.TRUE);
        }
    }

    private void handlePhoto(byte[] data, Camera camera) {
        Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(CameraManager.getInstance().getPictureOrientation());
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            emitter.onNext(bitmap);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(bitmap -> {
                    byte[] feature = FaceManager.getInstance().getPhotoFeatureByBitmapPosting(bitmap);
                    if (feature != null) {
                        headerCache = bitmap;
                        return Base64.encodeToString(feature, Base64.NO_WRAP);
                    }
                    throw new MyException(FaceManager.getInstance().getErrorMessage());
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    featureCache = s;
                    shootFlag.setValue(Status.SUCCESS);
                    hint.set("人脸特征提取成功");
                }, throwable -> {
                    shootFlag.setValue(Status.FAILED);
                    camera.startPreview();
                    if (throwable instanceof MyException) {
                        hint.set(throwable.getMessage() + "，请重新拍摄");
                    } else {
                        throwable.printStackTrace();
                        Log.e("asd", "" + throwable.getMessage());
                        hint.set("出现错误，请重新拍摄");
                    }
                });
    }

}
