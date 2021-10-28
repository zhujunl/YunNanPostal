package com.miaxis.postal.view.fragment;

import android.os.Handler;

import com.google.android.material.tabs.TabLayoutMediator;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.databinding.FragmentRecordTabBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.LoginViewModel;
import com.miaxis.postal.viewModel.RecordTabViewModel;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class RecordTabFragment extends BaseViewModelFragment<FragmentRecordTabBinding, RecordTabViewModel> {

    public static RecordTabFragment newInstance() {
        return new RecordTabFragment();
    }

    public RecordTabFragment() {
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
                                .replace(R.id.record_tab, new LoginFragment(), null)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });

        }
    };


    @Override
    protected int setContentView() {
        return R.layout.fragment_record_tab;
    }

    @Override
    protected RecordTabViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(RecordTabViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void initData() {
        //进入延时状态,一小时访问一次接口
        deviceHandler.postDelayed(task,3600000);//延迟调用
    }

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.vpPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return DraftFragment.newInstance();
                } else if (position == 1) {
                    return LocalFragment.newInstance();
                } else {
                    return RecordSearchFragment.newInstance();
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });
        new TabLayoutMediator(binding.tlPager, binding.vpPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("草稿箱");
            } else if (position == 1) {
                tab.setText("未上传订单");
            } else {
                tab.setText("联网查询");
            }
        }).attach();
        binding.vpPager.setOffscreenPageLimit(1);

        try {
            final Field recyclerViewField = ViewPager2.class.getDeclaredField("mRecyclerView");
            recyclerViewField.setAccessible(true);
            final RecyclerView recyclerView = (RecyclerView) recyclerViewField.get(binding.vpPager);//vb.viewpagerHome为要改变滑动距离的viewpager2控件
            final Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
            touchSlopField.setAccessible(true);
            // final int touchSlop = (int) touchSlopField.get(recyclerView);
            // touchSlopField.set(recyclerView, touchSlop*4);//通过获取原有的最小滑动距离 *n来增加此值
            touchSlopField.set(recyclerView, 150);//自己写一个值
        } catch (Exception ignore) {
        }
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

}
