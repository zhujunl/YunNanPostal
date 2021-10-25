package com.miaxis.postal.viewModel;


import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.AppEntity;
import com.miaxis.postal.data.repository.AppInstallRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AppDownloadViewModel extends BaseViewModel {
    public MutableLiveData<List<AppEntity.DataBean>> appInstalllist = new MutableLiveData<>(new ArrayList<>());

    public AppDownloadViewModel() {

    }
    public List<AppEntity.DataBean> getOrderList() {
        List<AppEntity.DataBean> value = appInstalllist.getValue();
        if (value == null) {
            List<AppEntity.DataBean> newArrayList = new ArrayList<>();
            appInstalllist.setValue(newArrayList);
            return newArrayList;
        } else {
            return value;
        }
    }

    public void appinstall() {
        Observable.create((ObservableOnSubscribe<List<AppEntity.DataBean>>) emitter -> {
            List<AppEntity.DataBean> simpleOrderList = AppInstallRepository.getInstance().getAppitem();
            emitter.onNext(simpleOrderList);
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(appitem -> {
                    List<AppEntity.DataBean> LocalappInstallList = getOrderList();
                    LocalappInstallList.addAll(appitem);
                    appInstalllist.setValue(LocalappInstallList);
                },throwable -> {
                    appInstalllist.setValue(null);
                });
    }

    //    public boolean isAppInstalled(Context context, AppItem appItem) {
    //        if (context == null || TextUtils.isEmpty(appItem.AppName) || TextUtils.isEmpty(appItem.AppVersion)) {
    //            return false;
    //        }
    //        PackageManager pm = context.getPackageManager();
    //        List<PackageInfo> packages = pm.getInstalledPackages(0);
    //        for (PackageInfo packageInfo : packages) {
    //            // 判断系统/非系统应用
    //            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
    //            {
    //                System.out.println("getAppList, packageInfo=" + packageInfo.packageName);
    //            } else {
    //                // 系统应用
    //            }
    //        }
    //    }


}
