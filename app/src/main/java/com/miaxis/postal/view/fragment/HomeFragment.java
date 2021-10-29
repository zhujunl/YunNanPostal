package com.miaxis.postal.view.fragment;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.data.repository.LoginRepository;
import com.miaxis.postal.databinding.FragmentHomeBinding;
import com.miaxis.postal.manager.AmapManager;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.util.ListUtils;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.adapter.BranchAdapter;
import com.miaxis.postal.view.adapter.HSpacesItemDecoration;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.dialog.CardModeSelectDialogFragment;
import com.miaxis.postal.view.dialog.EditPasswordDialogFragment;
import com.miaxis.postal.viewModel.HomeViewModel;
import com.miaxis.postal.viewModel.LoginViewModel;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class HomeFragment extends BaseViewModelFragment<FragmentHomeBinding, HomeViewModel> implements BranchAdapter.OnBodyClickListener {

    private BranchAdapter branchAdapter;
    private final Handler mHandler = new Handler();

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_home;
    }

    @Override
    protected HomeViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(HomeViewModel.class);
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
        binding.ivConfig.setOnClickListener(new OnLimitClickHelper(view -> {
            mListener.replaceFragment(ConfigFragment.newInstance());
        }));
        //binding.clExpress.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(CardFragment.newInstance())));
        binding.clExpress.setOnClickListener(new OnLimitClickHelper(view -> {
            if (checkBranch()) {
                CardModeSelectDialogFragment.newInstance(false).show(getChildFragmentManager(), "CardModeSelectDialogFragment");
            }
        }));
        binding.clRecord.setOnClickListener(new OnLimitClickHelper(view -> {
            if (checkBranch()) {
                mListener.replaceFragment(RecordTabFragment.newInstance());
            }
        }));
        binding.tvEditPassword.setOnClickListener(new OnLimitClickHelper(view -> {
            EditPasswordDialogFragment.newInstance().show(getChildFragmentManager(), "EditPasswordDialogFragment");
        }));
        binding.clProtocol.setOnClickListener(v -> {
            if (checkBranch()) {
                CardModeSelectDialogFragment.newInstance(true).show(getChildFragmentManager(), "CardModeSelectDialogFragment");
            }
        });
        binding.clMine.setOnClickListener(v -> {
            mListener.replaceFragment(MineBranchFragment.newInstance());
        });
        binding.clStatistical.setOnClickListener(v -> {
            mListener.replaceFragment(StatisticalFragment.newInstance());
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvBranches.setLayoutManager(linearLayoutManager);
        branchAdapter = new BranchAdapter(getContext());
        branchAdapter.setBodyListener(this);
        binding.rvBranches.setAdapter(branchAdapter);
        binding.rvBranches.addItemDecoration(new HSpacesItemDecoration(10));
        ((SimpleItemAnimator) binding.rvBranches.getItemAnimator()).setSupportsChangeAnimations(false);

        viewModel.branchList.observe(this, branches -> {
            List<Branch> dataList = branchAdapter.getDataList();
            if (!Objects.equals(dataList, branches)) {
                branchAdapter.setDataList(branches);
            }
            int selectedPosition = Branch.findSelectedPosition(branches);
            if (selectedPosition >= 0) {
                binding.rvBranches.scrollToPosition(selectedPosition);
            }
        });
        viewModel.init();

        getBranchList();

        AmapManager.getInstance().startLocation(getActivity().getApplication());//GPS初始化，登录后初始化
        App.getInstance().uploadEnable = true;
    }

    private void getBranchList() {
        //showWaitDialog("正在请求数据中，请稍候。。。");
        App.getInstance().getThreadExecutor().execute(() -> {
            try {
                //List<Branch> branchListSync = LoginRepository.getInstance().getBranchListSync(ValueUtil.GlobalPhone);
                List<Branch> branchListSync = LoginRepository.getInstance().getAllBranchListSync();
                dismissWaitDialog();
                viewModel.flush(branchListSync);
            } catch (Exception e) {
                e.printStackTrace();
                dismissWaitDialog();
                if (viewModel == null || ListUtils.isNullOrEmpty(viewModel.branchList.getValue())) {
                    showResultDialog("错误：" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        PostalManager.getInstance().startPostal();
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(getContext())
                .title("确认退出登录？")
                .positiveText("确认")
                .onPositive((dialog, which) -> {
                    PostalManager.getInstance().outLogin();
                    if (getActivity() != null) {
                        boolean isHaveLoginFragment = false;
                        List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
                        for (Fragment fragment : fragments) {
                            if (fragment instanceof LoginFragment) {
                                isHaveLoginFragment = true;
                                break;
                            }
                        }
                        if (mListener != null) {
                            if (isHaveLoginFragment) {
                                mListener.backToStack(LoginFragment.class);
                            } else {
                                mListener.replaceFragment(LoginFragment.newInstance());
                            }
                        }
                    }
                })
                .negativeText("取消")
                .show();
    }

    @Override
    public void onBodyClick(View view, Branch branch, int position) {
        new MaterialDialog.Builder(getContext())
                .title("确认切换至【" + branch.orgName + "】？")
                .positiveText("确认")
                .onPositive((dialog, which) -> {
                    viewModel.onItemClick(branch);
                })
                .negativeText("取消")
                .show();
    }

    private boolean checkBranch() {
        if (branchAdapter == null || branchAdapter.getDataList() == null || branchAdapter.getDataList().isEmpty()) {
            new MaterialDialog.Builder(getContext())
                    .title("请先查询网点机构。")
                    .positiveText("去查询")
                    .onPositive((dialog, which) -> getBranchList())
                    .show();
            return false;
        }
        if (Branch.findSelected(branchAdapter.getDataList()) == null) {
            new MaterialDialog.Builder(getContext())
                    .title("您当前未选择机构，无法使用此功能。")
                    .positiveText("确认")
                    .show();
            return false;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        LoginViewModel loginViewModel =new ViewModelProvider(this, getViewModelProviderFactory()).get(LoginViewModel.class);
        Config config = ConfigManager.getInstance().getConfig();
        loginViewModel.getDevices(config.getDeviceIMEI());
        loginViewModel.deviceslist.observe(getActivity(), new Observer<DevicesStatusEntity.DataDTO>() {
            @Override
            public void onChanged(DevicesStatusEntity.DataDTO dataDTO) {
                //如果是启用状态不做任何操作
                if (!dataDTO.getStatus().equals(ValueUtil.DEVICE_ENABLE)){
                    //如果从启用状态切换到了禁用状态强制退出登录跳到登录页面
                    mListener.replaceFragment(LoginFragment.newInstance());
                }
            }
        });
    }
}