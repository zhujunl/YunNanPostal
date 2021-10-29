package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.data.entity.SimpleOrder;
import com.miaxis.postal.databinding.FragmentLogisticsBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.adapter.EndLessOnScrollListener;
import com.miaxis.postal.view.adapter.ExpressAdapter;
import com.miaxis.postal.view.adapter.OrderAdapter;
import com.miaxis.postal.view.adapter.SpacesItemDecoration;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.LoginViewModel;
import com.miaxis.postal.viewModel.LogisticsViewModel;

import java.util.List;

public class LogisticsFragment extends BaseViewModelFragment<FragmentLogisticsBinding, LogisticsViewModel> {

    private OrderAdapter orderAdapter;
    private LinearLayoutManager layoutManager;

    private String filter = "";
    private int page = 1;
    private int localCount = 0;

    public static LogisticsFragment newInstance() {
        return new LogisticsFragment();
    }

    public LogisticsFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_logistics;
    }

    @Override
    protected LogisticsViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(LogisticsViewModel.class);
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
        initRecycleView();
        initSearchView();
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.srlOrder.setOnRefreshListener(this::refresh);
        binding.srlOrder.setColorSchemeResources(R.color.main_color,R.color.main_color_dark);
        viewModel.refreshing.observe(this, refreshingObserver);
        viewModel.orderList.observe(this, orderObserver);
        viewModel.orderDetail.observe(this, orderDetailObserver);
        refresh();
        binding.srlOrder.setRefreshing(true);
    }

    @Override
    public void onBackPressed() {
        hideInputMethod();
        mListener.backToStack(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.orderList.removeObserver(orderObserver);
    }

    private void initRecycleView() {
        orderAdapter = new OrderAdapter(getContext());
        orderAdapter.setListener(adapterListener);
        layoutManager = new LinearLayoutManager(getContext());
        binding.rvOrder.addOnScrollListener(onScrollListener);
        binding.rvOrder.setLayoutManager(layoutManager);
        binding.rvOrder.setAdapter(orderAdapter);
        ((SimpleItemAnimator) binding.rvOrder.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private void initSearchView() {
        binding.svSearch.setQueryHint("请输入姓名或单号");
        binding.svSearch.setSubmitButtonEnabled(true);
        binding.svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter = query;
                refresh();
                binding.svSearch.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        binding.svSearch.setOnCloseListener(() -> {
            filter = "";
            refresh();
            return false;
        });
    }

    private OrderAdapter.OnItemClickListener adapterListener = new OrderAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            viewModel.getOrderById(orderAdapter.getData(position));
        }
    };

    private Observer<List<SimpleOrder>> orderObserver = orderList -> {
        if (page == 1) {
            orderAdapter.setDataList(orderList);
            orderAdapter.notifyDataSetChanged();
            if (localCount == 0) {
                binding.rvOrder.scrollToPosition(0);
            }
            localCount = orderList.size();
        } else {
            orderAdapter.setDataList(orderList);
            orderAdapter.notifyItemRangeChanged(localCount, orderList.size() - localCount);
            if (localCount != 0) {
                binding.rvOrder.scrollToPosition(localCount);
            }
            localCount = orderList.size();
        }
    };

    private Observer<Order> orderDetailObserver = order -> {
        mListener.replaceFragment(OrderFragment.newInstance(order));
    };

    private Observer<Boolean> refreshingObserver = flag -> binding.srlOrder.setRefreshing(flag);

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        private boolean loadingMore = true;
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (!loadingMore && layoutManager.findLastVisibleItemPosition() + 1 == orderAdapter.getItemCount()) {
                    loadMore();
                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (loadingMore && layoutManager.findLastVisibleItemPosition() + 1 == orderAdapter.getItemCount()) {
                loadMore();
            } else if (loadingMore) {
                loadingMore = false;
            }
        }
    };

    private void refresh() {
        PostalManager.getInstance().startPostal();
        localCount = 0;
        viewModel.getOrderByCodeAndName(filter, page = 1);
    }

    private void loadMore() {
        viewModel.getOrderByCodeAndName(filter, ++page);
    }

}
