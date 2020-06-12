package com.miaxis.postal.view.fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.databinding.FragmentLoginBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.TTSManager;
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
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(LoginViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.loginResult.observe(this, loginResultObserver);
    }

    @Override
    protected void initView() {
        binding.ivConfig.setOnClickListener(v -> mListener.replaceFragment(ConfigFragment.newInstance()));
        binding.btnLogin.setOnClickListener(v -> {
            if (checkInput()) {
                viewModel.getCourier();
            }
        });
        addTextWatcher(binding.etPassword, binding.btnPasswordEye);
        viewModel.username.set("17857318080");
        viewModel.password.set("8080");
//        viewModel.password.set("");
    }

    @Override
    public void onBackPressed() {
        mListener.exitApp();
    }

    private Observer<Boolean> loginResultObserver = result -> {
        if (result) {
            mListener.replaceFragment(HomeFragment.newInstance());
        } else {
            ToastManager.toast("密码错误", ToastManager.INFO);
        }
    };

//    private Observer<Boolean> loginFaceFlagObserver = flag -> {
//        mListener.replaceFragment(FaceLoginFragment.newInstance(viewModel.courierLiveData.getValue()));
//    };
//
//    private Observer<Boolean> loginFingerFlagObserver = flag -> {
//        Courier courier = viewModel.courierLiveData.getValue();
//        if (courier != null) {
//            String fingerFeature1 = courier.getFingerFeature1();
//            String fingerFeature2 = courier.getFingerFeature2();
//            List<String> featureList = new ArrayList<>();
//            featureList.add(fingerFeature1);
//            featureList.add(fingerFeature2);
//            FingerVerifyDialogFragment.newInstance(featureList, result -> {
//                if (result) {
//                    TTSManager.getInstance().playVoiceMessageFlush("指纹登录成功");
//                    ToastManager.toast("登录成功", ToastManager.SUCCESS);
//                    mListener.replaceFragment(HomeFragment.newInstance(viewModel.courierLiveData.getValue()));
//                } else {
//                    ToastManager.toast("登录失败", ToastManager.INFO);
//                }
//            }).show(getChildFragmentManager(), "FingerVerifyDialogFragment");
//        }
//    };

    private boolean checkInput() {
        if (TextUtils.isEmpty(viewModel.username.get())) {
            ToastManager.toast("请输入手机号码", ToastManager.INFO);
            return false;
        } else if (TextUtils.isEmpty(viewModel.password.get())) {
            ToastManager.toast("请输入密码", ToastManager.INFO);
            return false;
        }
        return true;
    }

    private void addTextWatcher(final EditText editText, final ImageButton btnEye) {
        btnEye.setOnClickListener(v -> {
            if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                btnEye.setImageResource(R.drawable.ic_visibility_off_white);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                btnEye.setImageResource(R.drawable.ic_visibility_white);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            editText.setSelection(editText.getText().toString().length());
        });
    }

}
