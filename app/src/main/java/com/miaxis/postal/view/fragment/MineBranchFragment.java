package com.miaxis.postal.view.fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.data.repository.LoginRepository;
import com.miaxis.postal.databinding.FragmentMineBranchBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.adapter.MineBranchAdapter;
import com.miaxis.postal.view.adapter.SpacesItemDecoration;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.LoginViewModel;
import com.miaxis.postal.viewModel.MineBranchViewModel;

import java.util.List;

import androidx.lifecycle.Observer;
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

    private Handler deviceHandler = new Handler();
    private Runnable task =new Runnable() {
        public void run() {
            // TODOAuto-generated method stub
            deviceHandler.postDelayed(this,10*1000);//设置延迟时间
            //需要执行的代码
            //获取设备状态,判断设备状态是启用还是禁用
            LoginViewModel loginViewModel = new LoginViewModel();
            Config config = ConfigManager.getInstance().getConfig();
            loginViewModel.getDevices(config.getDeviceIMEI());
            loginViewModel.deviceslist.observe(getActivity(), new Observer<DevicesStatusEntity.DataDTO>() {
                @Override
                public void onChanged(DevicesStatusEntity.DataDTO dataDTO) {
                    //如果是启用状态不做任何操作
                    if (dataDTO.getStatus().equals(ValueUtil.DEVICE_ENABLE)){

                    }else {
                        //如果从启用状态切换到了禁用状态强制退出登录跳到登录页面
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mine_branch, new LoginFragment(), null)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });

        }
    };

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
        //进入延时状态,一小时访问一次接口
        deviceHandler.postDelayed(task,3600000);//延迟调用
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
        binding.btnBinding.setOnClickListener(v -> {
            String branchNo = binding.etNo.getText().toString();
            if (TextUtils.isEmpty(branchNo)) {
                new MaterialDialog.Builder(getContext())
                        .title("请先输入网点编号。")
                        .positiveText("确认")
                        .show();
            } else {
                bindBranch(branchNo);
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
                mHandler.post(() -> {
                    mineBranchAdapter.setDataList(branchListSync);
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
                mHandler.post(() -> ToastManager.toast(bindingNodeSync == null ? "绑定成功" : ("" + bindingNodeSync), bindingNodeSync == null ? ToastManager.SUCCESS : ToastManager.ERROR));
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
                mHandler.post(() -> ToastManager.toast(bindingNodeSync == null ? "解绑成功" : ("" + bindingNodeSync), bindingNodeSync == null ? ToastManager.SUCCESS : ToastManager.ERROR));
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
                .title("确认删除【" + branch.orgName + "】？")
                .positiveText("确认")
                .onPositive((dialog, which) -> {
                    unBindBranch(branch.orgNode);
                    //viewModel.deleteBranch(branch);
                })
                .negativeText("取消")
                .show();

    }


}
