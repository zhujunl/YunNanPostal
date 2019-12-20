package com.miaxis.postal.app;

import android.app.Application;

import androidx.annotation.NonNull;

import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.CrashExceptionManager;
import com.miaxis.postal.manager.FaceManager;
import com.miaxis.postal.util.FileUtil;

import org.greenrobot.eventbus.EventBus;

public class PostalApp extends Application {

    private static PostalApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
    }

    public static PostalApp getInstance() {
        return instance;
    }

    public void initApplication(@NonNull OnAppInitListener listener) {
        try {
            FileUtil.initDirectory();
            AppDatabase.initDB(this);
            ConfigManager.getInstance().checkConfig();
            CrashExceptionManager.getInstance().init(this);
            int result = FaceManager.getInstance().initFaceST(getApplicationContext(), FileUtil.MODEL_PATH, FileUtil.LICENCE_PATH);
            listener.onInit(result == FaceManager.INIT_SUCCESS, FaceManager.getFaceInitResultDetail(result));
//            listener.onInit(true, "");
        } catch (Exception e) {
            e.printStackTrace();
            listener.onInit(false, e.getMessage());
        }
    }

    public interface OnAppInitListener {
        void onInit(boolean result, String message);
    }

}
