package com.miaxis.postal.viewModel;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.Local;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.repository.ExpressRepository;
import com.miaxis.postal.data.repository.IDCardRecordRepository;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.manager.ToastManager;

import java.io.File;
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
            List<IDCardRecord> idCardRecordList = IDCardRecordRepository.getInstance().loadAllNotDraft();
            emitter.onNext(idCardRecordList);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
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
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mLocalList -> {
                    refreshing.setValue(Boolean.FALSE);
                    if (pageNum == 1) {
                        if (mLocalList.isEmpty()) {
//                            toast.setValue(ToastManager.getToastBody("本地暂无缓存日志", ToastManager.SUCCESS));
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

    public void deleteSelf( IDCardRecord idCardRecord, Express express) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            ExpressRepository expressRepository = ExpressRepository.getInstance();
            ExpressRepository.getInstance().deleteExpress(express);

            if (idCardRecord!=null) {
                if (!TextUtils.isEmpty(idCardRecord.getWebCardPath()) && !TextUtils.isEmpty(idCardRecord.getWebFacePath())) {
                    List<String> webPath = new ArrayList<>();
                    webPath.add(idCardRecord.getWebCardPath());
                    webPath.add(idCardRecord.getWebFacePath());
                    for (String path : webPath) {
                        expressRepository.deleteWebPicture(path);
                    }
                }
                IDCardRecordRepository.getInstance().deleteIDCardRecord(idCardRecord);
            }
            List<Local> value = localList.getValue();
            List<Local> locals = new ArrayList<>();
            if (value != null && !value.isEmpty()) {
                for (Local local : value) {
                    Express express1 = local.getExpress();
                    if (express1 == null) {
                        continue;
                    }
                    if (express1.getId().longValue() != express.getId().longValue()) {
                        locals.add(local);
                    }
                }
            }
            localList.postValue(locals);
            emitter.onNext(Boolean.TRUE);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {


                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

}
