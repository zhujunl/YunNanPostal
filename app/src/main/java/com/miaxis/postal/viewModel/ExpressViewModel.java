package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.miaxis.postal.app.PostalApp;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.data.repository.PostalRepository;
import com.miaxis.postal.manager.AmapManager;
import com.miaxis.postal.manager.ScanManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.FileUtil;
import com.speedata.libid2.IDInfor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ExpressViewModel extends BaseViewModel {

    public ObservableField<IDInfor> idInfor = new ObservableField<>();
    public ObservableField<Bitmap> header = new ObservableField<>();
    public ObservableField<TempIdDto> tempIdDto = new ObservableField<>();
    public ObservableField<String> phone = new ObservableField<>();
    public ObservableField<String> address = new ObservableField<>();

    public MutableLiveData<List<Order>> orderList = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Order> newOrder = new SingleLiveEvent<>();
    public MutableLiveData<Order> repeatOrder = new SingleLiveEvent<>();

    public ExpressViewModel() {
    }

    public void startScan() {
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

    private ScanManager.OnScanListener listener = code -> {
        stopScan();
        Order order = new Order();
        order.setBarCode(code);
        order.setStatus(Status.LOADING);
        if (!checkRepeat(order)) {
            newOrder.setValue(order);
        } else {
            repeatOrder.setValue(order);
        }
    };

    private boolean checkRepeat(Order repeat) {
        List<Order> orderList = getOrderList();
        for (Order order : orderList) {
            if (TextUtils.equals(repeat.getBarCode(), order.getBarCode())) {
                return true;
            }
        }
        return false;
    }

    public void addOrder(Order order, Bitmap bitmap) {
        Observable.just(bitmap)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(mBitmap -> {
                    String path = FileUtil.FACE_IMAGE_PATH + File.separator;
                    String fileName = order.getBarCode() + System.currentTimeMillis() + ".jpg";
                    FileUtil.saveBitmap(mBitmap, path, fileName);
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
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            PostalRepository.getInstance().saveOrderFromAppSync(order, tempIdDto.get(), address.get(), phone.get());
            emitter.onNext(Boolean.TRUE);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    updateOrderStatus(order, Status.SUCCESS);
                }, throwable -> {
                    updateOrderStatus(order, Status.FAILED);
                    toast.setValue(ToastManager.getToastBody(hanleError(throwable), ToastManager.INFO));
                });
    }

    private void updateOrderStatus(Order update, Status status) {
        List<Order> mOrderList = getOrderList();
        for (Order order : mOrderList) {
            if (TextUtils.equals(order.getBarCode(), update.getBarCode())) {
                order.setStatus(status);
            }
        }
        orderList.setValue(mOrderList);
    }

    public void getLocation() {
        AmapManager.getInstance().getOneLocation(aMapLocation -> address.set(aMapLocation.getAddress()));
    }

    public boolean checkInput() {
        if (TextUtils.isEmpty(phone.get()) || TextUtils.isEmpty(address.get())) {
            return false;
        }
        return true;
    }

}
