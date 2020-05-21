package com.miaxis.postal.view.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Update;
import com.miaxis.postal.data.repository.DeviceRepository;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.DateUtil;
import com.miaxis.postal.util.FileUtil;

import java.io.File;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class UpdatePresenter {

    private Context context;

    private MaterialDialog updateDialog;
    private MaterialDialog downloadProgressDialog;

    public UpdatePresenter(Context context) {
        this.context = context;
    }

    public void checkUpdate() {
        Observable.create((ObservableOnSubscribe<Update>) emitter -> {
            Update update = DeviceRepository.getInstance().updateApp();
            emitter.onNext(update);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(update -> {
                    showUpdateDialog(update);
                }, throwable -> {
                    throwable.printStackTrace();
                    ToastManager.toast("查询更新信息失败", ToastManager.INFO);
                    Log.e("asd", "更新App错误：" + throwable.getMessage());
                });
    }

    private void showUpdateDialog(Update update) {
        if (updateDialog != null && updateDialog.isShowing()) {
            updateDialog.dismiss();
        }
        updateDialog = new MaterialDialog.Builder(context)
                .title("检查更新")
                .content("")
                .positiveText("更新")
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    if (!TextUtils.isEmpty(update.getUrl())) {
                        initProgressDialog();
                        downloadUrl(update.getUrl());
                    } else {
                        ToastManager.toast("更新文件下载路径为空", ToastManager.INFO);
                    }
                })
                .negativeText("取消")
                .onNegative((dialog, which) -> {
                    dialog.dismiss();
                })
                .autoDismiss(false)
                .cancelable(false)
                .show();
    }

    private void initProgressDialog() {
        downloadProgressDialog = new MaterialDialog.Builder(context)
                .title("下载进度")
                .progress(false, 100)
                .positiveText("取消")
                .onPositive((dialog, which) -> FileDownloader.getImpl().pauseAll())
                .cancelable(false)
                .show();
    }

    private void downloadUrl(String url) {
        String path = FileUtil.MAIN_PATH + File.separator + "实名核验_" + DateUtil.YEAR_MONTH_DAY_FORMAT.format(new Date()) + ".apk";
        FileDownloader.getImpl().create(url)
                .setPath(path)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }
                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    }
                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        int percent = (int) ((double) soFarBytes / (double) totalBytes * 100);
                        if (downloadProgressDialog.isShowing()) {
                            downloadProgressDialog.setProgress(percent);
                        }
                    }
                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }
                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        if (downloadProgressDialog.isShowing()) {
                            downloadProgressDialog.dismiss();
                        }
                        downloadSuccess(path);
                    }
                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        if (downloadProgressDialog.isShowing()) {
                            downloadProgressDialog.dismiss();
                        }
                        ToastManager.toast("下载已取消", ToastManager.INFO);
                    }
                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        if (downloadProgressDialog.isShowing()) {
                            downloadProgressDialog.dismiss();
                        }
                        ToastManager.toast("下载更新文件失败：\n" + e.getMessage(), ToastManager.ERROR);
                    }
                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                }).start();
    }

    private void downloadSuccess(String path) {
        File file = new File(path);
        if (file.exists()) {
            installApk(file);
        } else {
            ToastManager.toast("未找到更新文件，请尝试手动更新", ToastManager.INFO);
        }
    }

    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
