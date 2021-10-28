package com.miaxis.postal.view.fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.data.event.FaceRegisterEvent;
import com.miaxis.postal.data.event.FingerRegisterEvent;
import com.miaxis.postal.databinding.FragmentRegisterBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.LoginViewModel;
import com.miaxis.postal.viewModel.RegisterViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RegisterFragment extends BaseViewModelFragment<FragmentRegisterBinding, RegisterViewModel> {

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    public RegisterFragment() {
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
                                .replace(R.id.register, new LoginFragment(), null)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });

        }
    };

    @Override
    protected int setContentView() {
        return R.layout.fragment_register;
    }

    @Override
    protected RegisterViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(RegisterViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.registerFlag.observe(this, flag -> {
            onBackPressed();
        });
        //进入延时状态,一小时访问一次接口
        deviceHandler.postDelayed(task,3600000);//延迟调用
    }

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.tvHeader.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(FaceRegisterFragment.newInstance())));
        binding.tvFinger1.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(FingerRegisterFragment.newInstance(RegisterViewModel.FINGER1))));
        binding.tvFinger2.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(FingerRegisterFragment.newInstance(RegisterViewModel.FINGER2))));
        binding.btnRegister.setOnClickListener(v -> {
            if (viewModel.checkInput()) {
                viewModel.getCourierByPhone();
            } else {
                ToastManager.toast("请输入全部信息", ToastManager.INFO);
            }
        });
        EventBus.getDefault().register(this);
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onFaceRegisterEvent(FaceRegisterEvent event) {
        viewModel.faceFeatureHint.set("已采集");
        binding.tvHeader.setOnClickListener(null);
        viewModel.setFeatureCache(event.getFeature());
        viewModel.setMaskFeatureCache(event.getMaskFeature());
        viewModel.setHeaderCache(event.getBitmap());
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onFingerRegisterEvent(FingerRegisterEvent event) {
        String feature = event.getFeature();
        if (TextUtils.isEmpty(feature)) return;
        if (TextUtils.equals(RegisterViewModel.FINGER1, event.getMark())) {
            viewModel.finger1FeatureHint.set("已采集");
            binding.tvFinger1.setOnClickListener(null);
            viewModel.setFingerFeature1(feature);
        } else {
            viewModel.finger2FeatureHint.set("已采集");
            binding.tvFinger2.setOnClickListener(null);
            viewModel.setFingerFeature2(feature);
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
