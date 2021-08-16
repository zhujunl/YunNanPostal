package com.miaxis.postal.view.fragment;

import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.liulishuo.filedownloader.FileDownloader;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.AppItem;
import com.miaxis.postal.databinding.FragmentAppDownloadBinding;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.NetUtils;
import com.miaxis.postal.view.adapter.AppListAdapter;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.presenter.DownloadPresenter;
import com.miaxis.postal.view.presenter.download.DownloadManager;
import com.miaxis.postal.viewModel.AppDownloadViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class AppDownloadFragment extends BaseViewModelFragment<FragmentAppDownloadBinding, AppDownloadViewModel> implements AppListAdapter.OnDownloadClickListener {

    public static AppDownloadFragment newInstance() {
        return new AppDownloadFragment();
    }

    public AppDownloadFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_app_download;
    }

    @Override
    protected AppDownloadViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(AppDownloadViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        AppListAdapter appListAdapter = new AppListAdapter();
        appListAdapter.setOnDownloadClickListener(this);
        binding.rvApps.setLayoutManager(linearLayoutManager);
        binding.rvApps.setAdapter(appListAdapter);

        List<AppItem> objects = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            AppItem appItem = new AppItem();
            appItem.AppName = "APP" + i;
            appItem.AppVersion = "1.0." + i;
            appItem.AppUrl = "http://192.168.5.104:8088/downloadfile/FaceMatchDemo.apk?path=/FaceMatchDemo.apk";
            appItem.AppLocalPath = DownloadPresenter.AppPath(appItem);
            objects.add(appItem);
        }
        appListAdapter.setDataList(objects);
    }

    @Override
    public void onDownloadClick(AppListAdapter.BodyViewHolder view, AppItem appItem, int position) {
        DownloadPresenter download = download(view, appItem, position);
        download.showUpdateDialog(appItem);
    }

    @Override
    public void onInstallClick(AppListAdapter.BodyViewHolder view, AppItem appItem, int position) {
        new MaterialDialog.Builder(getContext())
                .title("APP安装")
                .content("确认安装【" + appItem.AppName + "】，版本：" + appItem.AppVersion + " ？")
                .positiveText("安装")
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    if (!TextUtils.isEmpty(appItem.AppUrl)) {
                        new DownloadPresenter(getContext()).downloadSuccess(appItem.AppLocalPath);
                    } else {
                        ToastManager.toast("文件路径为空", ToastManager.INFO);
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

    @Override
    public void onDeleteClick(AppListAdapter.BodyViewHolder view, AppItem appItem, int position) {
        if (appItem != null) {
            new MaterialDialog.Builder(getContext())
                    .title("APP重新下载")
                    .content("确认重新下载【" + appItem.AppName + "】 ？"+ (NetUtils.getNetStatus(getContext()) == 0 ? ("\n下载类型：【手机流量】") : ""))
                    .positiveText("确认")
                    .onPositive((dialog, which) -> {
                        dialog.dismiss();
                        try {
                            boolean delete = new File(appItem.AppLocalPath).delete();
                            view.getBinding().setItem(appItem);
                            DownloadPresenter download = download(view, appItem, position);
                            download.downloadUrl(appItem);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showResultDialog("操作失败，" + e);
                        }
                    })
                    .negativeText("取消")
                    .onNegative((dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        }
    }

    private DownloadPresenter download(AppListAdapter.BodyViewHolder view, AppItem appItem, int position) {
        return new DownloadPresenter(getContext(), new DownloadPresenter.OnDownloadListener() {
            @Override
            public void onProgress(int progress) {
                view.getBinding().cpbProgress.setProgress(progress);
            }

            @Override
            public void onDownloadResult(boolean result, String message) {
                view.getBinding().cpbProgress.setProgress(result ? 100 : 0);
                if (result) {
                    appItem.AppLocalPath = DownloadPresenter.AppPath(appItem);
                    view.getBinding().setItem(appItem);
                    onInstallClick(view, appItem, position);
                } else {
                    showResultDialog(message);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

    @Override
    public void onDestroyView() {
        FileDownloader.getImpl().pauseAll();
        DownloadManager.getInstance().close();
        super.onDestroyView();
    }

}
