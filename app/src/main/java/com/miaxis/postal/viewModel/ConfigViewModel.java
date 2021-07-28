package com.miaxis.postal.viewModel;


import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.ToastManager;

import androidx.lifecycle.MutableLiveData;

public class ConfigViewModel extends BaseViewModel {

    public MutableLiveData<Config> config = new MutableLiveData<>();

    public MutableLiveData<Boolean> isExist = new MutableLiveData<>();

    public String host;

    public ConfigViewModel() {

    }

    public void saveConfig(Config config) {
        ConfigManager.getInstance().saveConfig(config, (result, message) -> {
            if (result) {
                PostalApi.rebuildRetrofit();
                isExist.postValue(true);
                toast.setValue(ToastManager.getToastBody(message, ToastManager.SUCCESS));
            } else {
                isExist.postValue(false);
                toast.setValue(ToastManager.getToastBody(message, ToastManager.ERROR));
            }
        });
    }
}
