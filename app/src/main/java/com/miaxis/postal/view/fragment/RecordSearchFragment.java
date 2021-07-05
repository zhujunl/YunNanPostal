package com.miaxis.postal.view.fragment;

import android.text.TextUtils;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.databinding.FragmentRecordSearchBinding;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.RecordSearchViewModel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class RecordSearchFragment extends BaseViewModelFragment<FragmentRecordSearchBinding, RecordSearchViewModel> {

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
        viewModel.searchOrder.observe(this, orderObserver);
    }

    @Override
    protected void initView() {
        binding.tvSearch.setOnClickListener(new OnLimitClickHelper(view -> {
            String orderCode = binding.etSearch.getText().toString();
            if (TextUtils.isEmpty(orderCode)) {
                ToastManager.toast("请输入查询内容", ToastManager.INFO);
                return;
            }
            if (!binding.tvHint.getText().toString().contains("查询中")) {
                viewModel.getOrderById(orderCode);
            }
        }));
        //binding.etSearch.setRawInputType(Configuration.KEYBOARD_QWERTY);
        //        binding.etSearch.setText("7302289336");
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

    private Observer<Order> orderObserver = order -> {
        if (order != null) {
            mListener.replaceFragment(RecordUpdateFragment.newInstance(order));
        }
    };

}
