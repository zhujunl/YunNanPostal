package com.miaxis.postal.view.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.databinding.FragmentLoginBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.dialog.FingerVerifyDialogFragment;
import com.miaxis.postal.viewModel.LoginViewModel;

import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends BaseViewModelFragment<FragmentLoginBinding, LoginViewModel> {

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_login;
    }

    @Override
    protected LoginViewModel initViewModel() {
        return ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.loginFaceFlag.observe(this, loginFaceFlagObserver);
        viewModel.loginFingerFlag.observe(this, loginFingerFlagObserver);
    }

    @Override
    protected void initView() {
        binding.ivConfig.setOnClickListener(v -> mListener.replaceFragment(ConfigFragment.newInstance()));
        binding.btnLogin.setOnClickListener(v -> {
            if (TextUtils.isEmpty(viewModel.phone.get())) {
                ToastManager.toast("请输入手机号码", ToastManager.INFO);
            } else {
                viewModel.getCourierByPhone();
            }
        });
        binding.tvSwitch.setOnClickListener(v -> {
            viewModel.editMode.set(true);
            viewModel.phone.set("");
            binding.etAccount.requestFocus();
        });
        binding.tvRegister.setOnClickListener(v -> mListener.replaceFragment(RegisterFragment.newInstance()));
    }

    @Override
    public void onBackPressed() {
        mListener.exitApp();
    }

    private Observer<Boolean> loginFaceFlagObserver = flag -> {
        mListener.replaceFragment(FaceLoginFragment.newInstance(viewModel.courierLiveData.getValue()));
    };

    private Observer<Boolean> loginFingerFlagObserver = flag -> {
        Courier courier = viewModel.courierLiveData.getValue();
        if (courier != null) {
            String fingerFeature1 = courier.getFingerFeature1();
            String fingerFeature2 = courier.getFingerFeature2();
            List<String> featureList = new ArrayList<>();
            featureList.add(fingerFeature1);
            featureList.add(fingerFeature2);
            FingerVerifyDialogFragment.newInstance(featureList, result -> {
                if (result) {
                    ToastManager.toast("登录成功", ToastManager.SUCCESS);
                    mListener.replaceFragment(HomeFragment.newInstance(viewModel.courierLiveData.getValue()));
                } else {
                    ToastManager.toast("登录失败", ToastManager.INFO);
                }
            }).show(getChildFragmentManager(), "FingerVerifyDialogFragment");
        }
    };

}
