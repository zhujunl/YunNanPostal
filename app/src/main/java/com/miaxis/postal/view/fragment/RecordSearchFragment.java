package com.miaxis.postal.view.fragment;

import android.text.TextUtils;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.databinding.FragmentRecordSearchBinding;
import com.miaxis.postal.view.adapter.OrderListAdapter;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.RecordSearchViewModel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class RecordSearchFragment extends BaseViewModelFragment<FragmentRecordSearchBinding, RecordSearchViewModel> implements OrderListAdapter.OnClickListener, XRecyclerView.LoadingListener {


    private static final String TAG = "RecordSearchFragment";
    private OrderListAdapter orderListAdapter;

    public static RecordSearchFragment newInstance() {
        return new RecordSearchFragment();
    }

    public RecordSearchFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_record_search;
    }

    @Override
    protected RecordSearchViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(RecordSearchViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.SearchOrder.observe(this, orderObserver);
        viewModel.OrderList.observe(this, orders -> {
            if (orderListAdapter != null) {
                orderListAdapter.setDataList(orders);
            }
        });
        viewModel.RefreshComplete.observe(this, aBoolean -> binding.rvList.refreshComplete());
        viewModel.LoadMoreComplete.observe(this, aBoolean -> binding.rvList.loadMoreComplete());
        viewModel.ErrorMessage.observe(this, this::showResultDialog);
        viewModel.QueryFlag.observe(this, aBoolean -> {
            if (aBoolean){
                showWaitDialog("正在查询中，请稍后。");
            }else {
                dismissWaitDialog();
            }
        });
        viewModel.LoadMoreEnable.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.rvList.setLoadingMoreEnabled(aBoolean);
            }
        });
    }

    @Override
    protected void initView() {
        binding.tvSearch.setOnClickListener(new OnLimitClickHelper(view -> {
            if (!binding.tvHint.getText().toString().contains("查询中")) {
                hideInputMethod();
                binding.rvList.refresh();
            }
        }));
        orderListAdapter = new OrderListAdapter();
        orderListAdapter.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvList.setLayoutManager(linearLayoutManager);
        binding.rvList.setAdapter(orderListAdapter);
        binding.rvList.setLoadingListener(this);
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

    private final Observer<Order> orderObserver = order -> {
        if (order != null) {
            mListener.replaceFragment(RecordUpdateFragment.newInstance(order));
        }
    };

    @Override
    public void onItemClick(OrderListAdapter.BodyViewHolder view, Order order, int position) {
        if (order==null|| TextUtils.isEmpty(order.getOrderCode())){
            viewModel.ErrorMessage.setValue("订单号不能为空");
            return;
        }
      viewModel.getOrderById(order.getOrderCode());
    }

    @Override
    public void onRefresh() {
        viewModel.LoadMoreEnable.setValue(true);
        String orderCode = binding.etSearch.getText().toString();
        viewModel.CurrentPage.setValue(1);
        viewModel.getOrderByCode(orderCode);
    }

    @Override
    public void onLoadMore() {
        String orderCode = binding.etSearch.getText().toString();
        viewModel.getOrderByCode(orderCode);
    }

    @Override
    public void onDestroyView() {
        binding.rvList.destroy();
        super.onDestroyView();
    }
}
