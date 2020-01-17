package com.miaxis.postal.view.fragment;

import android.view.View;

import androidx.lifecycle.ViewModelProviders;
import com.miaxis.postal.R;
import com.miaxis.postal.databinding.FragmentPreludeBinding;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.PreludeViewModel;

public class PreludeFragment extends BaseViewModelFragment<FragmentPreludeBinding, PreludeViewModel> {

    public static PreludeFragment newInstance() {
        return new PreludeFragment();
    }

    public PreludeFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_prelude;
    }

    @Override
    protected PreludeViewModel initViewModel() {
        return ViewModelProviders.of(this).get(PreludeViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.getInitSuccess().observe(this, initResult -> {
            if (initResult) {
                mListener.setRoot(LoginFragment.newInstance());
            }
        });
    }

    @Override
    protected void initView() {
        binding.ivConfig.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(ConfigFragment.newInstance())));
        binding.btnQuit.setOnClickListener(v -> mListener.exitApp());
        binding.btnRetry.setOnClickListener(v -> viewModel.requirePermission(PreludeFragment.this));
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.requirePermission(this);
    }

}