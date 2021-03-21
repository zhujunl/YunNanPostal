package com.miaxis.postal.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.serialport.DeviceControlSpd;

import androidx.annotation.NonNull;

import com.miaxis.postal.app.App;
import com.miaxis.postal.manager.fingerPower.BP990_FingerPower;
import com.miaxis.postal.manager.fingerPower.BP990s_FingerPower;
import com.miaxis.postal.manager.fingerPower.IFingerPower;
import com.miaxis.postal.manager.idpower.BP990_IdCardPower;
import com.miaxis.postal.manager.idpower.BP990s_IdCardPower;
import com.mx.finger.alg.MxFingerAlg;
import com.mx.finger.api.msc.MxMscBigFingerApi;
import com.mx.finger.api.msc.MxMscBigFingerApiFactory;
import com.mx.finger.common.MxImage;
import com.mx.finger.common.Result;
import com.mx.finger.utils.RawBitmapUtils;

import org.zz.jni.zzFingerAlgID;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FingerManager {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private FingerManager() {
    }

    public static FingerManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final FingerManager instance = new FingerManager();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    private Context context;
    private MxMscBigFingerApi mxMscBigFingerApi;
    private MxFingerAlg mxFingerAlg;

    private FingerListener listener;

    private volatile boolean init = false;
    IFingerPower mFingerPower = null;

    public void init(@NonNull Context context, @NonNull FingerListener listener) {
        this.context = context;
        this.listener = listener;
        if (Objects.equals(Build.MANUFACTURER,"QUALCOMM")){
            mFingerPower = new BP990_FingerPower();
        }else {
            mFingerPower = new BP990s_FingerPower();
        }
        initDevice();
    }

    private void initDevice() {
        executor.execute(() -> {
            try {
                mFingerPower.powerOn();
                Thread.sleep(500);
                MxMscBigFingerApiFactory fingerFactory = new MxMscBigFingerApiFactory(App.getInstance());
                mxMscBigFingerApi = fingerFactory.getApi();
                mxFingerAlg = fingerFactory.getAlg();
                init = true;
                listener.onFingerInitResult(true);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            listener.onFingerInitResult(false);
        });
    }

    public void getFingerFeature() {
        if (!init) return;
        executor.execute(() -> {
            try {
                Result<MxImage> result = mxMscBigFingerApi.getFingerImageBig(5000);
                if (result.isSuccess()) {
                    MxImage image = result.data;
                    if (image != null) {
                        byte[] feature = mxFingerAlg.extractFeature(image.data, image.width, image.height);
                        if (feature != null) {
                            listener.onFingerReceive(feature, null, false);
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            listener.onFingerReceive(null, null, false);
        });
    }

    public void getFingerFeatureAndImage() {
        if (!init) return;
        executor.execute(() -> {
            try {
                Result<MxImage> result = mxMscBigFingerApi.getFingerImageBig(5000);
                if (result.isSuccess()) {
                    MxImage image = result.data;
                    if (image != null) {
                        byte[] feature = mxFingerAlg.extractFeature(image.data, image.width, image.height);
                        if (feature != null) {
                            Bitmap bitmap = RawBitmapUtils.raw2Bimap(image.data, image.width, image.height);
                            listener.onFingerReceive(feature, bitmap, true);
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            listener.onFingerReceive(null, null, false);
        });
    }

    public boolean matchFeature(byte[] feature1, byte[] feature2) {
        return mxFingerAlg.match(feature1, feature2, 3) == 0;
    }

    public byte[] mergeFeature(byte[] feature1, byte[] feature2, byte[] feature3) {
        if (matchFeature(feature1, feature2)
            && matchFeature(feature1, feature3)
            && matchFeature(feature2, feature3)) {
            zzFingerAlgID alg = new zzFingerAlgID();
            byte[] buffer = new byte[512];
            int result = alg.mxGetMB512(feature1, feature2, feature3, buffer);
            if (result > 0) {
                return buffer;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void release() {
        init = false;
        mFingerPower.powerOff();
    }

    public interface FingerListener {
        void onFingerInitResult(boolean result);
        void onFingerReceive(byte[] feature, Bitmap image, boolean hasImage);
    }

}
