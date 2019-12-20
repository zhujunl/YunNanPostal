package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.PostalApp;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.entity.MxRGBImage;
import com.miaxis.postal.data.event.FeatureEvent;
import com.miaxis.postal.data.event.TempIdEvent;
import com.miaxis.postal.data.event.VerifyEvent;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.repository.PostalRepository;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.CardManager;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.FaceManager;
import com.miaxis.postal.manager.TTSManager;
import com.speedata.libid2.IDInfor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class IdentityViewModel extends BaseViewModel {

    private ExecutorService threadPool = Executors.newFixedThreadPool(1);

    public MutableLiveData<Status> initCardResult = new SingleLiveEvent<>();
    public MutableLiveData<IDInfor> idInfoLiveData = new MutableLiveData<>();
    public MutableLiveData<Status> verifyResult = new MutableLiveData<>();
    public MutableLiveData<Bitmap> verifyFace = new MutableLiveData<>();

    private byte[] cardFeature;

    private TempIdDto tempIdDto;

    public IdentityViewModel() {
        EventBus.getDefault().register(this);
        startReadCard();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        EventBus.getDefault().unregister(this);
    }

    public void startReadCard() {
        initCardResult.setValue(Status.LOADING);
        CardManager.getInstance().init(PostalApp.getInstance(), listener);
    }

    private CardManager.IDCardListener listener = new CardManager.IDCardListener() {
        @Override
        public void onIDCardInitResult(boolean result) {
            initCardResult.postValue(result ? Status.SUCCESS : Status.FAILED);
        }

        @Override
        public void onIDCardReceive(IDInfor idInfor) {
            verifyResult.setValue(Status.LOADING);
            idInfoLiveData.setValue(idInfor);
            cardFeature = null;
            threadPool.execute(() -> FaceManager.getInstance().getFeatureByBitmap(idInfor.getBmps(), "card"));
        }
    };

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onFeatureEvent(FeatureEvent event) {
        switch (event.getMode()) {
            case FeatureEvent.IMAGE_FACE:
                onImageFace(event);
                break;
            case FeatureEvent.CAMERA_FACE:
                onCameraFace(event);
                break;
        }
    }

    private void onImageFace(FeatureEvent event) {
        if (event.getFeature() != null) {
            cardFeature = event.getFeature();
            FaceManager.getInstance().setNeedNextFeature(true);
            FaceManager.getInstance().startLoop();
        } else {
            TTSManager.getInstance().playVoiceMessageFlush("请拿开身份证重试");
        }
    }

    private void onCameraFace(FeatureEvent event) {
        threadPool.execute(() -> {
            if (cardFeature != null) {
                float score = FaceManager.getInstance().matchFeature(cardFeature, event.getFeature());
                if (score >= ConfigManager.getInstance().getConfig().getVerifyScore()) {
                    try {
                        MxRGBImage mxRgbImage = event.getMxRgbImage();
                        byte[] fileImage = FaceManager.getInstance().imageEncode(mxRgbImage.getRgbImage(), mxRgbImage.getWidth(), mxRgbImage.getHeight());
                        Bitmap bitmap = BitmapFactory.decodeByteArray(fileImage, 0, fileImage.length);
                        Bitmap tailoringBitmap = FaceManager.getInstance().tailoringFace(bitmap, 102, 126, event.getMxFaceInfoEx());
                        verifyFace.postValue(tailoringBitmap);
                        verifyResult.postValue(Status.SUCCESS);
                        FaceManager.getInstance().stopLoop();
                        CameraManager.getInstance().closeCamera();
                        EventBus.getDefault().post(new VerifyEvent());
                        try {
                            tempIdDto = PostalRepository.getInstance().savePersonFromAppSync(idInfoLiveData.getValue(), tailoringBitmap);
                            EventBus.getDefault().post(new TempIdEvent(tempIdDto));
                        } catch (IOException | MyException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        FaceManager.getInstance().setNeedNextFeature(true);
                    }
                } else {
                    FaceManager.getInstance().setNeedNextFeature(true);
                }
            }
        });
    }

}
