package com.miaxis.postal.viewModel;

import androidx.databinding.ObservableField;

import com.miaxis.postal.data.entity.Courier;

public class HomeViewModel extends BaseViewModel {

    public ObservableField<Courier> courier = new ObservableField<>();

    public HomeViewModel() {
    }
}
