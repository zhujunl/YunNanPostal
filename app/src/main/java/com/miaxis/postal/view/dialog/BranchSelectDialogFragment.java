package com.miaxis.postal.view.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.databinding.FragmentBranchSelectDialogBinding;
import com.miaxis.postal.view.adapter.BranchSelectAdapter;
import com.miaxis.postal.view.adapter.SpacesItemDecoration;
import com.miaxis.postal.view.base.BaseViewModelDialogFragment;
import com.miaxis.postal.viewModel.BranchSelectViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class BranchSelectDialogFragment extends BaseViewModelDialogFragment<FragmentBranchSelectDialogBinding, BranchSelectViewModel> implements BranchSelectAdapter.OnBodyClickListener {

    private BranchSelectAdapter branchAdapter;

    private String userName = "";
    private final List<Branch> branches = new ArrayList<>();
    private Handler mHandler = new Handler();

    public static BranchSelectDialogFragment newInstance(String userName, List<Branch> branches) {
        BranchSelectDialogFragment branchSelectDialogFragment = new BranchSelectDialogFragment();
        branchSelectDialogFragment.setUserName(userName);
        branchSelectDialogFragment.setBranches(branches);
        return branchSelectDialogFragment;
    }

    private BranchSelectDialogFragment() {
        // Required empty public constructor
    }

    public void setUserName(String userName) {
        if (!TextUtils.isEmpty(userName)) {
            this.userName = userName;
        }
    }

    public void setBranches(List<Branch> branches) {
        if (branches != null && !branches.isEmpty()) {
            this.branches.addAll(branches);
        }
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_branch_select_dialog;
    }

    @Override
    protected BranchSelectViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(BranchSelectViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void initData() {
        //        viewModel.idCardSearch.observe(this, idCardObserver);
    }

    @Override
    protected void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvBranches.setLayoutManager(linearLayoutManager);
        branchAdapter = new BranchSelectAdapter(getContext());
        branchAdapter.setBodyListener(this);
        binding.rvBranches.setAdapter(branchAdapter);
        binding.rvBranches.addItemDecoration(new SpacesItemDecoration(2));
        ((SimpleItemAnimator) binding.rvBranches.getItemAnimator()).setSupportsChangeAnimations(false);

        branchAdapter.setDataList(branches);
        branchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.CENTER;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = (int) (dm.widthPixels * 0.8);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }

    @Override
    public void onBodyClick(View view, Branch branch, int position) {
//        ValueUtil.GlobalPhone = this.userName;
////        SPUtils.getInstance().write(ValueUtil.GlobalPhone, branch.orgCode);
////        SPUtils.getInstance().write(ValueUtil.GlobalPhone+"node", branch.orgNode);
//        ValueUtil.write(branch.orgCode,branch.orgNode,branch.orgName);
//        App.getInstance().getThreadExecutor().execute(() -> {
//            CourierModel.setLogin();
//            mHandler.post(() -> mListener.replaceFragment(HomeFragment.newInstance()));
//        });
    }
}
