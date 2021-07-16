package com.miaxis.postal.viewModel;

import android.os.SystemClock;
import android.text.TextUtils;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.model.CourierModel;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.repository.LoginRepository;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.ValueUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ConfigViewModel extends BaseViewModel {

    public MutableLiveData<Config> config = new MutableLiveData<>(ConfigManager.getInstance().getConfig());

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
