package com.miaxis.postal.viewModel;

import androidx.databinding.ObservableField;

import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.manager.DataCacheManager;

public class HomeViewModel extends BaseViewModel {

    public ObservableField<Courier> courier = new ObservableField<>(DataCacheManager.getInstance().getCourier());

    public HomeViewModel() {
    }
}
