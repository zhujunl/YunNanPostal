package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.event.TakePhotoEvent;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.ToastManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CameraViewModel extends BaseViewModel {

    public MutableLiveData<Status> shootFlag = new MutableLiveData<>(Status.FAILED);
    public MutableLiveData<Bitmap> thumbnail = new MutableLiveData<>();

    private Bitmap bitmapCache;
    private List<Bitmap> photoList = new ArrayList<>();

    public CameraViewModel() {
    }

    public void takePicture() {
        shootFlag.setValue(Status.LOADING);
        Camera backCamera = CameraManager.getInstance().getBackCamera();
        if (backCamera != null) {
            backCamera.takePicture(null, null, this::handlePhoto);
        } else {
            toast.setValue(ToastManager.getToastBody("摄像头未正常启动，请退出后重试", ToastManager.ERROR));
        }
    }

    public void confirmPicture() {
        shootFlag.setValue(Status.FAILED);
        Camera backCamera = CameraManager.getInstance().getBackCamera();
        if (backCamera != null) {
            backCamera.startPreview();
        } else {
            toast.setValue(ToastManager.getToastBody("摄像头未正常启动，请退出后重试", ToastManager.ERROR));
        }
        if (bitmapCache != null) {
            thumbnail.setValue(bitmapCache);
            photoList.add(bitmapCache);
        }
    }

    public void retry() {
        shootFlag.setValue(Status.FAILED);
        Camera backCamera = CameraManager.getInstance().getBackCamera();
        if (backCamera != null) {
            backCamera.startPreview();
        } else {
            toast.setValue(ToastManager.getToastBody("摄像头未正常启动，请退出后重试", ToastManager.ERROR));
        }
    }

    public void summary() {
        if (photoList.size() > 0) {
            EventBus.getDefault().postSticky(new TakePhotoEvent(photoList));
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    bitmapCache = bitmap;
                    shootFlag.setValue(Status.SUCCESS);
                }, throwable -> {
                    camera.startPreview();
                    shootFlag.setValue(Status.FAILED);
                    throwable.printStackTrace();
                    Log.e("asd", "" + throwable.getMessage());
                });
    }

}
