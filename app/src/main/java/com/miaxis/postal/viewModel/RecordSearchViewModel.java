package com.miaxis.postal.viewModel;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.data.repository.OrderRepository;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecordSearchViewModel extends BaseViewModel {

    public ObservableField<String> hint = new ObservableField<>("");

    public MutableLiveData<Order> searchOrder = new SingleLiveEvent<>();

    public RecordSearchViewModel() {
    }

    public void getOrderById(String orderCode) {
        hint.set("正在查询中");
        Disposable disposable = Observable.create((ObservableOnSubscribe<Order>) emitter -> {
            Order order = OrderRepository.getInstance().getOrderByCode(orderCode);
            emitter.onNext(order);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(order -> {
                    hint.set("");
                    searchOrder.setValue(order);
                }, throwable -> {
                    hint.set("查询失败，" + handleError(throwable));
                });
    }

}
