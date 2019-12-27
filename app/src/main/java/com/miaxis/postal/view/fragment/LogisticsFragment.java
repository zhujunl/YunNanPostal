package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.databinding.FragmentLogisticsBinding;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.LogisticsViewModel;

public class LogisticsFragment extends BaseViewModelFragment<FragmentLogisticsBinding, LogisticsViewModel> {

    public static LogisticsFragment newInstance() {
        return new LogisticsFragment();
    }

    public LogisticsFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_logistics;
    }

    @Override
    protected LogisticsViewModel initViewModel() {
        return ViewModelProviders.of(this).get(LogisticsViewModel.class);
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

    }

    @Override
    public void onBackPressed() {

    }
}
