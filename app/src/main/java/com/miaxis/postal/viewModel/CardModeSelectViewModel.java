package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.IDCard;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.repository.IDCardRepository;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.FileUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CardModeSelectViewModel extends BaseViewModel {

//    public MutableLiveData<Boolean> idCardSearch = new SingleLiveEvent<>();

//    public volatile IDCardRecord idCardRecord;

    public CardModeSelectViewModel() {
    }

//    public void searchLocalIDCard(String cardNumber) {
//        waitMessage.setValue("查询中，请稍后...");
//        Observable.create((ObservableOnSubscribe<IDCard>) emitter -> {
//            IDCard idCard = IDCardRepository.getInstance().findIDCardByCardNumber(cardNumber);
//            if (idCard != null) {
//                emitter.onNext(idCard);
//            } else {
//                throw new MyException("未查询到");
//            }
//        })
//                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
//                .observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
//                .map(idCard -> {
//                    Bitmap bitmap = FileUtil.loadBitmap(idCard.getCardPicture());
//                    if (bitmap == null) {
//                        throw new MyException("未找到本地缓存图片");
//                    } else {
//                        return transIDCardRecord(idCard, bitmap);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(mIdCardRecord -> {
//                    waitMessage.setValue("");
//                    idCardRecord = mIdCardRecord;
//                    if (idCardRecord != null) {
//                        ToastManager.toast("本地证件信息缓存", ToastManager.SUCCESS);
//                        idCardSearch.setValue(Boolean.TRUE);
//                    }
//                }, throwable -> {
//                    waitMessage.setValue("");
//                    idCardSearch.setValue(Boolean.FALSE);
//                });
//    }
//
//    private IDCardRecord transIDCardRecord(IDCard idCard, Bitmap bitmap) {
//        return new IDCardRecord.Builder()
//                .cardType("")
//                .name(idCard.getName())
//                .birthday(idCard.getBirthday())
//                .address(idCard.getAddress())
//                .cardNumber(idCard.getCardNumber())
//                .issuingAuthority(idCard.getIssuingAuthority())
//                .validateStart(idCard.getValidateStart())
//                .validateEnd(idCard.getValidateEnd())
//                .sex(idCard.getSex())
//                .nation(idCard.getNation())
//                .passNumber(idCard.getPassNumber())
//                .issueCount(idCard.getIssueCount())
//                .chineseName(idCard.getChineseName())
//                .version(idCard.getVersion())
//                .cardBitmap(bitmap)
//                .build();
//    }

}
