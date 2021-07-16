package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.model.CourierModel;
import com.miaxis.postal.data.repository.LoginRepository;
import com.miaxis.postal.databinding.FragmentConfigBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.manager.fingerPower.IFingerPower;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.auxiliary.OnLimitClickListener;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.presenter.UpdatePresenter;
import com.miaxis.postal.viewModel.ConfigViewModel;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
            Config config = viewModel.config.getValue();
            if (config != null) {
                Log.i("TAG===",config.getHost());
                String host1=config.getHost();
                String host = binding.etBaseUrl.getText().toString().trim();
                Log.i("TAG===2",host);
                if (!host.equals(host1)){
                    Log.i("TAG===3","");
                    config.setHost(host);
                    viewModel.saveConfig(config);
                }
            }
        });
        viewModel.config.observe(this, config -> {
            if (config!=null){
                binding.etBaseUrl.setText(config.getHost());
            }
        });
        viewModel.isExist.observe(this, aBoolean -> {
            if (aBoolean) {
                PostalManager.getInstance().clearAll();
                PostalManager.getInstance().outLogin();
                if (getActivity()!=null) {
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
