package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.PostalApp;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.data.repository.PostalRepository;
import com.miaxis.postal.manager.AmapManager;
import com.miaxis.postal.manager.ScanManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.FileUtil;
import com.speedata.libid2.IDInfor;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ExpressViewModel extends BaseViewModel {

    public ObservableField<IDInfor> idInfor = new ObservableField<>();
    public ObservableField<Bitmap> header = new ObservableField<>();
    public ObservableField<TempId> tempId = new ObservableField<>();
    public ObservableField<String> phone = new ObservableField<>();
    public ObservableField<String> address = new ObservableField<>();

    public MutableLiveData<List<Express>> expressList = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Express> newExpress = new SingleLiveEvent<>();
    public MutableLiveData<Express> repeatExpress = new SingleLiveEvent<>();

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

    private ScanManager.OnScanListener listener = code -> {
        stopScan();
        Express express = new Express();
        express.setBarCode(code);
        express.setStatus(Status.LOADING);
        if (!checkRepeat(express)) {
            newExpress.setValue(express);
        } else {
            repeatExpress.setValue(express);
        }
    };

    public List<Express> getExpressList() {
        List<Express> value = expressList.getValue();
        if (value == null) {
            List<Express> newArrayList = new ArrayList<>();
            expressList.setValue(newArrayList);
            return newArrayList;
        } else {
            return value;
        }
    }

    public void modifyExpress(Express express) {
        List<Express> localList = getExpressList();
        for (int i = 0; i < localList.size(); i++) {
            Express value = localList.get(i);
            if (TextUtils.equals(value.getBarCode(), express.getBarCode())) {
                localList.set(i, express);
                expressList.setValue(localList);
                return;
            }
        }
        localList.add(express);
        expressList.setValue(localList);
    }

    public void deleteExpress(Express express) {
        List<Express> localList = getExpressList();
        Iterator<Express> iterator = localList.iterator();
        while (iterator.hasNext()) {
            Express next = iterator.next();
            if (TextUtils.equals(next.getBarCode(), express.getBarCode())) {
                iterator.remove();
                break;
            }
        }
        expressList.setValue(localList);
    }
    
//    private void uploadExpress(Express express) {
//        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
//            PostalRepository.getInstance().saveExpressFromAppSync(express, tempId.get(), address.get(), phone.get());
//            emitter.onNext(Boolean.TRUE);
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(aBoolean -> {
//                    updateExpressStatus(express, Status.SUCCESS);
//                }, throwable -> {
//                    updateExpressStatus(express, Status.FAILED);
//                    toast.setValue(ToastManager.getToastBody(hanleError(throwable), ToastManager.INFO));
//                });
//    }
//
//    private void updateExpressStatus(Express update, Status status) {
//        List<Express> mExpressList = getExpressList();
//        for (Express express : mExpressList) {
//            if (TextUtils.equals(express.getBarCode(), update.getBarCode())) {
//                express.setStatus(status);
//            }
//        }
//        expressList.setValue(mExpressList);
//    }

    public void getLocation() {
        AmapManager.getInstance().getOneLocation(aMapLocation -> address.set(aMapLocation.getAddress()));
    }

    public boolean checkInput() {
        if (TextUtils.isEmpty(phone.get()) || TextUtils.isEmpty(address.get())) {
            return false;
        }
        return true;
    }

    private boolean checkRepeat(Express repeat) {
        List<Express> expressList = getExpressList();
        for (Express express : expressList) {
            if (TextUtils.equals(repeat.getBarCode(), express.getBarCode())) {
                return true;
            }
        }
        return false;
    }

}
