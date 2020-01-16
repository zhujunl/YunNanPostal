package com.miaxis.postal.view.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.databinding.FragmentFingerVerifyDialogBinding;
import com.miaxis.postal.view.base.BaseViewModelDialogFragment;
import com.miaxis.postal.viewModel.FingerVerifyViewModel;

import java.util.List;

public class FingerVerifyDialogFragment extends BaseViewModelDialogFragment<FragmentFingerVerifyDialogBinding, FingerVerifyViewModel> {

    private MaterialDialog retryDialog;

    private List<String> featureList;
    private OnFingerVerifyListener listener;

    public static FingerVerifyDialogFragment newInstance(List<String> featureList, OnFingerVerifyListener listener) {
        FingerVerifyDialogFragment fragment = new FingerVerifyDialogFragment();
        fragment.setFeatureList(featureList);
        fragment.setListener(listener);
        return fragment;
    }

    public FingerVerifyDialogFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_finger_verify_dialog;
    }

    @Override
    protected FingerVerifyViewModel initViewModel() {
        return ViewModelProviders.of(this).get(FingerVerifyViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.initFingerResult.observe(this, fingerInitObserver);
        viewModel.fingerResultFlag.observe(this, result -> {
            dismiss();
            listener.verifyResult(result);
        });
    }

    @Override
    protected void initView() {
        if (retryDialog != null && retryDialog.isShowing()) {
            retryDialog.dismiss();
        }
        viewModel.initFingerDevice();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.releaseFingerDevice();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable( new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );
        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.BOTTOM;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width =  ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
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
                            dismiss();
                        })
                        .autoDismiss(false)
                        .show();
                break;
            case LOADING:
                mListener.showWaitDialog("正在初始化指纹模块");
                break;
            case SUCCESS:
                mListener.dismissWaitDialog();
                viewModel.verifyFinger(featureList);
                break;
        }
    };

    public void setFeatureList(List<String> featureList) {
        this.featureList = featureList;
    }

    public void setListener(OnFingerVerifyListener listener) {
        this.listener = listener;
    }

    public interface OnFingerVerifyListener {
        void verifyResult(boolean result);
    }

}
