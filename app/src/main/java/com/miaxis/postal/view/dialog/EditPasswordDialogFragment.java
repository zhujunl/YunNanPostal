package com.miaxis.postal.view.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.miaxis.postal.R;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.databinding.FragmentEditPasswordDialogBinding;
import com.miaxis.postal.manager.DataCacheManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.EncryptUtil;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelDialogFragment;
import com.miaxis.postal.viewModel.EditPasswordViewModel;

import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class EditPasswordDialogFragment extends BaseViewModelDialogFragment<FragmentEditPasswordDialogBinding, EditPasswordViewModel> {

    public static EditPasswordDialogFragment newInstance() {
        return new EditPasswordDialogFragment();
    }

    public EditPasswordDialogFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_edit_password_dialog;
    }

    @Override
    protected EditPasswordViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(EditPasswordViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.editFlag.observe(this, editFlagObserver);
    }

    @Override
    protected void initView() {
        addTextWatcher(binding.etOldPwd, binding.btnOldPwdEye);
        addTextWatcher(binding.etNewPwd, binding.btnNewPwdEye);
        addTextWatcher(binding.etCheckPwd, binding.btnCheckPwdEye);
        binding.btnPasswordCancel.setOnClickListener(v -> dismiss());
        binding.btnPasswordConfirm.setOnClickListener(new OnLimitClickHelper(view -> {
            if (checkInput()) {
                viewModel.editPassword(viewModel.newPassword.get());
            }
        }));
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.CENTER;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }

    private Observer<Boolean> editFlagObserver = flag -> {
        if (flag) {
            dismiss();
        }
    };

    private boolean checkInput() {
        String psw = viewModel.oldPassword.get();
        String newPsw = viewModel.newPassword.get();
        String again = viewModel.checkPassword.get();
        String originalPsw = DataCacheManager.getInstance().getCourier().getPassword();
        try {
            if (TextUtils.isEmpty(psw)) {
                ToastManager.toast("请输入原始密码", ToastManager.INFO);
            } else if (TextUtils.isEmpty(newPsw) || newPsw == null || newPsw.trim().isEmpty()) {
                ToastManager.toast("请输入新密码", ToastManager.INFO);
            } else if (!TextUtils.equals(originalPsw, getPasswordMD5(psw))) {
                ToastManager.toast("输入的密码与原始密码不一致", ToastManager.INFO);
            } else if (TextUtils.equals(originalPsw, getPasswordMD5(newPsw))) {
                ToastManager.toast("输入的新密码与原始密码不能一致", ToastManager.INFO);
            } else if (TextUtils.isEmpty(again)) {
                ToastManager.toast("请再次输入新密码", ToastManager.INFO);
            } else if (!TextUtils.equals(newPsw, again)) {
                ToastManager.toast("再次输入的新密码不一致", ToastManager.INFO);
            } else {
                return true;
            }
        } catch (MyException e) {
            e.printStackTrace();
            ToastManager.toast("密码处理失败", ToastManager.INFO);
        }
        return false;
    }

    private String getPasswordMD5(String password) throws MyException {
        if (!TextUtils.isEmpty(password)) {
            String passwordMD5 = EncryptUtil.md5Decode32(password);
            if (!TextUtils.isEmpty(passwordMD5)) {
                return passwordMD5;
            }
        }
        throw new MyException("输入密码为空或提取MD5失败");
    }

    private void addTextWatcher(EditText editText, Button btnEye) {
        btnEye.setOnClickListener(v -> {
            if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                btnEye.setBackgroundResource(R.drawable.ic_visibility_off_blue);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                btnEye.setBackgroundResource(R.drawable.ic_visibility_blue);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            editText.setSelection(editText.getText().toString().length());
        });
        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.toString().trim().isEmpty()) {
                    return "";
                } else {
                    return null;
                }
            }
        }});
    }

}
