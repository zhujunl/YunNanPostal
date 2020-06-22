package com.miaxis.postal.view.dialog;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.IDCard;
import com.miaxis.postal.databinding.FragmentCardModeSelectDialogBinding;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.PatternUtil;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.auxiliary.OnLimitClickListener;
import com.miaxis.postal.view.base.BaseDialogFragment;
import com.miaxis.postal.view.base.BaseViewModelDialogFragment;
import com.miaxis.postal.view.fragment.CardFragment;
import com.miaxis.postal.view.fragment.FaceVerifyFragment;
import com.miaxis.postal.view.fragment.ManualFragment;
import com.miaxis.postal.viewModel.CardModeSelectViewModel;

public class CardModeSelectDialogFragment extends BaseViewModelDialogFragment<FragmentCardModeSelectDialogBinding, CardModeSelectViewModel> {

    public static CardModeSelectDialogFragment newInstance() {
        return new CardModeSelectDialogFragment();
    }

    public CardModeSelectDialogFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_card_mode_select_dialog;
    }

    @Override
    protected CardModeSelectViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(CardModeSelectViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.idCardSearch.observe(this, idCardObserver);
    }

    @Override
    protected void initView() {
        binding.tvHasCard.setOnClickListener(new OnLimitClickHelper(view -> {
            mListener.replaceFragment(CardFragment.newInstance());
            dismiss();
        }));
        binding.tvNoCard.setOnClickListener(new OnLimitClickHelper(view -> {
            if (binding.clCardNumber.getVisibility() == View.GONE) {
                binding.clCardNumber.setVisibility(View.VISIBLE);
            }
        }));
        binding.tvCardNumber.setOnClickListener(new OnLimitClickHelper(view -> {
            String input = binding.etCardNumber.getText().toString();
            if (TextUtils.isEmpty(input)) {
                ToastManager.toast("请输入证件号码", ToastManager.INFO);
            } else if (!PatternUtil.isIDNumber(input)) {
                ToastManager.toast("请输入格式正确的证件号码", ToastManager.INFO);
            } else {
                viewModel.searchLocalIDCard(input);
            }
        }));
        binding.etCardNumber.setText("");
        binding.etCardNumber.setRawInputType(Configuration.KEYBOARD_QWERTY);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable( new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.CENTER;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = (int) (dm.widthPixels * 0.8);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }

    private Observer<Boolean> idCardObserver = idCard -> {
        if (idCard) {
            mListener.replaceFragment(FaceVerifyFragment.newInstance(viewModel.idCardRecord));
        } else {
            mListener.replaceFragment(ManualFragment.newInstance(binding.etCardNumber.getText().toString()));
        }
        dismiss();
    };

}
