package com.miaxis.postal.manager;

import android.app.Application;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.repository.DeviceRepository;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class AmapManager implements AMapLocationListener {

    private AmapManager() {}

    public static AmapManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final AmapManager instance = new AmapManager();
    }

    /** ================================ 静态内部类单例写法 ================================ **/

    private Application application;
    private AMapLocationClient aMapLocationClient;
    private AMapLocation aMapLocation;

    public void getOneLocation(OnOneLocationListener listener) {
        AMapLocationClient aMapLocationClient = new AMapLocationClient(application);
        aMapLocationClient.setLocationListener(aMapLocation -> {
            if (aMapLocation.getErrorCode() == 0) {
                String address = aMapLocation.getAddress();
                aMapLocationClient.stopLocation();
                listener.onOneLocation(address);
                this.aMapLocation = aMapLocation;
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

    /**
     * 开始定位
     */
    public void startLocation(Application application) {
        this.application = application;
        aMapLocationClient = new AMapLocationClient(application);
        aMapLocationClient.setLocationListener(this);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setInterval(1000 * 60 * 10);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        aMapLocationClient.setLocationOption(mLocationOption);
        aMapLocationClient.startLocation();
    }

    /**
     * 定位回调，回调后查询天气信息
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            this.aMapLocation = aMapLocation;
            String deviceIMEI = ConfigManager.getInstance().getConfig().getDeviceIMEI();
            try {
                DeviceRepository.getInstance().deviceHeartBeat(deviceIMEI, aMapLocation.getLatitude(), aMapLocation.getLongitude());
            } catch (IOException | MyException e) {
                e.printStackTrace();
            }
        }
    }

    public AMapLocation getaMapLocation() {
        return aMapLocation;
    }

}
