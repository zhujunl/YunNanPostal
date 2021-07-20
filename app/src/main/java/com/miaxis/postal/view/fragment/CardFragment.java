package com.miaxis.postal.view.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.databinding.FragmentCardBinding;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.CardViewModel;

public class CardFragment extends BaseViewModelFragment<FragmentCardBinding, CardViewModel> {

    private MaterialDialog retryDialog;

    //是否协议客户
    private  boolean isAgreementCustomer=false;

    public static CardFragment newInstance() {
        return new CardFragment();
    }

    public static CardFragment newInstance(boolean isAgreementCustomer) {
        CardFragment cardFragment=    new CardFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean("agreementCustomer",isAgreementCustomer);
        cardFragment.setArguments(bundle);
        return cardFragment;
    }

    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            isAgreementCustomer=getArguments().getBoolean("agreementCustomer",false);
        }
    }

    public CardFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_card;
    }

    @Override
    protected CardViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(CardViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.initCardResult.observe(this, initCardResultObserver);
        viewModel.readCardFlag.observe(this, readCardFlagObserver);
        viewModel.saveFlag.observe(this, saveFlagObserver);
    }

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.fabAlarm.setOnLongClickListener(alarmListener);
    }

    @Override
    public void onBackPressed() {
        if (retryDialog != null) {
            retryDialog.dismiss();
        }
        mListener.backToStack(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (retryDialog != null && retryDialog.isShowing()) {
            retryDialog.dismiss();
        }
        viewModel.startReadCard();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.stopReadCard();
    }

    private Observer<Status> initCardResultObserver = status -> {
        switch (status) {
            case FAILED:
                mListener.dismissWaitDialog();
                retryDialog = new MaterialDialog.Builder(getContext())
                        .title("初始化身份证阅读器失败，是否重试？")
                        .positiveText("重试")
                        .onPositive((dialog, which) -> {
                            viewModel.startReadCard();
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
                mListener.showWaitDialog("正在初始化身份证阅读器");
                break;
            case SUCCESS:
                mListener.dismissWaitDialog();
                break;
        }
    };

    private Observer<Boolean> readCardFlagObserver = flag -> {
        mListener.replaceFragment(FaceVerifyFragment.newInstance(viewModel.getIdCardRecord(),isAgreementCustomer));
    };

    private Observer<Boolean> saveFlagObserver = flag -> mListener.backToStack(HomeFragment.class);

    private View.OnLongClickListener alarmListener = v -> {
        viewModel.alarm();
        return false;
    };

}
