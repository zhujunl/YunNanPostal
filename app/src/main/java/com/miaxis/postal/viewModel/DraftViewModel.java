package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;

import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.Draft;
import com.miaxis.postal.data.entity.DraftMessage;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.repository.ExpressRepository;
import com.miaxis.postal.data.repository.IDCardRecordRepository;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.DateUtil;
import com.miaxis.postal.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DraftViewModel extends BaseViewModel {

    public MutableLiveData<List<Draft>> draftList = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<Boolean> refreshing = new SingleLiveEvent<>();
    public MutableLiveData<DraftMessage> draftMessageSearch = new SingleLiveEvent<>();

    public DraftViewModel() {
    }

    public List<Draft> getDraftList() {
        List<Draft> value = draftList.getValue();
        if (value == null) {
            List<Draft> newArrayList = new ArrayList<>();
            draftList.setValue(newArrayList);
            return newArrayList;
        } else {
            return value;
        }
    }

    public void loadDraftByPage(int pageNum) {
        Disposable disposable = Observable.create((ObservableOnSubscribe<List<Draft>>) emitter -> {
            List<IDCardRecord> recordList = IDCardRecordRepository.getInstance().loadDraftIDCardRecordByPage(pageNum, 10);
            List<Draft> mDraftList = new ArrayList<>();
            for (IDCardRecord idCardRecord : recordList) {
                Draft draft = makeDraftByIDCardRecord(idCardRecord);
                mDraftList.add(draft);
            }
            emitter.onNext(mDraftList);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mDraftList -> {
                    refreshing.setValue(Boolean.FALSE);
                    if (pageNum == 1) {
                        if (mDraftList.isEmpty()) {
//                            toast.setValue(ToastManager.getToastBody("本地暂无缓存日志", ToastManager.SUCCESS));
                        }
                        draftList.setValue(mDraftList);
                    } else {
                        if (mDraftList.isEmpty()) {
                            toast.setValue(ToastManager.getToastBody("没有更多了", ToastManager.SUCCESS));
                        }
                        List<Draft> draftListCache = getDraftList();
                        draftListCache.addAll(mDraftList);
                        draftList.setValue(draftListCache);
                    }
                }, throwable -> {
                    refreshing.setValue(Boolean.FALSE);
                    resultMessage.setValue(handleError(throwable));
                });
    }

    private Draft makeDraftByIDCardRecord(IDCardRecord idCardRecord) {
        List<Express> expressList = ExpressRepository.getInstance().loadExpressByVerifyId(idCardRecord.getVerifyId());
        return new Draft.Builder()
                .idCardRecordId(idCardRecord.getId())
                .name(idCardRecord.getName())
                .cardNumber(idCardRecord.getCardNumber())
                .verifyTime(DateUtil.DATE_FORMAT.format(idCardRecord.getVerifyTime()))
                .orderCount(expressList != null ? expressList.size() : 0)
                .build();
    }

    public void getDraftMessage(Draft draft) {
        waitMessage.setValue("查询中，请稍后...");
        Observable.create((ObservableOnSubscribe<DraftMessage>) emitter -> {
            IDCardRecord idCardRecord = IDCardRecordRepository.getInstance().loadIDCardRecordById(draft.getIdCardRecordId());
            if (idCardRecord != null) {
                Bitmap cardBitmap = FileUtil.loadBitmap(idCardRecord.getCardPicture());
                if (cardBitmap != null) {
                    idCardRecord.setCardBitmap(cardBitmap);
                }
                Bitmap faceBitmap = FileUtil.loadBitmap(idCardRecord.getFacePicture());
                if (cardBitmap != null) {
                    idCardRecord.setFaceBitmap(faceBitmap);
                }

                List<Express> expressList = ExpressRepository.getInstance().loadExpressByVerifyId(idCardRecord.getVerifyId());
                for (Express express : expressList) {
                    List<Bitmap> photoList = new ArrayList<>();
                    for (String path : express.getPhotoPathList()) {
                        photoList.add(FileUtil.loadBitmap(path));
                    }
                    express.setPhotoList(photoList);
                }
                DraftMessage draftMessage = new DraftMessage(idCardRecord, expressList);
                emitter.onNext(draftMessage);
                return;
            }
            throw new MyException("未查询到数据");
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(draftMessage -> {
                    waitMessage.postValue("");
                    draftMessageSearch.postValue(draftMessage);
                }, throwable -> {
                    throwable.printStackTrace();
                    waitMessage.postValue("");
                    ToastManager.toast("查询时出现错误，请重试", ToastManager.ERROR);
                });
    }

}
