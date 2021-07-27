package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AgreementCustomersModel extends BaseViewModel {
    public static final String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";

    public ObservableField<IDCardRecord> idCardRecord = new ObservableField<>();
    public ObservableField<String> address = new ObservableField<>();

    public MutableLiveData<List<Express>> expressList = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Express> newExpress = new SingleLiveEvent<>();
    public MutableLiveData<String> repeatExpress = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> saveFlag = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> scanFlag = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> deleteFlag = new SingleLiveEvent<>();


    public ObservableField<String> rqCode = new ObservableField<>();

    public ObservableField<String> clientPhone = new ObservableField<>();
    public ObservableField<String> clientName = new ObservableField<>();
    public ObservableField<String> itemName = new ObservableField<>();
    public ObservableField<String> theQuantityOfGoods = new ObservableField<>();

    public MutableLiveData<Bitmap> showPicture = new MutableLiveData<>();

    private volatile AtomicLong timeFilter = new AtomicLong(0L);

    public AgreementCustomersModel() {
        ScanManager.getInstance().initDevice(App.getInstance(), listener);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        ScanManager.getInstance().closeDevice();
        if (address != null && address.get() != null) {
            address.set("");
        }
        if (expressList != null && expressList.getValue() != null) {
            expressList.setValue(new ArrayList<>());
        }
    }

    public void initExpressList(List<Express> mExpressList) {
        expressList.setValue(mExpressList);
        if (mExpressList != null && !mExpressList.isEmpty()) {
            Express express = mExpressList.get(0);
            address.set(express.getSenderAddress());
            if (!TextUtils.isEmpty(express.getBarCode())&&!express.getBarCode().startsWith(App.getInstance().BarHeader)){
                rqCode.set(express.getBarCode());
            }
            clientName.set(express.getCustomerName());
            clientPhone.set(express.getCustomerPhone());
            itemName.set(express.getGoodsName());
            theQuantityOfGoods.set(express.getGoodsNumber());
            if (express.getPhotoList() != null && !express.getPhotoList().isEmpty()) {
                Bitmap s = express.getPhotoList().get(0);
                showPicture.postValue(s);
            }
        }
    }

    public void startScan() {
        scanFlag.setValue(Boolean.TRUE);
        Log.e("startScan","startScan");
        ScanManager.getInstance().startScan();
    }

    public void stopScan() {
        try {
            ScanManager.getInstance().stopScan();
            scanFlag.setValue(Boolean.FALSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ScanManager.OnScanListener listener = this::handlerScanCode;

    public void handlerScanCode(String code) {
        if (System.currentTimeMillis() - timeFilter.get() < 2000) {
            resultMessage.setValue("操作太频繁");
            return;
        }
        timeFilter.set(System.currentTimeMillis());
        stopScan();
        waitMessage.setValue("扫描成功，开始校验");
        Disposable disposable = Observable.just(code)
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(mCode -> {
                    if (checkCodeLocalRepeat(mCode)) {
                        waitMessage.setValue("");
                        repeatExpress.setValue(mCode);
                        throw new MyException("本地校验重复，导向已有单号");
                    } else {
                        waitMessage.setValue("正在校验单号是否重复...");
                    }
                }).observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .doOnNext(mCode -> {
                    if (checkCodeDataBaseRepeat(mCode)) {
                        waitMessage.postValue("");
                        repeatExpress.postValue("");
                        throw new MyException("本地已有该单号，请勿重复添加");
                    } else if (!checkCodeNetRepeat(mCode)) {
                        waitMessage.postValue("");
                        repeatExpress.postValue("");
                        throw new MyException("联网校验重复");
                    } else {
                        waitMessage.postValue("联网校验通过，正在生成快递订单...");
                        rqCode.set(mCode);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(mCode -> {
                    waitMessage.setValue("");
                    removeRepeatEdit(mCode);
                    makeNewExpress(mCode);
                }, throwable -> {
                    waitMessage.postValue("");
                    throwable.printStackTrace();
                    removeRepeatEdit(code);
                });
    }

    private void removeRepeatEdit(String code) {
        String addressStr = address.get();
        if (!TextUtils.isEmpty(addressStr)) {
            if (addressStr.contains(code)) {
                addressStr = addressStr.replace(code, "");
                address.set(addressStr);
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

    public void saveDraft() {
        IDCardRecord cardMessage = idCardRecord.get();
        List<Express> expressList = getExpressList();
        if (expressList == null || expressList.isEmpty()) {
            expressList = new ArrayList<>();
//            if(!TextUtils.isEmpty(rqCode.get())){
//                Express value = newExpress.getValue();
//                if (value == null) {
//                    value = new Express();
//                }
//                value.setPhotoList(new ArrayList<>());
//                value.setPhotoPathList(new ArrayList<>());
//                expressList.add(value);
//            }
        }
        if (cardMessage == null) {
            resultMessage.setValue("未找到待上传数据");
            return;
        }
        cardMessage.setDraft(true);
        for (Express express : expressList) {
            express.setDraft(true);
        }
        saveExpress(cardMessage, expressList, true);
    }

    public void saveComplete() {
        IDCardRecord cardMessage = idCardRecord.get();
        List<Express> expressList = getExpressList();
        if (expressList == null || expressList.isEmpty()) {
            expressList = new ArrayList<>();
            Express value = newExpress.getValue();
            if (value == null) {
                value = new Express();
            }
            value.setPhotoList(new ArrayList<>());
            value.setPhotoPathList(new ArrayList<>());
            expressList.add(value);
        }
        if (cardMessage == null || expressList.isEmpty()) {
            resultMessage.setValue("未找到待上传数据");
            return;
        }
        cardMessage.setDraft(false);
        for (Express express : expressList) {
            express.setDraft(false);
        }
        saveExpress(cardMessage, expressList, false);
    }

    /**
     * 快递是否有实物拍照
     */
    private void isExpressNoImage(IDCardRecord cardMessage, List<Express> expressList) {
        if (expressList == null || expressList.isEmpty()) {
            return;
        }
        for (Express express : expressList) {
            if (express.getPhotoList() == null || express.getPhotoList().isEmpty()) {
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
    }

    private void saveExpress(IDCardRecord cardMessage, List<Express> expressList, boolean draft) {
        waitMessage.setValue("正在保存...");
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
            isExpressNoImage(cardMessage, expressList);
            cardMessage.setUpload(false);
            cardMessage.setSenderAddress(getAddress());
            String verifyId = IDCardRecordRepository.getInstance().saveIdCardRecord(cardMessage);
            emitter.onNext(verifyId);
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .map(verifyId -> {
                    AMapLocation aMapLocation = AmapManager.getInstance().getaMapLocation();
                    for (Express express : expressList) {
                        express.setSenderAddress(getAddress());
                        express.setVerifyId(verifyId);
                        express.setLatitude(aMapLocation != null ? String.valueOf(aMapLocation.getLatitude()) : "");
                        express.setLongitude(aMapLocation != null ? String.valueOf(aMapLocation.getLongitude()) : "");
                        express.setPieceTime(new Date());
                        express.setUpload(false);
                        express.setComplete(true);
                        express.setCustomerType("2");
                        express.setCustomerName(getRepString(clientName.get()));
                        express.setGoodsNumber(getRepString(theQuantityOfGoods.get()));
                        express.setGoodsName(getRepString(itemName.get()));
                        express.setCustomerPhone(getRepString(clientPhone.get()));
                        express.setPhone(ValueUtil.GlobalPhone);
                        ExpressRepository.getInstance().saveExpress(express);
                    }
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
                address.set(addressStr);
            }
        });
    }

    public boolean checkInput() {
        if (TextUtils.isEmpty(address.get())) {
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

    private boolean checkCodeDataBaseRepeat(String code) {
        Express express = ExpressRepository.getInstance().loadExpressWithCode(code);
        return express != null;
    }

    private String getAddress() {
        String addressStr = address.get();
        if (TextUtils.isEmpty(addressStr)) {
            AMapLocation aMapLocation = AmapManager.getInstance().getaMapLocation();
            return aMapLocation != null ? aMapLocation.getAddress() : "";
        }
        return addressStr;
    }

    public boolean isAllComplete() {
        for (Express express : getExpressList()) {
            if (!express.isComplete()) {
                return false;
            }
        }
        return true;
    }

    public void deleteSelf() {
        waitMessage.setValue("数据处理中，请稍后...");
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            List<Express> expressList = getExpressList();
            for (Express express : expressList) {
                try {
                    ExpressRepository.getInstance().deleteExpress(express);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            IDCardRecord idCardRecord = this.idCardRecord.get();
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
        IDCardRecord cardMessage = idCardRecord.get();
        String addressStr = getAddress();
        List<Express> expressList = getExpressList();
        if (expressList == null || expressList.isEmpty()) {
            expressList = new ArrayList<>();
            Express value = newExpress.getValue();
            if (value == null) {
                value = new Express();
            }
            value.setPhotoList(new ArrayList<>());
            value.setPhotoPathList(new ArrayList<>());
        }
        if (cardMessage == null) {
            resultMessage.setValue("未找到待上传数据");
            return;
        }
        cardMessage.setType(2);
        waitMessage.setValue("正在保存...");
        List<Express> finalExpressList = expressList;
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
                    for (Express express : finalExpressList) {
                        express.setSenderAddress(addressStr);
                        express.setVerifyId(verifyId);
                        express.setLatitude(aMapLocation != null ? String.valueOf(aMapLocation.getLatitude()) : "");
                        express.setLongitude(aMapLocation != null ? String.valueOf(aMapLocation.getLongitude()) : "");
                        express.setPieceTime(new Date());
                        express.setUpload(false);
                        express.setComplete(true);
                        express.setCustomerType("2");
                        express.setCustomerName(getRepString(clientName.get()));
                        express.setGoodsNumber(getRepString(theQuantityOfGoods.get()));
                        express.setGoodsName(getRepString(itemName.get()));
                        express.setCustomerPhone(getRepString(clientPhone.get()));
                        express.setPhone(ValueUtil.GlobalPhone);
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

    private String getRepString(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        return s;
    }

}
