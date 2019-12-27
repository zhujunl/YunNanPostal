package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.databinding.FragmentInspectBinding;
import com.miaxis.postal.view.adapter.InspectAdapter;
import com.miaxis.postal.view.adapter.SpacesItemDecoration;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.InspectViewModel;

public class InspectFragment extends BaseViewModelFragment<FragmentInspectBinding, InspectViewModel> {

    private Order order;
    private InspectAdapter inspectAdapter;

    public static InspectFragment newInstance(Order order) {
        InspectFragment inspectFragment = new InspectFragment();
        inspectFragment.setOrder(order);
        return inspectFragment;
    }

    public InspectFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_inspect;
    }

    @Override
    protected InspectViewModel initViewModel() {
        return ViewModelProviders.of(this).get(InspectViewModel.class);
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
        inspectAdapter = new InspectAdapter(getContext(), viewModel);
        viewModel.photoList.observe(this, photoList -> inspectAdapter.notifyDataSetChanged());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        binding.rvInspect.setLayoutManager(gridLayoutManager);
        binding.rvInspect.setAdapter(inspectAdapter);
        ((SimpleItemAnimator) binding.rvInspect.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
