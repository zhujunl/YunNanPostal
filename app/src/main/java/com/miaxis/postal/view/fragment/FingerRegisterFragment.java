package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.bridge.GlideApp;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.databinding.FragmentFingerRegisterBinding;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.FingerRegisterViewModel;

public class FingerRegisterFragment extends BaseViewModelFragment<FragmentFingerRegisterBinding, FingerRegisterViewModel> {

    private MaterialDialog retryDialog;

    private String mark;

    public static FingerRegisterFragment newInstance(String mark) {
        FingerRegisterFragment fragment = new FingerRegisterFragment();
        fragment.setMark(mark);
        return fragment;
    }

    public FingerRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_finger_register;
    }

    @Override
    protected FingerRegisterViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(FingerRegisterViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.mark = mark;
        viewModel.initFingerResult.observe(this, fingerInitObserver);
        viewModel.fingerResultFlag.observe(this, result -> mListener.backToStack(null));
        viewModel.fingerImageUpdate.observe(this, fingerImageUpdateObserver);
    }

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (retryDialog != null && retryDialog.isShowing()) {
            retryDialog.dismiss();
        }
        viewModel.initFingerDevice();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.releaseFingerDevice();
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(getContext())
                .title("确认退出？")
                .content("退出将放弃当前进度")
                .positiveText("确认")
                .onPositive((dialog, which) -> mListener.backToStack(null))
                .negativeText("取消")
                .show();
    }

    private Observer<Status> fingerInitObserver = status -> {
        switch (status) {
            case FAILED:
                mListener.dismissWaitDialog();
                retryDialog = new MaterialDialog.Builder(getContext())
                        .title("初始化指纹模块失败，是否重试？")
                        .positiveText("重试")
                        .onPositive((dialog, which) -> {
                            viewModel.initFingerDevice();
                            dialog.dismiss();
                        })
                        .negativeText("退出")
                        .onNegative((dialog, which) -> {
                            dialog.dismiss();
                            onBackPressed();
                        })
                        .autoDismiss(false)
                        .show();
                break;
            case LOADING:
                mListener.showWaitDialog("正在初始化指纹模块");
                break;
            case SUCCESS:
                mListener.dismissWaitDialog();
                viewModel.registerFeature();
                break;
        }
    };

    private Observer<Boolean> fingerImageUpdateObserver = update -> {
        GlideApp.with(this)
                .load(viewModel.fingerImageCache)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.ivFinger);
    };

    public void setMark(String mark) {
        this.mark = mark;
    }
}
