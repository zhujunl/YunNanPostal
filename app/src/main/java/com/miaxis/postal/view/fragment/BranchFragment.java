package com.miaxis.postal.view.fragment;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.databinding.FragmentBranchBinding;
import com.miaxis.postal.view.adapter.BranchAdapter;
import com.miaxis.postal.view.adapter.SpacesItemDecoration;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.BranchViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class BranchFragment extends BaseViewModelFragment<FragmentBranchBinding, BranchViewModel> {

    private static final String TAG = "Mx-ExpressFragment";

    private BranchFragment() {
        // Required empty public constructor
    }

    public static BranchFragment newInstance() {
        return new BranchFragment();
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_branch;
    }

    @Override
    protected BranchViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(BranchViewModel.class);
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
        initRecycleView();
        binding.ivBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(HomeFragment.class);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void initRecycleView() {
        BranchAdapter branchAdapter = new BranchAdapter(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.rvBranches.setLayoutManager(gridLayoutManager);
        binding.rvBranches.setAdapter(branchAdapter);
        binding.rvBranches.addItemDecoration(new SpacesItemDecoration(1));
        ((SimpleItemAnimator) binding.rvBranches.getItemAnimator()).setSupportsChangeAnimations(false);
        List<Branch> branches = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            branches.add(new Branch("机构编号XX" + i, "机构名称XX" + i));
        }
        branchAdapter.setDataList(branches);
    }

    //    private View.OnClickListener deleteListener = new OnLimitClickHelper(view -> {
    //        new MaterialDialog.Builder(getContext())
    //                .title("确认删除？")
    //                .positiveText("确认")
    //                .onPositive((dialog, which) -> viewModel.deleteSelf())
    //                .negativeText("取消")
    //                .show();
    //    });


}
