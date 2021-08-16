package com.miaxis.postal.viewModel;


public class AppDownloadViewModel extends BaseViewModel {


    public AppDownloadViewModel() {

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
