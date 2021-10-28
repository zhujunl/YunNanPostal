package com.miaxis.postal.view.fragment;

import android.os.Handler;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.databinding.FragmentFingerRegisterBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.FingerRegisterViewModel;
import com.miaxis.postal.viewModel.LoginViewModel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class FingerRegisterFragment extends BaseViewModelFragment<FragmentFingerRegisterBinding, FingerRegisterViewModel> {

    private MaterialDialog retryDialog;

    private String mark;

    public static FingerRegisterFragment newInstance(String mark) {
        FingerRegisterFragment fragment = new FingerRegisterFragment();
        fragment.setMark(mark);
        return fragment;
    }

    public FingerRegisterFragment() {
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
                                .replace(R.id.finger_register, new LoginFragment(), null)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });

        }
    };

    @Override
    protected int setContentView() {
        return R.layout.fragment_finger_register;
    }

    @Override
    protected FingerRegisterViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(FingerRegisterViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.mark = mark;
        viewModel.initFingerResult.observe(this, fingerInitObserver);
        viewModel.fingerResultFlag.observe(this, result -> mListener.backToStack(null));
        viewModel.fingerImageUpdate.observe(this, fingerImageUpdateObserver);
        //进入延时状态,一小时访问一次接口
        deviceHandler.postDelayed(task,3600000);//延迟调用
    }

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (retryDialog != null && retryDialog.isShowing()) {
            retryDialog.dismiss();
        }
        viewModel.initFingerDevice();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.releaseFingerDevice();
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(getContext())
                .title("确认退出？")
                .content("退出将放弃当前进度")
                .positiveText("确认")
                .onPositive((dialog, which) -> mListener.backToStack(null))
                .negativeText("取消")
                .show();
    }

    private Observer<Status> fingerInitObserver = status -> {
        switch (status) {
            case FAILED:
                mListener.dismissWaitDialog();
                retryDialog = new MaterialDialog.Builder(getContext())
                        .title("初始化指纹模块失败，是否重试？")
                        .positiveText("重试")
                        .onPositive((dialog, which) -> {
                            viewModel.initFingerDevice();
                            dialog.dismiss();
                        })
                        .negativeText("退出")
                        .onNegative((dialog, which) -> {
                            dialog.dismiss();
                            onBackPressed();
                        })
                        .autoDismiss(false)
                        .show();
                break;
            case LOADING:
                mListener.showWaitDialog("正在初始化指纹模块");
                break;
            case SUCCESS:
                mListener.dismissWaitDialog();
                viewModel.registerFeature();
                break;
        }
    };

    private Observer<Boolean> fingerImageUpdateObserver = update -> {
        Glide.with(this)
                .load(viewModel.fingerImageCache)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.ivFinger);
    };

    public void setMark(String mark) {
        this.mark = mark;
    }
}
