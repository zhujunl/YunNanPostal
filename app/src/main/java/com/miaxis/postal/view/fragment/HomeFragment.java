package com.miaxis.postal.view.fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.data.repository.LoginRepository;
import com.miaxis.postal.databinding.FragmentHomeBinding;
import com.miaxis.postal.manager.AmapManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.util.StringUtils;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.adapter.BranchAdapter;
import com.miaxis.postal.view.adapter.HSpacesItemDecoration;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.dialog.CardModeSelectDialogFragment;
import com.miaxis.postal.view.dialog.EditPasswordDialogFragment;
import com.miaxis.postal.viewModel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvBranches.setLayoutManager(linearLayoutManager);
        branchAdapter = new BranchAdapter(getContext());
        branchAdapter.setBodyListener(this);
        binding.rvBranches.setAdapter(branchAdapter);
        binding.rvBranches.addItemDecoration(new HSpacesItemDecoration(10));
        ((SimpleItemAnimator) binding.rvBranches.getItemAnimator()).setSupportsChangeAnimations(false);

        init();
        getBranchList();

        AmapManager.getInstance().startLocation(getActivity().getApplication());//GPS初始化，登录后初始化
        App.getInstance().uploadEnable = true;
    }

    private void init() {
        String orgCode = ValueUtil.readOrgCode();
        String orgNode = ValueUtil.readOrgNode();
        String orgName = ValueUtil.readOrgName();
        if (!TextUtils.isEmpty(orgCode) && !TextUtils.isEmpty(orgNode) && !TextUtils.isEmpty(orgName)) {
            List<Branch> branches = new ArrayList<>();
            Branch branch = new Branch();
            branch.isSelected = true;
            branch.comcode = orgCode;
            branch.orgNode = orgNode;
            branch.orgName = orgName;
            branches.add(branch);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    branchAdapter.setDataList(branches);
                    branchAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void getBranchList() {
        showWaitDialog("正在请求数据中，请稍候。。。");
        App.getInstance().getThreadExecutor().execute(() -> {
            try {
                List<Branch> branchListSync = LoginRepository.getInstance().getBranchListSync(ValueUtil.GlobalPhone);
                dismissWaitDialog();
                if (branchListSync == null || branchListSync.isEmpty()) {
                    init();
                    return;
                }
                String lastBranchId = ValueUtil.readOrgCode();
                int position = -1;
                for (int i = 0; i < branchListSync.size(); i++) {
                    Branch branch = branchListSync.get(i);
                    branch.isSelected = StringUtils.isEquals(lastBranchId, branch.orgCode);
                    if (branch.isSelected) {
                        position = i;
                    }
                }
                if (position < 0 && !branchListSync.isEmpty()) {
                    branchListSync.get(0).isSelected = true;
                    //SPUtils.getInstance().write(ValueUtil.GlobalPhone, branchListSync.get(0).orgCode);
                    //SPUtils.getInstance().write(ValueUtil.GlobalPhone + "node", branchListSync.get(0).orgNode);
                    ValueUtil.write(branchListSync.get(0).orgCode, branchListSync.get(0).orgNode, branchListSync.get(0).orgName);
                }
                int finalPosition = position;
                mHandler.post(() -> {
                    branchAdapter.setDataList(branchListSync);
                    branchAdapter.notifyDataSetChanged();
                    binding.rvBranches.scrollToPosition(finalPosition);
                });
            } catch (Exception e) {
                e.printStackTrace();
                dismissWaitDialog();
                showResultDialog("错误：" + e.getMessage());
                init();
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
                .title("确认切换至【" + branch.orgName + "】？网点编号【" + branch.comcode + "】")
                .positiveText("确认")
                .onPositive((dialog, which) -> {
                    List<Branch> dataList = branchAdapter.getDataList();
                    for (Branch bran : dataList) {
                        bran.isSelected = false;
                    }
                    //SPUtils.getInstance().write(ValueUtil.GlobalPhone, branch.orgCode);
                    //SPUtils.getInstance().write(ValueUtil.GlobalPhone + "node", branch.orgNode);
                    ValueUtil.write(branch.orgCode, branch.orgNode, branch.orgName);
                    branch.isSelected = true;
                    branchAdapter.notifyDataSetChanged();
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
        if (TextUtils.isEmpty(ValueUtil.readOrgCode()) || TextUtils.isEmpty(ValueUtil.readOrgNode())) {
            new MaterialDialog.Builder(getContext())
                    .title("您当前未选择机构，无法使用此功能。")
                    .positiveText("确认")
                    .show();
            return false;
        }
        return true;
    }
}
