package com.miaxis.postal.viewModel;

import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.data.entity.Order;

import java.util.ArrayList;
import java.util.List;

public class InspectViewModel extends BaseViewModel {

    public MutableLiveData<List<String>> photoList = new MutableLiveData<>(new ArrayList<>());

    public InspectViewModel() {
    }

    public List<String> getOrderList() {
        List<String> value = photoList.getValue();
        if (value == null) {
            List<String> newArrayList = new ArrayList<>();
            photoList.setValue(newArrayList);
            return newArrayList;
        } else {
            return value;
        }
    }

}
