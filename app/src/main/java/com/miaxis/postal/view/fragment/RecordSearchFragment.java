package com.miaxis.postal.view.fragment;

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
import androidx.recyclerview.widget.RecyclerView;

public class RecordSearchFragment extends BaseViewModelFragment<FragmentRecordSearchBinding, RecordSearchViewModel> implements OrderListAdapter.OnClickListener {

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
            RecyclerView.Adapter<?> adapter = binding.rvList.getAdapter();
            if (adapter instanceof OrderListAdapter) {
                ((OrderListAdapter) adapter).setDataList(orders);
            }
        });
    }

    @Override
    protected void initView() {
        binding.tvSearch.setOnClickListener(new OnLimitClickHelper(view -> {
            String orderCode = binding.etSearch.getText().toString();
            //            if (TextUtils.isEmpty(orderCode)) {
            //                ToastManager.toast("请输入查询内容", ToastManager.INFO);
            //                return;
            //            }
            if (!binding.tvHint.getText().toString().contains("查询中")) {
                viewModel.getOrderById(orderCode);
            }
        }));
        //binding.etSearch.setRawInputType(Configuration.KEYBOARD_QWERTY);
        //binding.etSearch.setText("7302289336");
        OrderListAdapter orderListAdapter = new OrderListAdapter();
        orderListAdapter.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvList.setLayoutManager(linearLayoutManager);
        binding.rvList.setAdapter(orderListAdapter);
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
        viewModel.SearchOrder.setValue(order);
    }
}
