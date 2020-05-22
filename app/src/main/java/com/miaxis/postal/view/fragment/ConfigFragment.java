package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.databinding.FragmentConfigBinding;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.auxiliary.OnLimitClickListener;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.presenter.UpdatePresenter;
import com.miaxis.postal.viewModel.ConfigViewModel;

public class ConfigFragment extends BaseViewModelFragment<FragmentConfigBinding, ConfigViewModel> {

    public static ConfigFragment newInstance() {
        return new ConfigFragment();
    }

    public ConfigFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_config;
    }

    @Override
    protected ConfigViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(ConfigViewModel.class);
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
        binding.tvVersion.setText(ValueUtil.getCurVersion(getContext()));
        binding.tvCheckUpdate.setOnClickListener(new OnLimitClickHelper(view -> {
            mListener.showWaitDialog("正在检查更新，请稍后...");
            mListener.updateApp((result, message) -> {
                mListener.dismissWaitDialog();
                if (!result) {
                    mListener.showResultDialog(message);
                }
            });
        }));
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.ivSave.setOnClickListener(v -> {
            Config config = viewModel.config.get();
            if (config != null) {
                viewModel.saveConfig(config);
            }
        });
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }
}
