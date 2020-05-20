package com.miaxis.postal.viewModel;

import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.data.entity.SimpleOrder;
import com.miaxis.postal.data.repository.OrderRepository;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.ValueUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LogisticsViewModel extends BaseViewModel {

    public MutableLiveData<List<SimpleOrder>> orderList = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Order> orderDetail = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> refreshing = new SingleLiveEvent<>();

    public LogisticsViewModel() {
        getOrderByCodeAndName("", 1);
    }

    public List<SimpleOrder> getOrderList() {
        List<SimpleOrder> value = orderList.getValue();
        if (value == null) {
            List<SimpleOrder> newArrayList = new ArrayList<>();
            orderList.setValue(newArrayList);
            return newArrayList;
        } else {
            return value;
        }
    }

    public void getOrderByCodeAndName(String filter, int pageNum) {
        Disposable disposable = Observable.create((ObservableOnSubscribe<List<SimpleOrder>>) emitter -> {
            List<SimpleOrder> simpleOrderList = OrderRepository.getInstance().getOrderByCodeAndNameSync(filter, pageNum, ValueUtil.PAGE_SIZE);
            emitter.onNext(simpleOrderList);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(simpleOrderList -> {
                    refreshing.setValue(Boolean.FALSE);
                    if (pageNum == 1) {
                        orderList.setValue(simpleOrderList);
                    } else {
                        List<SimpleOrder> LocalOrderList = getOrderList();
                        LocalOrderList.addAll(simpleOrderList);
                        orderList.setValue(LocalOrderList);
                    }
                    if (simpleOrderList.isEmpty()) {
                        toast.setValue(ToastManager.getToastBody("没有更多了", ToastManager.SUCCESS));
                    }
                }, throwable -> {
                    refreshing.setValue(Boolean.FALSE);
                    resultMessage.setValue(handleError(throwable));
                });
    }

    public void getOrderById(SimpleOrder simpleOrder) {
        waitMessage.setValue("查询中，请稍后...");
        Disposable disposable = Observable.create((ObservableOnSubscribe<Order>) emitter -> {
            Order order = OrderRepository.getInstance().getOrderByIdSync(simpleOrder.getId());
            emitter.onNext(order);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(order -> {
                    waitMessage.setValue("");
                    orderDetail.setValue(order);
                }, throwable -> {
                    waitMessage.setValue("");
                    resultMessage.setValue(handleError(throwable));
                });
    }

}
