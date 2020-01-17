package com.miaxis.postal.view.fragment;

import android.view.View;

import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.databinding.FragmentHomeBinding;
import com.miaxis.postal.view.auxiliary.GlideImageLoader;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.auxiliary.OnLimitClickListener;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.HomeViewModel;

import java.util.Arrays;

public class HomeFragment extends BaseViewModelFragment<FragmentHomeBinding, HomeViewModel> {

    private Courier courier;

    public static HomeFragment newInstance(Courier courier) {
        HomeFragment fragment = new HomeFragment();
        fragment.setCourier(courier);
        return fragment;
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
        return ViewModelProviders.of(this).get(HomeViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.courier.set(courier);
    }

    @Override
    protected void initView() {
        binding.clConfig.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(ConfigFragment.newInstance())));
        binding.clLogistic.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(CardFragment.newInstance())));
        binding.clRecord.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(LogisticsFragment.newInstance())));
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

    public void setCourier(Courier courier) {
        this.courier = courier;
    }
}
