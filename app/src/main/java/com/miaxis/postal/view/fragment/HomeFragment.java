package com.miaxis.postal.view.fragment;

import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.databinding.FragmentHomeBinding;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.view.auxiliary.GlideImageLoader;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.auxiliary.OnLimitClickListener;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.dialog.CardModeSelectDialogFragment;
import com.miaxis.postal.view.dialog.EditPasswordDialogFragment;
import com.miaxis.postal.viewModel.HomeViewModel;

import java.util.Arrays;

public class HomeFragment extends BaseViewModelFragment<FragmentHomeBinding, HomeViewModel> {

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_home;
    }

    @Override
    protected HomeViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(HomeViewModel.class);
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
        binding.ivConfig.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(ConfigFragment.newInstance())));
//        binding.clExpress.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(CardFragment.newInstance())));
        binding.clExpress.setOnClickListener(new OnLimitClickHelper(view -> {
            CardModeSelectDialogFragment.newInstance().show(getChildFragmentManager(), "CardModeSelectDialogFragment");
        }));
        binding.clRecord.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(RecordTabFragment.newInstance())));
        binding.tvEditPassword.setOnClickListener(new OnLimitClickHelper(view -> {
            EditPasswordDialogFragment.newInstance().show(getChildFragmentManager(), "EditPasswordDialogFragment");
        }));
    }

    @Override
    public void onResume() {
        super.onResume();
        PostalManager.getInstance().startPostal();
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(getContext())
                .title("确认退出登录？")
                .positiveText("确认")
                .onPositive((dialog, which) -> mListener.backToStack(LoginFragment.class))
                .negativeText("取消")
                .show();
    }
}
