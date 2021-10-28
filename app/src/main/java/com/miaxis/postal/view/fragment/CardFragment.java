package com.miaxis.postal.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.Customer;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.databinding.FragmentCardBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.CardViewModel;
import com.miaxis.postal.viewModel.LoginViewModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class CardFragment extends BaseViewModelFragment<FragmentCardBinding, CardViewModel> {

    private MaterialDialog retryDialog;
    private Customer mCustomer;
    //是否协议客户
    private boolean isAgreementCustomer = false;

    public static CardFragment newInstance() {
        return new CardFragment();
    }

    public static CardFragment newInstance(boolean isAgreementCustomer, Customer customer) {
        CardFragment cardFragment = new CardFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("agreementCustomer", isAgreementCustomer);
        cardFragment.setArguments(bundle);
        cardFragment.setCustomer(customer);
        return cardFragment;
    }

    public void setCustomer(Customer customer) {
        mCustomer = customer;
    }

    private Handler deviceHandler = new Handler();
    private Runnable task =new Runnable() {
        public void run() {
            // TODOAuto-generated method stub
            deviceHandler.postDelayed(this,10*1000);//设置延迟时间
            //需要执行的代码
            //获取设备状态,判断设备状态是启用还是禁用
            LoginViewModel loginViewModel = new LoginViewModel();
            Config config = ConfigManager.getInstance().getConfig();
            loginViewModel.getDevices(config.getDeviceIMEI());
            loginViewModel.deviceslist.observe(getActivity(), new Observer<DevicesStatusEntity.DataDTO>() {
                @Override
                public void onChanged(DevicesStatusEntity.DataDTO dataDTO) {
                    //如果是启用状态不做任何操作
                    if (dataDTO.getStatus().equals(ValueUtil.DEVICE_ENABLE)){

                    }else {
                        //如果从启用状态切换到了禁用状态强制退出登录跳到登录页面
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.card_devices, new LoginFragment(), null)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isAgreementCustomer = getArguments().getBoolean("agreementCustomer", false);
        }

        //进入延时状态,一小时访问一次接口
        deviceHandler.postDelayed(task,3600000);//延迟调用
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
        mListener.replaceFragment(FaceVerifyFragment.newInstance(viewModel.getIdCardRecord(), isAgreementCustomer, mCustomer));
    };

    private Observer<Boolean> saveFlagObserver = flag -> mListener.backToStack(HomeFragment.class);

    private View.OnLongClickListener alarmListener = v -> {
        //viewModel.alarm();
        return false;
    };

}
