package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.databinding.FragmentRecordTabBinding;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.RecordTabViewModel;

public class RecordTabFragment extends BaseViewModelFragment<FragmentRecordTabBinding, RecordTabViewModel> {

    public static RecordTabFragment newInstance() {
        return new RecordTabFragment();
    }

    public RecordTabFragment() {
        // Required empty public constructor
    }

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

    }

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.vpPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return LocalFragment.newInstance();
                } else {
                    return RecordSearchFragment.newInstance();
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });
        new TabLayoutMediator(binding.tlPager, binding.vpPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("未上传订单");
            } else {
                tab.setText("联网查询");
            }
        }).attach();
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

}
