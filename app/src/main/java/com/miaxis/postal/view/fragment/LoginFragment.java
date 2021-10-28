package com.miaxis.postal.view.fragment;

import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import com.miaxis.postal.R;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.AppEntity;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.data.model.CourierModel;
import com.miaxis.postal.databinding.FragmentLoginBinding;
import com.miaxis.postal.manager.AmapManager;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.LoginViewModel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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

    private Handler mHandler = new Handler();

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
        ValueUtil.GlobalPhone = null;
        App.getInstance().getThreadExecutor().execute(CourierModel::setLoginOut);
        App.getInstance().uploadEnable = false;
        binding.ivConfig.setOnClickListener(v -> mListener.replaceFragment(ConfigFragment.newInstance()));
        binding.btnLogin.setOnClickListener(v -> {
            if (checkInput()) {
                Config config = ConfigManager.getInstance().getConfig();
                viewModel.getCourier(config.getDeviceIMEI());
            }
        });

        addTextWatcher(binding.etPassword, binding.btnPasswordEye);
        //        viewModel.username.set("17857318080");
        //        viewModel.password.set("8080");
        viewModel.password.set("");
    }

    @Override
    public void onResume() {
        super.onResume();
        AmapManager.getInstance().stopLocation();
    }

    @Override
    public void onBackPressed() {
        mListener.exitApp();
    }

    private Observer<Boolean> loginResultObserver = result -> {
        if (result) {
            ValueUtil.GlobalPhone = viewModel.username.get();
            App.getInstance().getThreadExecutor().execute(() -> {
                CourierModel.setLogin();
                mHandler.post(() -> mListener.replaceFragment(HomeFragment.newInstance()));
            });
        } else {
            ToastManager.toast("手机号码或密码错误", ToastManager.INFO);
        }
    };

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
            if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
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
