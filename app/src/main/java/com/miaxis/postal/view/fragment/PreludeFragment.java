package com.miaxis.postal.view.fragment;

import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.miaxis.postal.R;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.databinding.FragmentPreludeBinding;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.PreludeViewModel;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(PreludeViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.getInitSuccess().observe(this, initResult -> {
            if (initResult) {
                viewModel.isLogin();
            }
        });
        //是否登录
        viewModel.isLoginState.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    mListener.setRoot(HomeFragment.newInstance());
                } else {
                    mListener.setRoot(LoginFragment.newInstance());
                }
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