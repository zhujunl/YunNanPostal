package com.miaxis.postal.viewModel;

import androidx.databinding.ObservableField;

import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.ToastManager;

public class ConfigViewModel extends BaseViewModel {

    public ObservableField<Config> config = new ObservableField<>(ConfigManager.getInstance().getConfig());

    public ConfigViewModel() {
    }

    public void saveConfig(Config config) {
        ConfigManager.getInstance().saveConfig(config, (result, message) -> {
            if (result) {
                PostalApi.rebuildRetrofit();
                toast.setValue(ToastManager.getToastBody(message, ToastManager.SUCCESS));
            } else {
                toast.setValue(ToastManager.getToastBody(message, ToastManager.ERROR));
            }
        });
    }

}
