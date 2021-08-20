package com.miaxis.postal.view.fragment;

import android.util.Log;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.databinding.FragmentConfigBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.ConfigViewModel;

import androidx.lifecycle.ViewModelProvider;

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
        binding.flDownload.setOnClickListener(v -> mListener.replaceFragment(AppDownloadFragment.newInstance()));

        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.ivSave.setOnClickListener(v -> {
            Config config = viewModel.config.getValue();
            if (config != null) {
                Log.i("TAG===", config.getHost());
                String host1 = config.getHost();
                String host = binding.etBaseUrl.getText().toString().trim();
                Log.i("TAG===2", host);
                if (!host.equals(host1)) {
                    Log.i("TAG===3", "");
                    config.setHost(host);
                    viewModel.saveConfig(config);
                }
            }
        });
        viewModel.config.observe(this, config -> {
            if (config != null) {
                binding.etBaseUrl.setText(config.getHost());
            }
        });
        Config config = ConfigManager.getInstance().getConfig();
        viewModel.config.setValue(config);
        viewModel.isExist.observe(this, aBoolean -> {
            if (aBoolean) {
                PostalManager.getInstance().clearAll();
                PostalManager.getInstance().outLogin();
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack(LoginFragment.class.getName(), 0);
                }
                //                mListener.backToStack(LoginFragment.class);
            }
        });
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

}
