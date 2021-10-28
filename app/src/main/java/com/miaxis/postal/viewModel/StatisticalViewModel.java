package com.miaxis.postal.viewModel;

import com.miaxis.postal.app.App;
import com.miaxis.postal.data.bean.Statistical;
import com.miaxis.postal.data.repository.PostalRepository;
import com.miaxis.postal.view.adapter.StatisticalAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StatisticalViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> emptyFlag = new MutableLiveData<>();
    private AtomicInteger pageNum = new AtomicInteger();
    /**
     * 列表数据，list不为空
     */
    public MutableLiveData<List<StatisticalAdapter.StatisticalItem>> itemList = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<Boolean> nextPageEnable = new MutableLiveData<>();

    public StatisticalViewModel() {
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void refresh() {
        pageNum.set(0);
        itemList.setValue(new ArrayList<>());
        getList();
    }

    public void getList() {
        waitMessage.setValue("请稍后");
        Disposable subscribe = Observable.create((ObservableOnSubscribe<List<StatisticalAdapter.StatisticalItem>>) emitter -> {
            List<Statistical> statisticsByDate = PostalRepository.getInstance().getStatisticsByDate(pageNum.incrementAndGet(), 10);
            nextPageEnable.postValue(statisticsByDate != null && !statisticsByDate.isEmpty());
            List<StatisticalAdapter.StatisticalItem> value = itemList.getValue();
            if (value == null) {
                value = new ArrayList<>();
            }
            for (Statistical statistical : statisticsByDate) {
                process(value, statistical);
            }
            emitter.onNext(value);
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    itemList.setValue(list);
                    if (pageNum.get() == 1 && list == null || list.isEmpty()) {
                        emptyFlag.setValue(true);
                    }
                    waitMessage.setValue("");
                }, throwable -> {
                    pageNum.decrementAndGet();
                    waitMessage.setValue("");
                    resultMessage.setValue(handleError(throwable));
                });
    }

    private void process(List<StatisticalAdapter.StatisticalItem> value, Statistical statistical) {
        for (StatisticalAdapter.StatisticalItem statisticalItem : value) {
            if (Objects.equals(statisticalItem.date, statistical.date)) {
                statisticalItem.allCounts += statistical.sendNumber;
                statisticalItem.checkCounts += statistical.checkNumber;
                statisticalItem.noCheckCounts += statistical.noCheckNumber;
                if (statisticalItem.list == null) {
                    statisticalItem.list = new ArrayList<>();
                }
                statisticalItem.list.add(statistical);
                return;
            }
        }
        List<Statistical> objects = new ArrayList<>();
        objects.add(statistical);
        StatisticalAdapter.StatisticalItem statisticalItem =
                new StatisticalAdapter.StatisticalItem(
                        statistical.date, statistical.sendNumber, statistical.checkNumber, statistical.noCheckNumber, objects);
        value.add(statisticalItem);
    }

}
