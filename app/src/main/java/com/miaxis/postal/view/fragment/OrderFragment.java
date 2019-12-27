package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.databinding.FragmentOrderBinding;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.OrderViewModel;

public class OrderFragment extends BaseViewModelFragment<FragmentOrderBinding, OrderViewModel> {

    public static OrderFragment newInstance() {
        return new OrderFragment();
    }

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_order;
    }

    @Override
    protected OrderViewModel initViewModel() {
        return ViewModelProviders.of(this).get(OrderViewModel.class);
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
