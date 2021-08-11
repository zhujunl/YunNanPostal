package com.miaxis.postal.viewModel;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Customer;
import com.miaxis.postal.data.model.CustomerModel;
import com.miaxis.postal.util.ValueUtil;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CardModeSelectViewModel extends BaseViewModel {

    public MutableLiveData<List<Customer>> itemList = new SingleLiveEvent<>();

    public CardModeSelectViewModel() {
    }

    public void show(String expressMan) {
        waitMessage.setValue("查询中，请稍后...");
        Observable.create((ObservableOnSubscribe<List<Customer>>) emitter -> {
            List<Customer> customers = CustomerModel.find(ValueUtil.GlobalPhone);
            //            List<Customer> customers = new ArrayList<>();
            //            for (int i = 0; i < 10; i++) {
            //                customers.add(new Customer(expressMan, "2222" + i, "3333" + i));
            //            }
            emitter.onNext(customers);
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    waitMessage.setValue("");
                    itemList.postValue(list);
                }, throwable -> {
                    waitMessage.setValue("");
                    itemList.postValue(null);
                });
    }


}
