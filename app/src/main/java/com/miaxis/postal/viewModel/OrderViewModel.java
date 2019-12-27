package com.miaxis.postal.viewModel;

import androidx.databinding.ObservableField;

import com.miaxis.postal.data.entity.Order;

public class OrderViewModel extends BaseViewModel {

    public ObservableField<Order> order = new ObservableField<>();

    public OrderViewModel() {
    }

}
