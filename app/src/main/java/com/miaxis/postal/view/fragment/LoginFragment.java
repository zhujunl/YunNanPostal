package com.miaxis.postal.view.fragment;

import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import com.miaxis.postal.R;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.data.model.CourierModel;
import com.miaxis.postal.data.repository.LoginRepository;
import com.miaxis.postal.databinding.FragmentLoginBinding;
import com.miaxis.postal.manager.AmapManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.SPUtils;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.dialog.BranchSelectDialogFragment;
import com.miaxis.postal.viewModel.LoginViewModel;

import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
                viewModel.getCourier();
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
            final String userPhone = viewModel.username.get();
            if (TextUtils.isEmpty(SPUtils.getInstance().read(userPhone, ""))) {
                showWaitDialog("获取机构中，请稍候。。。");
                App.getInstance().getThreadExecutor().execute(() -> {
                    try {
                        //List<Branch> branchListSync = LoginRepository.getInstance().getBranchListSync(userPhone);
                        List<Branch> branchListSync = LoginRepository.getInstance().getAllBranchListSync();
                        dismissWaitDialog();
                        if (branchListSync.isEmpty()) {
                            ValueUtil.GlobalPhone = userPhone;
                            //boolean write = SPUtils.getInstance().write(ValueUtil.GlobalPhone, "");
                            //                            SPUtils.getInstance().write(ValueUtil.GlobalPhone, "");
                            //                            SPUtils.getInstance().write(ValueUtil.GlobalPhone + "node", "");
                            ValueUtil.write("", "","");
                            CourierModel.setLogin();
                            mHandler.post(() -> mListener.replaceFragment(HomeFragment.newInstance()));
                        } else if (branchListSync.size() == 1) {
                            ValueUtil.GlobalPhone = userPhone;
                            //                            SPUtils.getInstance().write(ValueUtil.GlobalPhone, branchListSync.get(0).orgCode);
                            //                            SPUtils.getInstance().write(ValueUtil.GlobalPhone + "node", branchListSync.get(0).orgNode);
                            ValueUtil.write(branchListSync.get(0).orgCode, branchListSync.get(0).orgNode, branchListSync.get(0).orgName);
                            CourierModel.setLogin();
                            mHandler.post(() -> mListener.replaceFragment(HomeFragment.newInstance()));
                        } else {
                            mHandler.post(() -> BranchSelectDialogFragment.newInstance(userPhone, branchListSync)
                                    .show(getChildFragmentManager(), "BranchSelectDialogFragment"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dismissWaitDialog();
                        showResultDialog("错误：" + e.getMessage());
                    }
                });
            } else {
                ValueUtil.GlobalPhone = userPhone;
                App.getInstance().getThreadExecutor().execute(() -> {
                    CourierModel.setLogin();
                    mHandler.post(() -> mListener.replaceFragment(HomeFragment.newInstance()));
                });
            }
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
