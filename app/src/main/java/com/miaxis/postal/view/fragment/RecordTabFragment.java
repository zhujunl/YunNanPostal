package com.miaxis.postal.view.fragment;

import com.google.android.material.tabs.TabLayoutMediator;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.databinding.FragmentRecordTabBinding;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.RecordTabViewModel;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
