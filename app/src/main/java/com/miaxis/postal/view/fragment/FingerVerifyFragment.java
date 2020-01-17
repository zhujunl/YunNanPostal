package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.databinding.FragmentFingerVerifyBinding;
import com.miaxis.postal.manager.TTSManager;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelDialogFragment;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.FingerVerifyViewModel;

public class FingerVerifyFragment extends BaseViewModelFragment<FragmentFingerVerifyBinding, FingerVerifyViewModel> {

    private MaterialDialog retryDialog;

    private IDCardRecord idCardRecord;

    private boolean pass = false;

    public static FingerVerifyFragment newInstance(IDCardRecord idCardRecord) {
        FingerVerifyFragment fragment = new FingerVerifyFragment();
        fragment.setIdCardRecord(idCardRecord);
        return fragment;
    }

    public FingerVerifyFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_finger_verify;
    }

    @Override
    protected FingerVerifyViewModel initViewModel() {
        return ViewModelProviders.of(this).get(FingerVerifyViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.idCardRecordLiveData.setValue(idCardRecord);
        viewModel.initFingerResult.observe(this, fingerInitObserver);
        viewModel.fingerResultFlag.observe(this, fingerResultFlagObserver);
    }

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.tvSwitch.setOnClickListener(new OnLimitClickHelper(view -> {
            mListener.replaceFragment(FaceVerifyFragment.newInstance(idCardRecord));
        }));
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
        mListener.backToStack(HomeFragment.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener.dismissWaitDialog();
        if (!pass) {
            TTSManager.getInstance().stop();
        }
        viewModel.releaseFingerDevice();
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
                            mListener.backToStack(HomeFragment.class);
                        })
                        .autoDismiss(false)
                        .show();
                break;
            case LOADING:
                mListener.showWaitDialog("正在初始化指纹模块");
                break;
            case SUCCESS:
                mListener.dismissWaitDialog();
                TTSManager.getInstance().playVoiceMessageAdd("请按" + idCardRecord.getFingerprintPosition0() + "或" + idCardRecord.getFingerprintPosition1());
                viewModel.verifyFinger();
                break;
        }
    };

    private Observer<Boolean> fingerResultFlagObserver = flag -> {
        if (flag) {
            pass = true;
            TTSManager.getInstance().playVoiceMessageFlush("核验通过");
            mListener.replaceFragment(ExpressFragment.newInstance(idCardRecord));
        }
    };

    public void setIdCardRecord(IDCardRecord idCardRecord) {
        this.idCardRecord = idCardRecord;
    }
}
