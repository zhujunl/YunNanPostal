package com.miaxis.postal.view.fragment;

import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.liulishuo.filedownloader.FileDownloader;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.AppItem;
import com.miaxis.postal.databinding.FragmentAppDownloadBinding;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.view.adapter.AppListAdapter;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.presenter.DownloadPresenter;
import com.miaxis.postal.view.presenter.download.DownloadManager;
import com.miaxis.postal.viewModel.AppDownloadViewModel;

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

    private final String BaseDownloadUrl = "http://14.205.75.23:8888/";

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        AppListAdapter appListAdapter = new AppListAdapter();
        appListAdapter.setOnDownloadClickListener(this);
        binding.rvApps.setLayoutManager(linearLayoutManager);
        binding.rvApps.setAdapter(appListAdapter);

        List<AppItem> objects = new ArrayList<>();
        objects.add(new AppItem("掌中通", "6.5.1", BaseDownloadUrl + "group1/M00/00/7B/wKgBEmEc-eqAEu4NBaOjPbbQ6Zs2.1.apk"));
        objects.add(new AppItem("韵镖侠", "7.0.2.1", BaseDownloadUrl + "group1/M00/00/7B/wKgBEmEc-RGAbAmXCp_MH1M1OuY9.1.apk"));
        objects.add(new AppItem("邦小哥", "0.1.4.07", BaseDownloadUrl + "group1/M00/00/7B/wKgBEmEc-SKABFxTBnVWmU_1-q4519.apk"));
        objects.add(new AppItem("如来神掌", "6.14.1", BaseDownloadUrl + "group1/M00/00/7B/wKgBEmEc-S6AF6tNBF5pGjogRhE293.apk"));
        objects.add(new AppItem("菜鸟包裹侠", "6.57.0", BaseDownloadUrl + "group1/M00/00/7B/wKgBEmEc-UeAAF_gB_bA9bn2wWU029.apk"));
        objects.add(new AppItem("快宝快递员", "8.5.0", BaseDownloadUrl + "group1/M00/00/7B/wKgBEmEc-VaAXHTnBUN8o8meoZ41.0.apk"));
        objects.add(new AppItem("外场pro", "1.0.89", BaseDownloadUrl + "group1/M00/00/7B/wKgBEmEc-WSASuBVAi-UuiFCtsc154.apk"));
        objects.add(new AppItem("顺丰丰源", "1.6.5", BaseDownloadUrl + "group1/M00/00/7B/wKgBEmEc-XKAByG5B-jvyfNDrT87.5.apk"));
        objects.add(new AppItem("中邮处理", "9.9.6", BaseDownloadUrl + "group1/M00/00/7B/wKgBEmEc-gmATx10AKiOtJn5v8c7.6.apk"));
        objects.add(new AppItem("小哥工作台", "21.10.58", BaseDownloadUrl + "group1/M00/00/7B/wKgBEmEc-YSAar3zAyUnVH2UCiU.58.apk"));
        objects.add(new AppItem("优速宝", "1.0.7", BaseDownloadUrl + "group1/M00/00/7B/wKgBEmEc80WAXjmKACP6VpMpFSI8.7.apk"));
        objects.add(new AppItem("安易递收寄版", "1.4.4", BaseDownloadUrl + "group1/M00/00/7B/wKgBEmEc-I-ACCUQAjedgMOrVws784.apk"));
        objects.add(new AppItem("菜鸟点我达", "7.3.19.4", BaseDownloadUrl + "group1/M00/00/7B/wKgBEmEc-LeAKJhcBZrCrVB_D709.4.apk"));

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

    //    @Override
    //    public void onDeleteClick(AppListAdapter.BodyViewHolder view, AppItem appItem, int position) {
    //        if (appItem != null) {
    //            new MaterialDialog.Builder(getContext())
    //                    .title("APP重新下载")
    //                    .content("确认重新下载【" + appItem.AppName + "】 ？" + (NetUtils.getNetStatus(getContext()) == 0 ? ("\n下载类型：【手机流量】") : ""))
    //                    .positiveText("确认")
    //                    .onPositive((dialog, which) -> {
    //                        dialog.dismiss();
    //                        try {
    //                            boolean delete = new File(appItem.AppLocalPath).delete();
    //                            view.getBinding().setItem(appItem);
    //                            DownloadPresenter download = download(view, appItem, position);
    //                            download.downloadUrl(appItem);
    //                        } catch (Exception e) {
    //                            e.printStackTrace();
    //                            showResultDialog("操作失败，" + e);
    //                        }
    //                    })
    //                    .negativeText("取消")
    //                    .onNegative((dialog, which) -> {
    //                        dialog.dismiss();
    //                    })
    //                    .show();
    //        }
    //    }

    private DownloadPresenter download(AppListAdapter.BodyViewHolder view, AppItem appItem, int position) {
        return new DownloadPresenter(getContext(), new DownloadPresenter.OnDownloadListener() {
            @Override
            public void onProgress(int progress) {
                view.getBinding().cpbProgress.setProgress(progress);
            }

            @Override
            public void onDownloadResult(boolean result, String message) {
                if (result) {
                    appItem.AppLocalPath = DownloadPresenter.AppPath(appItem);
                    onInstallClick(view, appItem, position);
                } else {
                    showResultDialog(message);
                }
                view.getBinding().setItem(appItem);
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
