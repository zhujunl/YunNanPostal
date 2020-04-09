package com.miaxis.postal.viewModel;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.Local;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.repository.ExpressRepository;
import com.miaxis.postal.data.repository.IDCardRecordRepository;
import com.miaxis.postal.manager.ToastManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LocalViewModel extends BaseViewModel {

    public MutableLiveData<List<Local>> localList = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<Boolean> refreshing = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> loadCardResult = new SingleLiveEvent<>();

    private List<IDCardRecord> idCardRecordList;

    public LocalViewModel() {
    }

    public void loadIdCardRecord() {
        waitMessage.setValue("正在加载数据");
        Observable.create((ObservableOnSubscribe<List<IDCardRecord>>) emitter -> {
            List<IDCardRecord> idCardRecordList = IDCardRecordRepository.getInstance().loadAll();
            emitter.onNext(idCardRecordList);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mIdCardRecordList -> {
                    idCardRecordList = mIdCardRecordList;
                    waitMessage.setValue("");
                    loadCardResult.setValue(Boolean.TRUE);
                }, throwable -> {
                    waitMessage.setValue("");
                    toast.setValue(ToastManager.getToastBody("", ToastManager.INFO));
                });
    }

    public List<Local> getLocalList() {
        List<Local> value = localList.getValue();
        if (value == null) {
            List<Local> newArrayList = new ArrayList<>();
            localList.setValue(newArrayList);
            return newArrayList;
        } else {
            return value;
        }
    }

    public IDCardRecord findIDCardRecordByVerifyId(String verifyId) {
        if (idCardRecordList == null) return null;
        for (IDCardRecord idCardRecord : idCardRecordList) {
            if (TextUtils.equals(idCardRecord.getVerifyId(), verifyId)) {
                return idCardRecord;
            }
        }
        return null;
    }

    public void loadExpressByPage(int pageNum) {
        Disposable disposable = Observable.create((ObservableOnSubscribe<List<Local>>) emitter -> {
            List<Express> recordList = ExpressRepository.getInstance().loadRecordByPage(pageNum, 10);
            List<Local> mLocalList = new ArrayList<>();
            for (Express express : recordList) {
                Local local = new Local(express, findIDCardRecordByVerifyId(express.getVerifyId()));
                mLocalList.add(local);
            }
            emitter.onNext(mLocalList);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mLocalList -> {
                    refreshing.setValue(Boolean.FALSE);
                    if (pageNum == 1) {
                        if (mLocalList.isEmpty()) {
                            toast.setValue(ToastManager.getToastBody("本地暂无缓存日志", ToastManager.SUCCESS));
                        }
                        localList.setValue(mLocalList);
                    } else {
                        if (mLocalList.isEmpty()) {
                            toast.setValue(ToastManager.getToastBody("没有更多了", ToastManager.SUCCESS));
                        }
                        List<Local> localListCache = getLocalList();
                        localListCache.addAll(mLocalList);
                        localList.setValue(localListCache);
                    }
                }, throwable -> {
                    refreshing.setValue(Boolean.FALSE);
                    resultMessage.setValue(handleError(throwable));
                });
    }

}
