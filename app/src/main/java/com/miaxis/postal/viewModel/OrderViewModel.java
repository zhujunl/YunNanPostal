package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

import com.miaxis.postal.app.PostalApp;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.data.event.TempIdEvent;
import com.miaxis.postal.data.event.VerifyEvent;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.repository.PostalRepository;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.ScanManager;
import com.miaxis.postal.util.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class OrderViewModel extends BaseViewModel implements LifecycleObserver {

    public MutableLiveData<List<Order>> orderList = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Order> newOrder = new SingleLiveEvent<>();

    public boolean verifyResult = false;

    private TempIdDto tempIdDto;

    public OrderViewModel() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onVerifyEvent(VerifyEvent event) {
        verifyResult = true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onTempIdEvent(TempIdEvent event) {
        this.tempIdDto = event.getTempIdDto();
    }

    public void startScan() {
        CameraManager.getInstance().closeCamera();
        ScanManager.getInstance().initDevice(PostalApp.getInstance(), listener);
        ScanManager.getInstance().startScan();
    }

    public void stopScan() {
        try {
            ScanManager.getInstance().stopScan();
            ScanManager.getInstance().closeDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ScanManager.OnScanListener listener = code -> {
        stopScan();
        Order order = new Order();
        order.setBarCode(code);
        newOrder.setValue(order);
    };

    public void addOrder(Order order, Bitmap bitmap) {
        Observable.just(bitmap)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(bitmap1 -> {
                    String path = FileUtil.FACE_IMAGE_PATH + File.separator;
                    String fileName = order.getBarCode() + System.currentTimeMillis() + ".jpg";
                    FileUtil.saveBitmap(bitmap1, path, fileName);
                    return path + fileName;
                })
                .map(s -> {
                    List<String> imageList = new ArrayList<>();
                    imageList.add(s);
                    order.setPhotoList(imageList);
                    uploadOrder(order);
                    return order;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mOrder -> {
                    List<Order> orderListCache = getOrderList();
                    orderListCache.add(mOrder);
                    orderList.setValue(orderListCache);
                }, Throwable::printStackTrace);
    }

    private void uploadOrder(Order order) {
        if (tempIdDto != null) {
            Observable.just(order)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(mOrder -> {
                        PostalRepository.getInstance().saveOrderFromAppSync(mOrder, tempIdDto, "", "");
                    }, Throwable::printStackTrace);
        }
    }

    public List<Order> getOrderList() {
        List<Order> value = orderList.getValue();
        if (value == null) {
            List<Order> newArrayList = new ArrayList<>();
            orderList.setValue(newArrayList);
            return newArrayList;
        } else {
            return value;
        }
    }

}
