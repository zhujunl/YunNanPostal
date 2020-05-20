package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.os.Looper;
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

import java.util.Date;

public class FingerVerifyFragment extends BaseViewModelFragment<FragmentFingerVerifyBinding, FingerVerifyViewModel> {

    private MaterialDialog retryDialog;

    private IDCardRecord idCardRecord;

    private Handler handler;
    private int delay = 21;

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
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(FingerVerifyViewModel.class);
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
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.tvSwitch.setOnClickListener(new OnLimitClickHelper(view -> {
            mListener.replaceFragment(FaceVerifyFragment.newInstance(idCardRecord));
        }));
        handler.post(countDownRunnable);
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
        handler.removeCallbacks(countDownRunnable);
        mListener.dismissWaitDialog();
        if (!pass) {
            TTSManager.getInstance().stop();
        }
        viewModel.releaseFingerDevice();
    }

    private Runnable countDownRunnable = new Runnable() {
        @Override
        public void run() {
            delay--;
            viewModel.countDown.set(delay + " S");
            if (delay <= 0) {
                onBackPressed();
            } else {
                handler.postDelayed(countDownRunnable, 1000);
            }
        }
    };

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
            binding.ivBack.setEnabled(false);
            binding.tvSwitch.setEnabled(false);
            handler.postDelayed(() -> {
                try {
                    idCardRecord.setVerifyTime(new Date());
                    mListener.replaceFragment(ExpressFragment.newInstance(idCardRecord));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 1000);
        }
    };

    public void setIdCardRecord(IDCardRecord idCardRecord) {
        this.idCardRecord = idCardRecord;
    }
}
