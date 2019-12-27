package com.miaxis.postal.manager;

import android.app.Application;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.repository.DeviceRepository;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class AmapManager implements AMapLocationListener, WeatherSearch.OnWeatherSearchListener {

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
    private LocalWeatherLive weatherLive;

    public void getOneLocation(AMapLocationListener listener) {
        AMapLocationClient aMapLocationClient = new AMapLocationClient(application);
        aMapLocationClient.setLocationListener(listener);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setOnceLocation(true);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocationLatest(true);
        option.setHttpTimeOut(8000);
        aMapLocationClient.setLocationOption(option);
        aMapLocationClient.startLocation();
    }

    /**
     * 开始定位
     */
    public void startLocation(Application application) {
        this.application = application;
        aMapLocationClient = new AMapLocationClient(application);
        aMapLocationClient.setLocationListener(this);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setInterval(1000 * 60 * 5);
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
            String mac = ConfigManager.getInstance().getConfig().getMac();
            try {
                DeviceRepository.getInstance().deviceHeartBeat(mac, aMapLocation.getLatitude(), aMapLocation.getLongitude());
            } catch (IOException | MyException e) {
                e.printStackTrace();
            }
//            queryWeather(aMapLocation.getCity());
        }
    }

    /**
     * 天气查询回调
     * @param localWeatherLiveResult
     * @param rCode
     */
    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int rCode ) {
        if (rCode  == 1000) {
            if (localWeatherLiveResult != null && localWeatherLiveResult.getLiveResult() != null) {
                this.weatherLive = localWeatherLiveResult.getLiveResult();
                EventBus.getDefault().post(this.weatherLive);
            }
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {
        // 忽略天气预报信息
    }

    private void queryWeather(String city) {
        WeatherSearchQuery mQuery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        WeatherSearch mWeatherSearch = new WeatherSearch(application);
        mWeatherSearch.setOnWeatherSearchListener(this);
        mWeatherSearch.setQuery(mQuery);
        mWeatherSearch.searchWeatherAsyn(); //异步搜索
    }

    public AMapLocation getaMapLocation() {
        return aMapLocation;
    }

    public LocalWeatherLive getWeatherLive() {
        return weatherLive;
    }
}
