package com.miaxis.postal.view.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.miaxis.postal.R;
import com.miaxis.postal.data.event.FaceRegisterEvent;
import com.miaxis.postal.data.event.FingerRegisterEvent;
import com.miaxis.postal.databinding.FragmentRegisterBinding;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.view.base.BaseViewModelFragment;
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

    @Override
    protected int setContentView() {
        return R.layout.fragment_register;
    }

    @Override
    protected RegisterViewModel initViewModel() {
        return ViewModelProviders.of(this).get(RegisterViewModel.class);
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
    }

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.tvHeader.setOnClickListener(v -> mListener.replaceFragment(FaceRegisterFragment.newInstance()));
        binding.tvFinger1.setOnClickListener(v -> mListener.replaceFragment(FingerRegisterFragment.newInstance(RegisterViewModel.FINGER1)));
        binding.tvFinger2.setOnClickListener(v -> mListener.replaceFragment(FingerRegisterFragment.newInstance(RegisterViewModel.FINGER2)));
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
