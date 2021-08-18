package com.miaxis.postal.viewModel;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.data.repository.OrderRepository;
import com.miaxis.postal.util.ListUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecordSearchViewModel extends BaseViewModel {

    public ObservableField<String> hint = new ObservableField<>("");

    public MutableLiveData<Order> SearchOrder = new SingleLiveEvent<>();

    public MutableLiveData<List<Order>> OrderList = new MutableLiveData<>();


    public RecordSearchViewModel() {
    }

    //    public void getOrderById(String orderCode) {
    //        hint.set("正在查询中");
    //        Disposable disposable = Observable.create((ObservableOnSubscribe<Order>) emitter -> {
    //            Order order = OrderRepository.getInstance().getOrderByCode(orderCode);
    //            emitter.onNext(order);
    //        })
    //                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
    //                .observeOn(AndroidSchedulers.mainThread())
    //                .subscribe(order -> {
    //                    hint.set("");
    //                    SearchOrder.setValue(order);
    //                }, throwable -> {
    //                    hint.set("查询失败，" + handleError(throwable));
    //                });
    //    }

    //根据快递员信息 订单号  时间段  查询订单列表
    public void getOrderById(String orderCode) {
        hint.set("正在查询中");
        Disposable disposable = Observable.create((ObservableOnSubscribe<List<Order>>) emitter -> {
            List<Order> orders = OrderRepository.getInstance().getOrderByCode(orderCode);
            emitter.onNext(orders);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(order -> {
                    hint.set(ListUtils.isNullOrEmpty(order) ? "未查询到数据" : "");
                    OrderList.setValue(order);
                }, throwable -> {
                    hint.set("查询失败，" + handleError(throwable));
                    OrderList.setValue(new ArrayList<>());
                });
    }
}
