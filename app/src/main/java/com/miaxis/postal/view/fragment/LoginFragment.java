package com.miaxis.postal.view.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.ViewModelProviders;

import com.miaxis.postal.R;
import com.miaxis.postal.databinding.FragmentLoginBinding;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.LoginViewModel;

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

    }

    @Override
    protected void initView() {
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
    }
}
