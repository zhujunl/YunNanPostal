package com.miaxis.postal.viewModel;


import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.AppEntity;
import com.miaxis.postal.data.repository.AppInstallRepository;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AppDownloadViewModel extends BaseViewModel {
    public MutableLiveData<List<AppEntity.DataBean>> appInstallList = new MutableLiveData<>(new ArrayList<>());

    public AppDownloadViewModel() {

    }

    public void getAppList() {
        waitMessage.setValue("正在请求中，请稍后");
        Observable.create((ObservableOnSubscribe<List<AppEntity.DataBean>>) emitter -> {
            List<AppEntity.DataBean> appItemList = AppInstallRepository.getInstance().getAppitem();
            emitter.onNext(appItemList);
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(appItem -> {
                    waitMessage.setValue(null);
//                    List<AppEntity.DataBean> LocalAppInstallList = getItemList();
//                    LocalAppInstallList.addAll(appItem);
                    appInstallList.setValue(appItem);
                }, throwable -> {
                    waitMessage.setValue(null);
                    resultMessage.setValue("" + throwable.getMessage());
                    appInstallList.setValue(null);
                });
    }

    public List<AppEntity.DataBean> getItemList() {
        List<AppEntity.DataBean> value = appInstallList.getValue();
        if (value == null) {
            List<AppEntity.DataBean> newArrayList = new ArrayList<>();
            appInstallList.setValue(newArrayList);
            return newArrayList;
        } else {
            return value;
        }
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
