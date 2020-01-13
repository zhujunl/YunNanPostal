package com.miaxis.postal.viewModel;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.data.entity.Order;

public class OrderViewModel extends BaseViewModel {

    public MutableLiveData<Order> order = new MutableLiveData<>();

    public OrderViewModel() {
    }

}
