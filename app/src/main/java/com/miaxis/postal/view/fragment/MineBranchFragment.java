package com.miaxis.postal.view.fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.data.repository.LoginRepository;
import com.miaxis.postal.databinding.FragmentMineBranchBinding;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.adapter.MineBranchAdapter;
import com.miaxis.postal.view.adapter.SpacesItemDecoration;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.MineBranchViewModel;

import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class MineBranchFragment extends BaseViewModelFragment<FragmentMineBranchBinding, MineBranchViewModel> implements MineBranchAdapter.OnBodyClickListener {

    private static final String TAG = "Mx-ExpressFragment";
    private final Handler mHandler = new Handler();
    private MineBranchAdapter mineBranchAdapter;

    private MineBranchFragment() {
        // Required empty public constructor
    }

    public static MineBranchFragment newInstance() {
        return new MineBranchFragment();
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_mine_branch;
    }

    @Override
    protected MineBranchViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(MineBranchViewModel.class);
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
        binding.btnBinding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String branchNo = binding.etNo.getText().toString();
                if (TextUtils.isEmpty(branchNo)) {
                    new MaterialDialog.Builder(getContext())
                            .title("请先输入网点编号。")
                            .positiveText("确认")
                            .show();
                } else {
                    bindBranch(branchNo);
                }
            }
        });
        mineBranchAdapter = new MineBranchAdapter(getContext());
        mineBranchAdapter.setBodyListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.rvBranches.setLayoutManager(gridLayoutManager);
        binding.rvBranches.setAdapter(mineBranchAdapter);
        binding.rvBranches.addItemDecoration(new SpacesItemDecoration(1));
        ((SimpleItemAnimator) binding.rvBranches.getItemAnimator()).setSupportsChangeAnimations(false);
        getBranchList();
    }


    private void getBranchList() {
        showWaitDialog("正在请求数据中，请稍候。。。");
        App.getInstance().getThreadExecutor().execute(() -> {
            try {
                List<Branch> branchListSync = LoginRepository.getInstance().getBranchListSync(ValueUtil.GlobalPhone);
                dismissWaitDialog();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mineBranchAdapter.setDataList(branchListSync);
                        mineBranchAdapter.notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                dismissWaitDialog();
                showResultDialog("错误：" + e.getMessage());
            }
        });
    }

    private void bindBranch(String comcode) {
        showWaitDialog("正在请求数据中，请稍候。。。");
        App.getInstance().getThreadExecutor().execute(() -> {
            try {
                String bindingNodeSync = LoginRepository.getInstance().bindingNodeSync(ValueUtil.GlobalPhone, comcode);
                dismissWaitDialog();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastManager.toast(bindingNodeSync == null ? "绑定成功" : ("绑定失败" + bindingNodeSync), bindingNodeSync == null ? ToastManager.SUCCESS : ToastManager.ERROR);
                    }
                });
                if (bindingNodeSync == null) {
                    getBranchList();
                }
            } catch (Exception e) {
                e.printStackTrace();
                dismissWaitDialog();
                showResultDialog("错误：" + e.getMessage());
            }
        });
    }

    private void unBindBranch(String orgNode) {
        showWaitDialog("正在请求数据中，请稍候。。。");
        App.getInstance().getThreadExecutor().execute(() -> {
            try {
                String bindingNodeSync = LoginRepository.getInstance().unBindingNodeSync(ValueUtil.GlobalPhone, orgNode);
                dismissWaitDialog();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastManager.toast(bindingNodeSync == null ? "解绑成功" : ("解绑失败" + bindingNodeSync), bindingNodeSync == null ? ToastManager.SUCCESS : ToastManager.ERROR);
                    }
                });
                if (bindingNodeSync == null) {
                    getBranchList();
                }
            } catch (Exception e) {
                e.printStackTrace();
                dismissWaitDialog();
                showResultDialog("错误：" + e.getMessage());
            }
        });
    }

    @Override
    public void onBodyClick(View view, Branch branch, int position) {
        new MaterialDialog.Builder(getContext())
                .title("确认删除【" + branch.orgName + "】？网点编号【" + branch.comcode + "】")
                .positiveText("确认")
                .onPositive((dialog, which) -> {
                    unBindBranch(branch.orgNode);
                    //viewModel.deleteBranch(branch);
                })
                .negativeText("取消")
                .show();

    }


}
