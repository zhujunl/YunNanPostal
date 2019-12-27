package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.databinding.FragmentOrderBinding;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.OrderViewModel;

public class OrderFragment extends BaseViewModelFragment<FragmentOrderBinding, OrderViewModel> {

    private Order order;

    public static OrderFragment newInstance(Order order) {
        OrderFragment fragment = new OrderFragment();
        fragment.setOrder(order);
        return fragment;
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
        viewModel.order.set(order);
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
