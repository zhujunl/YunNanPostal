package com.miaxis.postal.manager;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.repository.DeviceRepository;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AmapManager implements AMapLocationListener {

    private AmapManager() {
    }

    public static AmapManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final AmapManager instance = new AmapManager();
    }

    /**
     * ================================ 静态内部类单例写法 ================================
     **/

    private Application application;
    private AMapLocationClient aMapLocationClient;
    private AMapLocation aMapLocation;

    public void getOneLocation(OnOneLocationListener listener) {
        AMapLocationClient aMapLocationClient = new AMapLocationClient(application);
        aMapLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation location) {
                if (location.getErrorCode() == 0) {
                    String address = location.getAddress();
                    aMapLocationClient.stopLocation();
                    listener.onOneLocation(address);
                    // aMapLocation = location;
                    //heatBeat(location);
                    aMapLocationClient.stopLocation();
                    aMapLocationClient.unRegisterLocationListener(this);
                }
            }
        });
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setOnceLocation(true);
        option.setNeedAddress(true);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocationLatest(true);
        aMapLocationClient.setLocationOption(option);
        aMapLocationClient.startLocation();
    }

    public interface OnOneLocationListener {
        void onOneLocation(String address);
    }

    private long getPositionTime = 1000 * (60 * 5 - 20);
    //    private long getPositionTime = 1000 * (20);
    private Timer timer;

    /**
     * 开始定位
     */
    public void startLocation(Application application) {
        this.application = application;
        stopLocation();
        aMapLocationClient = new AMapLocationClient(application);
        aMapLocationClient.setLocationListener(this);
        AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
        aMapLocationClientOption.setInterval(1000 * 60);
        aMapLocationClientOption.setHttpTimeOut(1000 * 10);
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        aMapLocationClient.setLocationOption(aMapLocationClientOption);
        aMapLocationClient.startLocation();
        Log.e("deviceHeart", "start");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("deviceHeart", "---------------");
                if (aMapLocationClient != null) {
                    AMapLocation lastKnownLocation = aMapLocationClient.getLastKnownLocation();
                    if (lastKnownLocation != null && lastKnownLocation.getErrorCode() == 0) {
                        aMapLocation = lastKnownLocation;
                        heatBeat(lastKnownLocation);
                    }
                }
            }
        }, 100, getPositionTime);
    }

    public void stopLocation() {
        try {
            if (aMapLocationClient != null) {
                aMapLocationClient.stopLocation();
                aMapLocationClient.unRegisterLocationListener(this);
            }
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定位回调，回调后查询天气信息
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
        //            this.aMapLocation = aMapLocation;
        //            heatBeat(aMapLocation);
        //        }
    }

    private void heatBeat(AMapLocation aMapLocation) {
        App.getInstance().getThreadExecutor().execute(() -> {
            Config config = ConfigManager.getInstance().getConfig();
            try {
                if (config!=null&& !TextUtils.isEmpty(config.getDeviceIMEI())) {
                    DeviceRepository.getInstance().deviceHeartBeat(config.getDeviceIMEI(), aMapLocation.getLatitude(), aMapLocation.getLongitude());
                }
            } catch (IOException | MyException | NetResultFailedException e ) {
                e.printStackTrace();
            }
        });
    }

    public AMapLocation getaMapLocation() {
        return aMapLocation;
    }

}
