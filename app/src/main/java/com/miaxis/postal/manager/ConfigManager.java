package com.miaxis.postal.manager;

import android.app.Application;

import androidx.annotation.NonNull;

import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.model.ConfigModel;
import com.miaxis.postal.util.DeviceUtil;
import com.miaxis.postal.util.ValueUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ConfigManager {

    private ConfigManager() {
    }

    public static ConfigManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final ConfigManager instance = new ConfigManager();
    }

    /** ================================ 静态内部类单例写法 ================================ **/

    private Config config;

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void checkConfig() {
        config = ConfigModel.loadConfig();
        if (config == null) {
            config = Config.ConfigBuilder.aConfig()
                    .id(1L)
                    .host(ValueUtil.DEFAULT_BASE_HOST)
                    .mac(DeviceUtil.getMacFromHardware())
                    .loginMode(ValueUtil.DEFAULT_LOGIN_MODE)
                    .verifyScore(ValueUtil.DEFAULT_VERIFY_SCORE)
                    .qualityScore(ValueUtil.DEFAULT_QUALITY_SCORE)
                    .registerQualityScore(ValueUtil.DEFAULT_REGISTER_QUALITY_SCORE)
                    .deviceId(ValueUtil.DEFAULT_DEVICE_ID)
                    .deviceStatus(ValueUtil.DEVICE_UNABLE)
                    .heartBeatInterval(ValueUtil.DEFAULT_HEART_BEAT_INTERVAL)
                    .build();
            ConfigModel.saveConfig(config);
        }
    }

    public void saveConfigSync(@NonNull Config config) {
        ConfigModel.saveConfig(config);
        this.config = config;
    }

    public void saveConfig(@NonNull Config config, @NonNull OnConfigSaveListener listener) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            ConfigModel.saveConfig(config);
            this.config = config;
            emitter.onNext(Boolean.TRUE);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> listener.onConfigSave(true, "保存成功")
                        , throwable -> listener.onConfigSave(false, "保存失败，" + throwable.getMessage()));
    }

    public interface OnConfigSaveListener {
        void onConfigSave(boolean result, String message);
    }

}
