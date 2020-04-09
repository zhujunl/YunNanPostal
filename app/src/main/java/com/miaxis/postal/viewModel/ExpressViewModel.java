package com.miaxis.postal.viewModel;

import android.text.TextUtils;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.amap.api.location.AMapLocation;
import com.miaxis.postal.app.PostalApp;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.repository.ExpressRepository;
import com.miaxis.postal.data.repository.IDCardRecordRepository;
import com.miaxis.postal.data.repository.PostalRepository;
import com.miaxis.postal.manager.AmapManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.manager.ScanManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ExpressViewModel extends BaseViewModel {

    public static final String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";

    public ObservableField<IDCardRecord> idCardRecord = new ObservableField<>();
    public ObservableField<String> phone = new ObservableField<>();
    public ObservableField<String> address = new ObservableField<>();

    public MutableLiveData<List<Express>> expressList = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Express> newExpress = new SingleLiveEvent<>();
    public MutableLiveData<String> repeatExpress = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> saveFlag = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> scanFlag = new SingleLiveEvent<>();

    public ExpressViewModel() {
    }

    public void startScan() {
        scanFlag.setValue(Boolean.TRUE);
        ScanManager.getInstance().initDevice(PostalApp.getInstance(), listener);
        ScanManager.getInstance().startScan();
    }

    public void stopScan() {
        try {
            ScanManager.getInstance().stopScan();
            ScanManager.getInstance().closeDevice();
            scanFlag.setValue(Boolean.FALSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ScanManager.OnScanListener listener = this::handlerScanCode;

    public void handlerScanCode(String code) {
        stopScan();
        waitMessage.setValue("扫描成功，开始校验");
        Disposable disposable = Observable.just(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(mCode -> {
                    if (checkCodeLocalRepeat(mCode)) {
                        waitMessage.setValue("");
                        repeatExpress.setValue(mCode);
                        throw new MyException("本地校验重复，导向已有单号");
                    } else {
                        waitMessage.setValue("本地校验通过，正在联网校验...");
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(mCode -> {
                    if (!checkCodeNetRepeat(mCode)) {
                        waitMessage.postValue("");
                        repeatExpress.postValue("");
                        throw new MyException("联网校验重复");
                    } else {
                        waitMessage.postValue("联网校验通过，正在生成快递订单...");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mCode -> {
                    removeRepeatEdit(mCode);
                    waitMessage.setValue("");
                    makeNewExpress(mCode);
                }, throwable -> {
                    throwable.printStackTrace();
                    removeRepeatEdit(code);
                });
    }

    private void removeRepeatEdit(String code) {
        String phoneStr = phone.get();
        if (phoneStr != null && phoneStr.contains(code)) {
            phoneStr = phoneStr.replace(code, "");
            phone.set(phoneStr);
        }
    }

    private boolean checkCodeNetRepeat(String code) {
        waitMessage.postValue("正在联网校验单号...");
        try {
            boolean result = PostalRepository.getInstance().checkOrderByCodeSync(code);
            waitMessage.postValue("");
            return result;
        } catch (MyException | IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    private void makeNewExpress(String code) {
        Express express = new Express();
        express.setBarCode(code);
        newExpress.setValue(express);
    }

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

    public Express getExpressByCode(String code) {
        List<Express> expressList = getExpressList();
        for (Express express : expressList) {
            if (TextUtils.equals(express.getBarCode(), code)) {
                return express;
            }
        }
        return null;
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

    public void saveExpress() {
        IDCardRecord cardMessage = idCardRecord.get();
        List<Express> expressList = getExpressList();
        if (cardMessage == null || expressList == null || expressList.isEmpty()) {
            resultMessage.setValue("未找到待上传数据");
            return;
        }
        waitMessage.setValue("正在保存...");
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            cardMessage.setUpload(false);
            String verifyId = IDCardRecordRepository.getInstance().saveIdCardRecord(cardMessage);
            emitter.onNext(verifyId);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(verifyId -> {
                    String phoneStr = phone.get();
                    String addressStr = address.get();
                    AMapLocation aMapLocation = AmapManager.getInstance().getaMapLocation();
                    for (Express express : expressList) {
                        express.setSenderAddress(addressStr);
                        express.setSenderPhone(phoneStr);
                        express.setVerifyId(verifyId);
                        express.setLatitude(aMapLocation != null ? String.valueOf(aMapLocation.getLatitude()) : "");
                        express.setLongitude(aMapLocation != null ? String.valueOf(aMapLocation.getLongitude()) : "");
                        express.setPieceTime(new Date());
                        express.setUpload(false);
                        ExpressRepository.getInstance().saveExpress(express);
                    }
                    return true;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    waitMessage.setValue("");
                    resultMessage.setValue("数据已缓存，将于后台自动传输");
                    saveFlag.setValue(Boolean.TRUE);
                    PostalManager.getInstance().startPostal();
                }, throwable -> {
                    waitMessage.setValue("");
                    resultMessage.setValue("数据缓存失败，失败原因：\n" + throwable.getMessage());
                });
    }

//    public void uploadExpress() {
//        IDCardRecord cardMessage = idCardRecord.get();
//        List<Express> expressList = getExpressList();
//        if (cardMessage == null || expressList.isEmpty()) {
//            return;
//        }
//        List<Express> cacheList = new ArrayList<>();
//        waitMessage.setValue("正在上传核验数据...");
//        Disposable disposable = Observable.create((ObservableOnSubscribe<TempId>) emitter -> {
//            TempId tempId = PostalRepository.getInstance().savePersonFromAppSync(cardMessage);
//            emitter.onNext(tempId);
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .map(tempId -> {
//                    String message = "";
//                    for (Express express : expressList) {
//                        try {
//                            message += "单号：" + express.getBarCode() + "\n\t状态：正在上传\n";
//                            waitMessage.postValue(message);
//                            boolean result = PostalRepository.getInstance().saveExpressFromAppSync(express, tempId, address.get(), phone.get());
//                            if (result) {
//                                message = message.replace("正在上传", "上传成功");
//                            } else {
//                                message = message.replace("正在上传", "重复单号");
//                            }
//                        } catch (IOException | MyException e) {
//                            e.printStackTrace();
//                            message = message.replace("正在上传", "上传失败");
//                            cacheList.add(express);
//                        }
//                        waitMessage.postValue(message);
//                    }
//                    return message;
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(message -> {
//                    waitMessage.setValue("");
//                    resultMessage.setValue(message);
//                    if (cacheList.isEmpty()) {
//                        uploadFlag.setValue(Boolean.TRUE);
//                    } else {
//                        waitMessage.setValue("部分数据上传失败，失败订单正在缓存...");
//                        saveExpressCache(message, cardMessage, cacheList, address.get(), phone.get());
//                    }
//                }, throwable -> {
//                    waitMessage.setValue("数据上传失败，失败原因：" + hanleError(throwable) + "\n数据正在缓存到本地，请勿退出");
//                    cacheList.addAll(expressList);
//                    saveExpressCache("", cardMessage, cacheList, address.get(), phone.get());
//                });
//    }

//    public void saveExpressCache(String message, IDCardRecord idCardRecord, List<Express> expressList, String address, String phone) {
//        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
//            PostalRepository.getInstance().saveLocalExpress(idCardRecord, expressList, address, phone);
//            emitter.onNext(Boolean.TRUE);
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(aBoolean -> {
//                    waitMessage.setValue("");
//                    resultMessage.setValue((TextUtils.isEmpty(message) ? "" : message + "\n\n") + "数据已缓存，将于后台自动尝试续传");
//                    uploadFlag.setValue(Boolean.TRUE);
//                }, throwable -> {
//                    waitMessage.setValue("");
//                    resultMessage.setValue((TextUtils.isEmpty(message) ? "" : message + "\n\n") + "数据缓存失败，失败原因：\n" + throwable.getMessage());
//                });
//    }

    public void getLocation() {
        AmapManager.getInstance().getOneLocation(addressStr -> address.set(addressStr));
    }

    public boolean checkInput() {
        if (TextUtils.isEmpty(phone.get()) || TextUtils.isEmpty(address.get())) {
            return false;
        }
        return true;
    }

    private boolean checkCodeLocalRepeat(String code) {
        List<Express> expressList = getExpressList();
        for (Express express : expressList) {
            if (TextUtils.equals(code, express.getBarCode())) {
                return true;
            }
        }
        return false;
    }

}
