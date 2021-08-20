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

    public MutableLiveData<List<Order>> OrderList = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<Integer> CurrentPage = new MutableLiveData<>(1);

    public MutableLiveData<String> ErrorMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> QueryFlag = new MutableLiveData<>();


    public MutableLiveData<Boolean> RefreshComplete = new MutableLiveData<>();
    public MutableLiveData<Boolean> LoadMoreComplete = new MutableLiveData<>();
    public MutableLiveData<Boolean> LoadMoreEnable = new MutableLiveData<>();


    public RecordSearchViewModel() {
    }

    public void getOrderById(String orderCode) {
        QueryFlag.setValue(true);
        Disposable disposable = Observable.create((ObservableOnSubscribe<Order>) emitter -> {
            Order order = OrderRepository.getInstance().getOrderByCode(orderCode);
            emitter.onNext(order);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(order -> {
                    QueryFlag.setValue(false);
                    SearchOrder.setValue(order);
                }, throwable -> {
                    QueryFlag.setValue(false);
                    ErrorMessage.setValue("查询失败，" + handleError(throwable));
                });
    }

    //根据快递员信息 订单号  时间段  查询订单列表
    public void getOrderByCode(String orderCode) {
        hint.set("正在查询中");
        Disposable disposable = Observable.create((ObservableOnSubscribe<List<Order>>) emitter -> {
            Integer page = CurrentPage.getValue();
            if (page == null) {
                page = 1;
                CurrentPage.setValue(page);
            }
            if (page == 1) {
                List<Order> value = OrderList.getValue();
                if (!ListUtils.isNull(value)) {
                    value.clear();
                }
            }
            List<Order> orders = OrderRepository.getInstance().getOrderByCode(orderCode, page);
            emitter.onNext(orders);
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(order -> {
                    LoadMoreEnable.setValue(!ListUtils.isNullOrEmpty(order));
                    Integer pageValue = CurrentPage.getValue();
                    if (pageValue == null) {
                        pageValue = 1;
                    }
                    hint.set(ListUtils.isNullOrEmpty(order) ? (pageValue == 1 ? "未查询到数据" : "") : "");
                    LoadMoreComplete.setValue(true);
                    RefreshComplete.setValue(true);
                    if (!ListUtils.isNullOrEmpty(order)) {
                        CurrentPage.setValue(pageValue + 1);
                    }
                    List<Order> value = OrderList.getValue();
                    if (value == null) {
                        value = new ArrayList<>();
                    }
                    value.addAll(order);
                    OrderList.setValue(value);
                }, throwable -> {
                    hint.set("查询失败，" + handleError(throwable));
                    OrderList.setValue(new ArrayList<>());
                    LoadMoreComplete.setValue(true);
                    RefreshComplete.setValue(true);
                });
    }
}
