package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.databinding.FragmentOrderBinding;
import com.miaxis.postal.view.adapter.OrderImageAdapter;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.OrderViewModel;

public class OrderFragment extends BaseViewModelFragment<FragmentOrderBinding, OrderViewModel> {

    private OrderImageAdapter orderImageAdapter;

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
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(OrderViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.order.setValue(order);
        viewModel.order.observe(this, orderObserver);
    }

    @Override
    protected void initView() {
        initRecycleView();
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.ivCardPicture.setOnClickListener(v -> {
            mListener.replaceFragment(PhotoFragment.newInstance(order.getCardImage()));
        });
        binding.ivFacePicture.setOnClickListener(v -> {
            mListener.replaceFragment(PhotoFragment.newInstance(order.getCheckImage()));
        });
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.order.removeObserver(orderObserver);
    }

    private void initRecycleView() {
        orderImageAdapter = new OrderImageAdapter(getContext());
        orderImageAdapter.setListener((view, position) -> {
            mListener.replaceFragment(PhotoFragment.newInstance(orderImageAdapter.getData(position)));
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        binding.rvImage.setLayoutManager(gridLayoutManager);
        binding.rvImage.setAdapter(orderImageAdapter);
        ((SimpleItemAnimator) binding.rvImage.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private Observer<Order> orderObserver = order -> {
        orderImageAdapter.setDataList(order.getImageList());
        orderImageAdapter.notifyDataSetChanged();
    };

    public void setOrder(Order order) {
        this.order = order;
    }
}
