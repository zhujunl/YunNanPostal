package com.miaxis.postal.view.fragment;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.databinding.FragmentInspectBinding;
import com.miaxis.postal.view.adapter.InspectAdapter;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.InspectViewModel;

public class InspectFragment extends BaseViewModelFragment<FragmentInspectBinding, InspectViewModel> {

    private Express express;
    private InspectAdapter inspectAdapter;

    public static InspectFragment newInstance(Express express) {
        InspectFragment inspectFragment = new InspectFragment();
        inspectFragment.setExpress(express);
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

    public void setExpress(Express express) {
        this.express = express;
    }
}
