package com.miaxis.postal.view.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.liulishuo.filedownloader.FileDownloader;
import com.miaxis.postal.data.entity.AppEntity;
import com.miaxis.postal.data.entity.AppItem;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.FileUtil;
import com.miaxis.postal.util.NetUtils;
import com.miaxis.postal.view.presenter.download.DownloadCallback;
import com.miaxis.postal.view.presenter.download.DownloadManager;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

public class DownloadPresenter {

    private Context context;
    private OnDownloadListener mOnDownloadListener;

    private MaterialDialog updateDialog;
    private MaterialDialog downloadProgressDialog;
    private Handler mHandler = new Handler();

    public DownloadPresenter(Context context) {
        this.context = context;
    }

    public DownloadPresenter(Context context, OnDownloadListener onDownloadListener) {
        this.context = context;
        this.mOnDownloadListener = onDownloadListener;
    }

    public interface OnDownloadListener {
        void onProgress(int progress);

        void onDownloadResult(boolean result, String message);
    }

    public void showUpdateDialog(@NonNull AppEntity.DataBean appItem) {
        if (updateDialog != null && updateDialog.isShowing()) {
            updateDialog.dismiss();
        }
        updateDialog = new MaterialDialog.Builder(context)
                .title("APP下载")
                .content("确认下载【" + appItem.name + "】，版本：" + appItem.version + " ？" + (NetUtils.getNetStatus(context) == 0 ? ("\n下载类型：【手机流量】") : ""))
                .positiveText("下载")
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    if (!TextUtils.isEmpty(appItem.url)) {
                        downloadUrl(appItem);
                    } else {
                        ToastManager.toast("文件下载路径为空", ToastManager.INFO);
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
                .onPositive((dialog, which) -> {
                    FileDownloader.getImpl().pauseAll();
                    DownloadManager.getInstance().close();
                })
                .cancelable(false).build();
    }

    public static String AppPath(@NonNull AppEntity.DataBean appItem) {
        return FileUtil.APP_PATH + File.separator + appItem.name + "-" + appItem.version + ".apk";
    }

    public void downloadUrl(@NonNull AppEntity.DataBean appItem) {
        initProgressDialog();
        String path = AppPath(appItem);
        DownloadManager.getInstance().downloadFile(appItem.url, path, new DownloadCallback<AppEntity.DataBean>(appItem) {
            @Override
            public void onDownloadStart(AppEntity.DataBean data) {
                if (mHandler != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (downloadProgressDialog != null) {
                                downloadProgressDialog.show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onDownloadProgress(AppEntity.DataBean data, int total, int progress) {
                if (mHandler != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            int percent = (int) ((progress * 1F) / (total * 1F) * 100);
                            if (downloadProgressDialog != null && downloadProgressDialog.isShowing()) {
                                downloadProgressDialog.setProgress(percent);
                            }
                            if (mOnDownloadListener != null) {
                                mOnDownloadListener.onProgress(percent);
                            }
                        }
                    });
                }
            }

            @Override
            public void onDownloadStop(AppEntity.DataBean data, int error, String msg) {
                if (mHandler != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (downloadProgressDialog != null &&downloadProgressDialog.isShowing()) {
                                downloadProgressDialog.dismiss();
                            }
                            if (mOnDownloadListener != null) {
                                mOnDownloadListener.onDownloadResult(error == 0, msg);
                            }
                        }
                    });
                }
            }
        });

        //        FileDownloader.getImpl().create(appItem.AppUrl)
        //                .setPath(path)
        //                .setListener(new FileDownloadListener() {
        //                    @Override
        //                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        //                    }
        //
        //                    @Override
        //                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
        //                    }
        //
        //                    @Override
        //                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        //                        Log.e("downloadUrl", "totalBytes:" + totalBytes + "    soFarBytes:" + soFarBytes);
        //                        int percent = (int) (soFarBytes / totalBytes * 1F * 100);
        //                        if (downloadProgressDialog != null && downloadProgressDialog.isShowing()) {
        //                            downloadProgressDialog.setProgress(percent);
        //                        }
        //                        if (mOnDownloadListener != null) {
        //                            mOnDownloadListener.onProgress(percent);
        //                        }
        //                    }
        //
        //                    @Override
        //                    protected void blockComplete(BaseDownloadTask task) {
        //                    }
        //
        //                    @Override
        //                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
        //                    }
        //
        //                    @Override
        //                    protected void completed(BaseDownloadTask task) {
        //                        if (downloadProgressDialog != null && downloadProgressDialog.isShowing()) {
        //                            downloadProgressDialog.dismiss();
        //                        }
        //                        if (mOnDownloadListener != null) {
        //                            mOnDownloadListener.onDownloadResult(true, null);
        //                        }
        //                        appItem.AppLocalPath = path;
        //                        downloadSuccess(path);
        //                    }
        //
        //                    @Override
        //                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        //                        if (downloadProgressDialog.isShowing()) {
        //                            downloadProgressDialog.dismiss();
        //                        }
        //                        if (mOnDownloadListener != null) {
        //                            mOnDownloadListener.onDownloadResult(false, null);
        //                        }
        //                        ToastManager.toast("下载已取消", ToastManager.INFO);
        //                    }
        //
        //                    @Override
        //                    protected void error(BaseDownloadTask task, Throwable e) {
        //                        if (downloadProgressDialog != null && downloadProgressDialog.isShowing()) {
        //                            downloadProgressDialog.dismiss();
        //                        }
        //                        if (mOnDownloadListener != null) {
        //                            mOnDownloadListener.onDownloadResult(false, null);
        //                        }
        //                        ToastManager.toast("下载文件失败：\n" + e.getMessage(), ToastManager.ERROR);
        //                    }
        //
        //                    @Override
        //                    protected void warn(BaseDownloadTask task) {
        //                    }
        //                }).start();
    }

    public void downloadSuccess(String path) {
        File file = new File(path);
        Log.i("TAG更新地址", "" + path);
        if (file.exists()) {
            installApk(file);
        } else {
            ToastManager.toast("未找到安装文件", ToastManager.INFO);
        }
    }

    private void installApk(File file) {
        Intent installApkIntent = new Intent();
        installApkIntent.setAction(Intent.ACTION_VIEW);
        installApkIntent.addCategory(Intent.CATEGORY_DEFAULT);
        installApkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            installApkIntent.setDataAndType(
                    FileProvider.getUriForFile(context, "com.miaxis.postal.provider", file), "application/vnd.android.package-archive");
            installApkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            installApkIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(installApkIntent);
    }

}
