package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.entity.IDCard;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.Photograph;
import com.miaxis.postal.data.entity.WarnLog;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.repository.IDCardRecordRepository;
import com.miaxis.postal.data.repository.IDCardRepository;
import com.miaxis.postal.data.repository.WarnLogRepository;
import com.miaxis.postal.manager.DataCacheManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ManualViewModel extends BaseViewModel {

    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> identityNumber = new ObservableField<>();

    public MutableLiveData<List<Photograph>> photographList = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<IDCard>> idCardListLiveData;

    public MutableLiveData<Boolean> confirm = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> idCardSearch = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> alarmFlag = new SingleLiveEvent<>();

    public IDCardRecord idCardRecord;
    public IDCardRecord idCardRecordCache;

    public ManualViewModel() {
    }

    public void addPhotograph(List<Bitmap> bitmapList) {
        List<Photograph> cacheList = new ArrayList<>();
        for (Bitmap bitmap : bitmapList) {
            cacheList.add(new Photograph(bitmap, false));
        }
        int selectSize = getSelectSize();
        if (selectSize < InspectViewModel.MAX_COUNT && cacheList.size() > 0) {
            int surplus = InspectViewModel.MAX_COUNT - selectSize;
            if (surplus > 0) {
                for (int i = 0; i < surplus; i++) {
                    if (i + 1 > cacheList.size())
                        break;
                    cacheList.get(i).setSelect(true);
                }
            }
        }
        List<Photograph> photoList = getPhotographList();
        photoList.addAll(cacheList);
        this.photographList.setValue(photoList);
    }

    public List<Photograph> getPhotographList() {
        List<Photograph> value = photographList.getValue();
        if (value == null) {
            List<Photograph> newArrayList = new ArrayList<>();
            photographList.setValue(newArrayList);
            return newArrayList;
        } else {
            return value;
        }
    }

    public int getSelectSize() {
        int count = 0;
        for (Photograph photograph : getPhotographList()) {
            if (photograph.isSelect()) {
                count++;
            }
        }
        return count;
    }

    public List<Bitmap> getSelectList() {
        List<Bitmap> selectList = new ArrayList<>();
        for (Photograph photograph : getPhotographList()) {
            if (photograph.isSelect()) {
                selectList.add(photograph.getBitmap());
            }
        }
        return selectList;
    }

    public void loadIDCardList() {
        LiveData<List<IDCard>> listLiveData = IDCardRepository.getInstance().loadAllWithLiveData();
        List<IDCard> lists = new ArrayList<>();
        MutableLiveData<List<IDCard>> liveLists = new MutableLiveData<>();
        if (listLiveData.getValue() != null) {
            for (IDCard idCard : listLiveData.getValue()) {
                if (!TextUtils.isEmpty(idCard.getCardPicture())) {
                    lists.add(idCard);
                }
            }
            liveLists.setValue(lists);
            idCardListLiveData = listLiveData;
        }
    }

    public void confirm() {
        waitMessage.setValue("确认中，请稍后...");
        App.getInstance().getThreadExecutor().execute(() -> {
            try {
                if (idCardRecord == null) {
                    idCardRecord = makeIDCardRecord();
                } else {
                    idCardRecord.setManualType("1");
                }
                if (idCardRecord.getCardBitmap() != null) {
                    String cardPath = FileUtil.FACE_STOREHOUSE_PATH + File.separator + "card_" + idCardRecord.getCardNumber() + System.currentTimeMillis() + ".jpg";
                    FileUtil.saveBitmap(idCardRecord.getCardBitmap(), cardPath);
                    idCardRecord.setCardPicture(cardPath);
                }
                List<Bitmap> selectList = getSelectList();
                Bitmap bitmap = selectList.get(0);
                idCardRecord.setFaceBitmap(bitmap);
                //移除 点击提交会保存
                String facePath = FileUtil.FACE_STOREHOUSE_PATH + File.separator + "face_" + idCardRecord.getCardNumber() + System.currentTimeMillis() + ".jpg";
                File file = FileUtil.saveBitmap(idCardRecord.getFaceBitmap(), facePath);
                Log.e("confirm", "" + file);
                idCardRecord.setFacePicture(facePath);
                idCardRecord.setVerifyTime(new Date());
                idCardRecord.setVerifyType("1");

                waitMessage.postValue("");
                confirm.postValue(Boolean.TRUE);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("confirm", "Exception：" + e);
                waitMessage.postValue("");
                confirm.postValue(Boolean.FALSE);
            }
        });
    }

    private IDCardRecord makeIDCardRecord() {
        String nameStr = name.get();

        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(nameStr);
        nameStr = m.replaceAll("");

        Log.i("TAG", nameStr);
        String identityNumberStr = identityNumber.get();
        if (!TextUtils.isEmpty(identityNumberStr)) {
            identityNumberStr = identityNumberStr.toUpperCase();
        }
        return new IDCardRecord.Builder()
                .name(nameStr)
                .cardNumber(identityNumberStr)
                .manualType("2")
                .build();
    }

    public void searchLocalIDCard(String cardNumber) {
        waitMessage.setValue("查询中，请稍后...");
        Observable.create((ObservableOnSubscribe<IDCard>) emitter -> {
            IDCard idCard = IDCardRepository.getInstance().findIDCardByCardNumber(cardNumber);
            if (idCard != null) {
                emitter.onNext(idCard);
            } else {
                throw new MyException("未查询到");
            }
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .map(idCard -> {
                    Bitmap bitmap = FileUtil.loadBitmap(idCard.getCardPicture());
                    if (bitmap == null) {
                        throw new MyException("未找到本地缓存图片");
                    } else {
                        return transIDCardRecord(idCard, bitmap);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mIdCardRecord -> {
                    waitMessage.setValue("");
                    idCardRecordCache = mIdCardRecord;
                    if (idCardRecordCache != null) {
                        ToastManager.toast("本地证件信息缓存", ToastManager.SUCCESS);
                        idCardSearch.setValue(Boolean.TRUE);
                    }
                }, throwable -> {
                    waitMessage.setValue("");
                    idCardSearch.setValue(Boolean.FALSE);
                });
    }

    private IDCardRecord transIDCardRecord(IDCard idCard, Bitmap bitmap) {
        return new IDCardRecord.Builder()
                .cardType("")
                .name(idCard.getName())
                .birthday(idCard.getBirthday())
                .address(idCard.getAddress())
                .cardNumber(idCard.getCardNumber())
                .issuingAuthority(idCard.getIssuingAuthority())
                .validateStart(idCard.getValidateStart())
                .validateEnd(idCard.getValidateEnd())
                .sex(idCard.getSex())
                .nation(idCard.getNation())
                .passNumber(idCard.getPassNumber())
                .issueCount(idCard.getIssueCount())
                .chineseName(idCard.getChineseName())
                .version(idCard.getVersion())
                .cardBitmap(bitmap)
                .build();
    }

    public void alarm() {
        waitMessage.setValue("正在保存");
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            if (idCardRecord == null) {
                idCardRecord = makeIDCardRecord();
            } else {
                idCardRecord.setManualType("1");
            }
            List<Bitmap> selectList = getSelectList();
            if (selectList != null && !selectList.isEmpty()) {
                Bitmap bitmap = selectList.get(0);
                idCardRecord.setFaceBitmap(bitmap);
            }
            idCardRecord.setVerifyTime(new Date());
            idCardRecord.setVerifyType("1");
            idCardRecord.setUpload(false);
            idCardRecord.setDraft(false);
            String verifyId = IDCardRecordRepository.getInstance().saveIdCardRecord(idCardRecord);
            emitter.onNext(verifyId);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .doOnNext(verifyId -> {
                    Courier courier = DataCacheManager.getInstance().getCourier();
                    WarnLog warnLog = new WarnLog.Builder()
                            .verifyId(verifyId)
                            .sendAddress("")
                            .sendCardNo(idCardRecord.getCardNumber())
                            .sendPhone("")
                            .sendName(idCardRecord.getName())
                            .expressmanId(courier.getCourierId())
                            .expressmanName(courier.getName())
                            .expressmanPhone(courier.getPhone())
                            .createTime(new Date())
                            .upload(false)
                            .build();
                    WarnLogRepository.getInstance().saveWarnLog(warnLog);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    waitMessage.setValue("");
                    resultMessage.setValue("数据已缓存，将于后台自动传输");
                    alarmFlag.setValue(Boolean.TRUE);
                    PostalManager.getInstance().startPostal();
                }, throwable -> {
                    waitMessage.setValue("");
                    resultMessage.setValue("数据缓存失败，失败原因：\n" + throwable.getMessage());
                });
    }

}
