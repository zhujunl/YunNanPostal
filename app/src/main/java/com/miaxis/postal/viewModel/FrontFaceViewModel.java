package com.miaxis.postal.viewModel;

import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.data.entity.Courier;

public class FrontFaceViewModel extends BaseViewModel {

    public MutableLiveData<Courier> courierLiveData = new MutableLiveData<>();

    public FrontFaceViewModel() {
    }



}
