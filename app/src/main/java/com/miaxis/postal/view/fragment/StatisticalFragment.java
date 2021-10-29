package com.miaxis.postal.view.fragment;

import android.content.DialogInterface;
import android.os.Handler;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.databinding.FragmentStatisticalBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.adapter.StatisticalAdapter;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.LoginViewModel;
import com.miaxis.postal.viewModel.StatisticalViewModel;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class StatisticalFragment extends BaseViewModelFragment<FragmentStatisticalBinding, StatisticalViewModel> implements XRecyclerView.LoadingListener {

    private static final String TAG = "Mx-StatisticalFragment";
    private final Handler mHandler = new Handler();
    private StatisticalAdapter statisticalAdapter;

    private StatisticalFragment() {
        // Required empty public constructor
    }


    public static StatisticalFragment newInstance() {
        return new StatisticalFragment();
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_statistical;
    }

    @Override
    protected StatisticalViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(StatisticalViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.itemList.observe(this, new Observer<List<StatisticalAdapter.StatisticalItem>>() {
            @Override
            public void onChanged(List<StatisticalAdapter.StatisticalItem> statisticalItems) {
                binding.rvItem.loadMoreComplete();
                binding.rvItem.refreshComplete();
                statisticalAdapter.setItemList(statisticalItems);
            }
        });
        viewModel.nextPageEnable.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.rvItem.setLoadingMoreEnabled(aBoolean);
            }
        });
        viewModel.emptyFlag.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    new AlertDialog.Builder(getContext()).setTitle("提示").setMessage("暂无数据")
                            .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            onBackPressed();
                        }
                    }).setNegativeButton("重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            binding.rvItem.refresh();
                        }
                    }).create().show();
                }
            }
        });
        onRefresh();
    }

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.rvItem.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        statisticalAdapter = new StatisticalAdapter();
        binding.rvItem.setAdapter(statisticalAdapter);
        binding.rvItem.setLoadingListener(this);
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(HomeFragment.class);
    }

    @Override
    public void onRefresh() {
        viewModel.refresh();
    }

    @Override
    public void onLoadMore() {
        viewModel.getList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.rvItem.destroy();
    }
}
