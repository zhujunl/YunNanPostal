package com.miaxis.postal.viewModel;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.WarnLog;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.repository.ExpressRepository;
import com.miaxis.postal.data.repository.IDCardRecordRepository;
import com.miaxis.postal.data.repository.PostalRepository;
import com.miaxis.postal.data.repository.WarnLogRepository;
import com.miaxis.postal.manager.AmapManager;
import com.miaxis.postal.manager.DataCacheManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.manager.ScanManager;
import com.miaxis.postal.util.ValueUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AgreementCustomersModel extends BaseViewModel {
    private final String TAG = "AgreementCustomersModel";
    public static final String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";

    public MutableLiveData<IDCardRecord> idCardRecordLiveData = new MutableLiveData<>();

    public MutableLiveData<Express> expressLiveData = new MutableLiveData<>(new Express());

    public MutableLiveData<Boolean> repeat = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> saveFlag = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> scanFlag = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> deleteFlag = new SingleLiveEvent<>();

    public MutableLiveData<String> address = new MutableLiveData<>();

    public MutableLiveData<String> rqCode = new MutableLiveData<>();

    public MutableLiveData<String> clientPhone = new MutableLiveData<>();
    public MutableLiveData<String> clientName = new MutableLiveData<>();
    public MutableLiveData<String> itemName = new MutableLiveData<>();
    public MutableLiveData<String> theQuantityOfGoods = new MutableLiveData<>();

    public MutableLiveData<String> showPicture = new MutableLiveData<>();

    private volatile AtomicLong timeFilter = new AtomicLong(0L);
    private Handler mHandler = new Handler();

    public AgreementCustomersModel() {
        ScanManager.getInstance().initDevice(App.getInstance(), listener);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        try {
            ScanManager.getInstance().closeDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        if (address != null && address.get() != null) {
        //            address.set("");
        //        }
        mHandler.removeCallbacksAndMessages(null);
        ScanManager.getInstance().powerOff();
    }

    public void initExpress(Express express) {
        Log.e(TAG, "express:" + express);
        if (TextUtils.isEmpty(rqCode.getValue())){
            rqCode.setValue(express.getBarCode());
        }
        if (TextUtils.isEmpty(address.getValue())) {
            address.setValue(express.getSenderAddress());
        }
        if (TextUtils.isEmpty(clientName.getValue())) {
            clientName.setValue(express.getCustomerName());
        }
        if (TextUtils.isEmpty(clientPhone.getValue())) {
            clientPhone.setValue(express.getCustomerPhone());
        }
        if (TextUtils.isEmpty(itemName.getValue())) {
            itemName.setValue(express.getGoodsName());
        }
        if (TextUtils.isEmpty(theQuantityOfGoods.getValue())) {
            theQuantityOfGoods.setValue(express.getGoodsNumber());
        }
        List<String> photoPathList = express.getPhotoPathList();
        if (photoPathList != null && !photoPathList.isEmpty()) {
            String path = photoPathList.get(0);
            showPicture.postValue(path);
        }
    }

    public void startScan() {
        if (System.currentTimeMillis() - timeFilter.get() < 2000) {
            resultMessage.setValue("操作太频繁");
            return;
        }
        timeFilter.set(System.currentTimeMillis());
        scanFlag.setValue(Boolean.TRUE);
        ScanManager.getInstance().startScan();
        ScanManager.getInstance().powerOn();
    }

    public void stopScan() {
        try {
            scanFlag.setValue(Boolean.FALSE);
            ScanManager.getInstance().stopScan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ScanManager.OnScanListener listener = this::handlerScanCode;

    public void handlerScanCode(String code) {
        stopScan();
        scanFlag.setValue(Boolean.FALSE);
        waitMessage.setValue("扫描成功，开始校验");
        Disposable disposable = Observable.just(code)
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(mCode -> {
                    if (checkCodeLocalRepeat(mCode)) {
                        waitMessage.setValue("");
                        repeat.postValue(true);
                        throw new MyException("本地校验重复，导向已有单号");
                    } else {
                        waitMessage.setValue("正在校验单号是否重复...");
                    }
                }).observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .doOnNext(mCode -> {
                    if (checkCodeDataBaseRepeat(mCode)) {
                        waitMessage.postValue("");
                        repeat.postValue(true);
                        throw new MyException("本地已有该单号，请勿重复添加");
                    } else if (!checkCodeNetRepeat(mCode)) {
                        waitMessage.postValue("");
                        repeat.postValue(true);
                        throw new MyException("联网校验重复");
                    } else {
                        waitMessage.postValue("联网校验通过，正在生成快递订单...");
                        rqCode.postValue(mCode);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(mCode -> {
                    waitMessage.setValue("");
                    removeRepeatEdit(mCode);
                    makeNewExpress(mCode);
                }, throwable -> {
                    waitMessage.postValue("");
                    resultMessage.postValue("" + throwable.getMessage());
                    throwable.printStackTrace();
                    removeRepeatEdit(code);
                });
    }

    private void removeRepeatEdit(String code) {
        String addressStr = address.getValue();
        if (!TextUtils.isEmpty(addressStr)) {
            if (addressStr.contains(code)) {
                addressStr = addressStr.replace(code, "");
                address.setValue(addressStr);
            }
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

    public void makeNewExpress(String code) {
        Express value = expressLiveData.getValue();
        if (value != null) {
            value.setBarCode(code);
        }
        expressLiveData.setValue(value);
    }

    public void saveDraft() {
        IDCardRecord cardMessage = idCardRecordLiveData.getValue();
        if (cardMessage == null) {
            resultMessage.setValue("未找到待上传数据");
            return;
        }
        cardMessage.setDraft(true);
        Express value = expressLiveData.getValue();
        if (value == null) {
            resultMessage.setValue("数据为空");
            return;
        }
        value.setDraft(true);
        saveExpress(cardMessage, true);
    }

    public void saveComplete() {
        IDCardRecord cardMessage = idCardRecordLiveData.getValue();
        Express value = expressLiveData.getValue();
        if (cardMessage == null) {
            resultMessage.setValue("未找到待上传数据");
            return;
        }
        cardMessage.setDraft(false);
        value.setDraft(false);
        saveExpress(cardMessage, false);
    }

    /**
     * 快递是否有实物拍照
     */
    private void isExpressNoImage(IDCardRecord cardMessage, Express express) {
        if (express == null) {
            return;
        }
        if (express.getPhotoPathList() == null || express.getPhotoPathList().isEmpty()) {
            String addressStr = getAddress();
            Courier courier = DataCacheManager.getInstance().getCourier();
            WarnLog warnLog = new WarnLog.Builder()
                    .verifyId(cardMessage.getCheckId())
                    .sendAddress(addressStr)
                    .sendCardNo(cardMessage.getCardNumber())
                    .sendName(cardMessage.getName())
                    .expressmanId(courier.getCourierId())
                    .expressmanName(courier.getName())
                    .expressmanPhone(courier.getPhone())
                    .createTime(new Date())
                    .upload(false)
                    .build();
            WarnLogRepository.getInstance().saveWarnLog(warnLog);
        }
    }

    private void saveExpress(IDCardRecord cardMessage, boolean draft) {
        waitMessage.setValue("正在保存...");
        Express express = this.expressLiveData.getValue();
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            //核验状态 0：未核验 1：核验通过  2：核验未通过
            if (cardMessage.getChekStatus() == 0 || cardMessage.getChekStatus() == 2) {
                String addressStr = getAddress();
                Courier courier = DataCacheManager.getInstance().getCourier();
                WarnLog warnLog = new WarnLog.Builder()
                        .verifyId(cardMessage.getCheckId())
                        .sendAddress(addressStr)
                        .sendCardNo(cardMessage.getCardNumber())
                        .sendName(cardMessage.getName())
                        .expressmanId(courier.getCourierId())
                        .expressmanName(courier.getName())
                        .expressmanPhone(courier.getPhone())
                        .createTime(new Date())
                        .upload(false)
                        .build();
                WarnLogRepository.getInstance().saveWarnLog(warnLog);
            }
            cardMessage.setType(2);
            isExpressNoImage(cardMessage, express);
            cardMessage.setUpload(false);
            cardMessage.setSenderAddress(getAddress());
            String verifyId = IDCardRecordRepository.getInstance().saveIdCardRecord(cardMessage);
            emitter.onNext(verifyId);
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .map(verifyId -> {
                    AMapLocation aMapLocation = AmapManager.getInstance().getaMapLocation();
                    String rqCode = this.rqCode.getValue();
                    express.setBarCode(TextUtils.isEmpty(rqCode) ? App.getInstance().getRandomBarCode() : rqCode);
                    express.setSenderAddress(getAddress());
                    express.setVerifyId(verifyId);
                    express.setLatitude(aMapLocation != null ? String.valueOf(aMapLocation.getLatitude()) : "");
                    express.setLongitude(aMapLocation != null ? String.valueOf(aMapLocation.getLongitude()) : "");
                    express.setPieceTime(new Date());
                    express.setUpload(false);
                    express.setComplete(true);
                    express.setCustomerType("2");
                    express.setCustomerName(getRepString(clientName.getValue()));
                    express.setGoodsNumber(getRepString(theQuantityOfGoods.getValue()));
                    express.setGoodsName(getRepString(itemName.getValue()));
                    express.setCustomerPhone(getRepString(clientPhone.getValue()));
                    express.setPhone(ValueUtil.GlobalPhone);
                    ExpressRepository.getInstance().saveExpress(express);
                    return true;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    waitMessage.setValue("");
                    resultMessage.setValue(draft ? "数据已存入草稿箱" : "数据已缓存，将于后台自动传输");
                    saveFlag.setValue(Boolean.TRUE);

                    PostalManager.getInstance().startPostal();
                }, throwable -> {
                    waitMessage.setValue("");
                    resultMessage.setValue("数据缓存失败，失败原因：\n" + throwable.getMessage());
                });
    }

    public void getLocation() {
        AmapManager.getInstance().getOneLocation(addressStr -> {
            if (!TextUtils.isEmpty(addressStr)) {
                address.setValue(addressStr);
            }
        });
    }

    public boolean checkInput() {
        return !TextUtils.isEmpty(address.getValue());
    }

    private boolean checkCodeLocalRepeat(String code) {
        if (TextUtils.isEmpty(code)) {
            return false;
        }
        Express value = expressLiveData.getValue();
        return value != null && code.equals(value.getBarCode());
    }

    private boolean checkCodeDataBaseRepeat(String code) {
        Express express = ExpressRepository.getInstance().loadExpressWithCode(code);
        return express != null;
    }

    private String getAddress() {
        String addressStr = address.getValue();
        if (TextUtils.isEmpty(addressStr)) {
            AMapLocation aMapLocation = AmapManager.getInstance().getaMapLocation();
            return aMapLocation != null ? aMapLocation.getAddress() : "";
        }
        return addressStr;
    }

    public boolean isAllComplete() {
        Express value = expressLiveData.getValue();
        return value != null && value.isComplete();
    }

    public void deleteSelf() {
        waitMessage.setValue("数据处理中，请稍后...");
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            try {
                Express value = expressLiveData.getValue();
                if (value != null) {
                    ExpressRepository.getInstance().deleteExpress(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            IDCardRecord idCardRecord = this.idCardRecordLiveData.getValue();
            //删除网络图片
            if (idCardRecord != null && !TextUtils.isEmpty(idCardRecord.getWebCardPath()) && !TextUtils.isEmpty(idCardRecord.getWebFacePath())) {
                List<String> webPath = new ArrayList<>();
                webPath.add(idCardRecord.getWebCardPath());
                webPath.add(idCardRecord.getWebFacePath());
                for (String path : webPath) {
                    ExpressRepository.getInstance().deleteWebPicture(path);
                }
            }
            if (idCardRecord != null) {
                AppDatabase.getInstance().warnLogDao().delete(idCardRecord.getVerifyId());
                IDCardRecordRepository.getInstance().deleteIDCardRecord(idCardRecord);
            }
            emitter.onNext(Boolean.TRUE);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    waitMessage.setValue("");
                    resultMessage.setValue("删除成功");
                    deleteFlag.setValue(result);
                }, throwable -> {
                    throwable.printStackTrace();
                    waitMessage.setValue("");
                    resultMessage.setValue("删除失败，原因：" + throwable.getMessage());
                });
    }

    public void alarm() {
        IDCardRecord cardMessage = idCardRecordLiveData.getValue();
        String addressStr = getAddress();
        Express value = expressLiveData.getValue();
        if (cardMessage == null || value == null) {
            resultMessage.setValue("未找到待上传数据");
            return;
        }
        cardMessage.setType(2);
        waitMessage.setValue("正在保存...");
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            cardMessage.setUpload(false);
            cardMessage.setDraft(false);
            String verifyId = IDCardRecordRepository.getInstance().saveIdCardRecord(cardMessage);
            emitter.onNext(verifyId);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .doOnNext(verifyId -> {
                    Courier courier = DataCacheManager.getInstance().getCourier();
                    WarnLog warnLog = new WarnLog.Builder()
                            .verifyId(verifyId)
                            .sendAddress(addressStr)
                            .sendCardNo(cardMessage.getCardNumber())
                            .sendName(cardMessage.getName())
                            .expressmanId(courier.getCourierId())
                            .expressmanName(courier.getName())
                            .expressmanPhone(courier.getPhone())
                            .createTime(new Date())
                            .upload(false)
                            .build();
                    WarnLogRepository.getInstance().saveWarnLog(warnLog);
                })
                .map(verifyId -> {
                    AMapLocation aMapLocation = AmapManager.getInstance().getaMapLocation();
                    Express express = this.expressLiveData.getValue();
                    String rqCode = this.rqCode.getValue();
                    express.setBarCode(TextUtils.isEmpty(rqCode) ? App.getInstance().getRandomBarCode() : rqCode);
                    express.setSenderAddress(addressStr);
                    express.setVerifyId(verifyId);
                    express.setLatitude(aMapLocation != null ? String.valueOf(aMapLocation.getLatitude()) : "");
                    express.setLongitude(aMapLocation != null ? String.valueOf(aMapLocation.getLongitude()) : "");
                    express.setPieceTime(new Date());
                    express.setUpload(false);
                    express.setComplete(true);
                    express.setCustomerType("2");
                    express.setCustomerName(getRepString(clientName.getValue()));
                    express.setGoodsNumber(getRepString(theQuantityOfGoods.getValue()));
                    express.setGoodsName(getRepString(itemName.getValue()));
                    express.setCustomerPhone(getRepString(clientPhone.getValue()));
                    express.setPhone(ValueUtil.GlobalPhone);
                    ExpressRepository.getInstance().saveExpress(express);
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

    private String getRepString(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        return s;
    }

}
