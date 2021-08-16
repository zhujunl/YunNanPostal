package com.miaxis.postal.app;

import android.app.Application;

import com.liulishuo.filedownloader.FileDownloader;
import com.miaxis.postal.BuildConfig;
import com.miaxis.postal.MyEventBusIndex;
import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.CrashExceptionManager;
import com.miaxis.postal.manager.FaceManager;
import com.miaxis.postal.manager.TTSManager;
import com.miaxis.postal.util.DateUtil;
import com.miaxis.postal.util.FileUtil;
import com.miaxis.postal.util.SPUtils;
import com.miaxis.postal.util.carch.CrashHandler;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

public class App extends Application {

    private ExecutorService threadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    public boolean uploadEnable = false;//是否启动后台上传订单号
    private static App instance;
    private final Random GlobalRandom = new Random();
    public final String BarHeader = "TP";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (!BuildConfig.IS_DEBUG) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(this);
        }
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
    }

    public String getRandomBarCode() {
        long timeMillis = System.currentTimeMillis();
        String temp = ConfigManager.getInstance().getConfig().getDeviceIMEI() + "" + timeMillis + "" + App.getInstance().GlobalRandom.nextLong();
        String format = DateUtil.DATEFORMAT.format(new Date());
        return this.BarHeader + format + Math.abs(temp.hashCode());
    }

    public static App getInstance() {
        return instance;
    }

    public void initApplication(@NonNull OnAppInitListener listener) {
        try {
            FileUtil.initDirectory();
            AppDatabase.initDB(this);
            SPUtils.getInstance().init(this);
            ConfigManager.getInstance().checkConfig();
            if (!BuildConfig.IS_DEBUG) {
                CrashExceptionManager.getInstance().init(this);
            }
            PostalApi.rebuildRetrofit();
            FileDownloader.setup(this);
            TTSManager.getInstance().init(getApplicationContext());
            //FileUtil.LICENCE_PATH 有可能带路径的报错
            int result = FaceManager.getInstance().initFaceST(getApplicationContext(), FileUtil.LICENCE_PATH);
            if (result != FaceManager.INIT_SUCCESS) {
                result = FaceManager.getInstance().initFaceST(getApplicationContext(), "");
            }
            listener.onInit(result == FaceManager.INIT_SUCCESS, FaceManager.getFaceInitResultDetail(result));
            //listener.onInit(true, "");
        } catch (Exception e) {
            e.printStackTrace();
            listener.onInit(false, e.getMessage());
        }
    }

    public ExecutorService getThreadExecutor() {
        return threadExecutor;
    }

    public interface OnAppInitListener {
        void onInit(boolean result, String message);
    }

}
