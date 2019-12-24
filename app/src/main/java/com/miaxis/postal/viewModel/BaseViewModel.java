package com.miaxis.postal.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.miaxis.postal.manager.ToastManager;

public class BaseViewModel extends ViewModel {

    public MutableLiveData<String> waitMessage = new MutableLiveData<>("");
    public MutableLiveData<String> resultMessage = new MutableLiveData<>("");
    public MutableLiveData<ToastManager.ToastBody> toast = new MutableLiveData<>();

}
