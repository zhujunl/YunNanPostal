package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.data.entity.SimpleOrder;
import com.miaxis.postal.databinding.FragmentLogisticsBinding;
import com.miaxis.postal.view.adapter.EndLessOnScrollListener;
import com.miaxis.postal.view.adapter.ExpressAdapter;
import com.miaxis.postal.view.adapter.OrderAdapter;
import com.miaxis.postal.view.adapter.SpacesItemDecoration;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.LogisticsViewModel;

import java.util.List;

public class LogisticsFragment extends BaseViewModelFragment<FragmentLogisticsBinding, LogisticsViewModel> {

    private OrderAdapter orderAdapter;
    private EndLessOnScrollListener scrollListener;

    private String filter = "";
    private boolean refreshFlag = true;

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
        return ViewModelProviders.of(this).get(LogisticsViewModel.class);
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
        if (orderAdapter == null) {
            orderAdapter = new OrderAdapter(getContext());
        }
        if (scrollListener == null) {
            //TODO:BUG HERE
            scrollListener = new EndLessOnScrollListener() {
                @Override
                public void onLoadMore(int currentPage) {
                    Log.e("asd", currentPage + "");
                    viewModel.getOrderByCodeAndName(filter, currentPage);
                }
            };
        }
        orderAdapter.setListener(adapterListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        scrollListener.setLinearLayoutManager(layoutManager);
        scrollListener.reset();
        binding.rvOrder.clearOnScrollListeners();
        binding.rvOrder.addOnScrollListener(scrollListener);
        binding.rvOrder.setLayoutManager(layoutManager);
        binding.rvOrder.setAdapter(orderAdapter);
        ((SimpleItemAnimator) binding.rvOrder.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.srlOrder.setOnRefreshListener(this::refresh);
        initSearchView();
        viewModel.orderList.observe(this, orderObserver);
        viewModel.orderDetail.observe(this, orderDetailObserver);
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.orderList.removeObserver(orderObserver);
    }

    private void initSearchView() {
        binding.svSearch.setQueryHint("请输入寄件人姓名或快递单号...");
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

        @Override
        public void onThumbnail(String url) {
            mListener.replaceFragment(PhotoFragment.newInstance(url));
        }
    };

    private Observer<List<SimpleOrder>> orderObserver = orderList -> {
        if (binding.srlOrder.isRefreshing()) {
            binding.srlOrder.setRefreshing(false);
        }
        if (refreshFlag) {
            refreshFlag = false;
            updateScrollListener();
            orderAdapter.setDataList(orderList);
            binding.rvOrder.scrollToPosition(0);
        } else {
            int itemCount = orderAdapter.getItemCount();
            orderAdapter.appendDataList(orderList);
            binding.rvOrder.scrollToPosition(itemCount);
        }
    };

    private Observer<Order> orderDetailObserver = order -> {
        mListener.replaceFragment(OrderFragment.newInstance(order));
    };

    private void updateScrollListener() {
        binding.rvOrder.clearOnScrollListeners();
        scrollListener.reset();
        binding.rvOrder.addOnScrollListener(scrollListener);
    }

    private void refresh() {
        refreshFlag = true;
        viewModel.getOrderByCodeAndName(filter, 1);
    }

}
