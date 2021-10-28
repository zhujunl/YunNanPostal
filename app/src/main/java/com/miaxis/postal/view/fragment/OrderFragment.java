package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.databinding.FragmentOrderBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.adapter.OrderImageAdapter;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.LoginViewModel;
import com.miaxis.postal.viewModel.OrderViewModel;

public class OrderFragment extends BaseViewModelFragment<FragmentOrderBinding, OrderViewModel> {

    private OrderImageAdapter orderImageAdapter;

    private Order order;

    public static OrderFragment newInstance(Order order) {
        OrderFragment fragment = new OrderFragment();
        fragment.setOrder(order);
        return fragment;
    }

    public OrderFragment() {
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
                                .replace(R.id.order, new LoginFragment(), null)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });

        }
    };

    @Override
    protected int setContentView() {
        return R.layout.fragment_order;
    }

    @Override
    protected OrderViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(OrderViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.order.setValue(order);
        viewModel.order.observe(this, orderObserver);
        //进入延时状态,一小时访问一次接口
        deviceHandler.postDelayed(task,3600000);//延迟调用
    }

    @Override
    protected void initView() {
        initRecycleView();
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.ivCardPicture.setOnClickListener(v -> {
            mListener.replaceFragment(PhotoFragment.newInstance(order.getCardImage()));
        });
        binding.ivFacePicture.setOnClickListener(v -> {
            mListener.replaceFragment(PhotoFragment.newInstance(order.getCheckImage()));
        });
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.order.removeObserver(orderObserver);
    }

    private void initRecycleView() {
        orderImageAdapter = new OrderImageAdapter(getContext());
        orderImageAdapter.setListener((view, position) -> {
            mListener.replaceFragment(PhotoFragment.newInstance(orderImageAdapter.getData(position)));
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        binding.rvImage.setLayoutManager(gridLayoutManager);
        binding.rvImage.setAdapter(orderImageAdapter);
        ((SimpleItemAnimator) binding.rvImage.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private Observer<Order> orderObserver = order -> {
        orderImageAdapter.setDataList(order.getImageList());
        orderImageAdapter.notifyDataSetChanged();
    };

    public void setOrder(Order order) {
        this.order = order;
    }
}
