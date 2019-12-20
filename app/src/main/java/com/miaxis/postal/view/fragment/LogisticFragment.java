package com.miaxis.postal.view.fragment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.databinding.FragmentLogisticBinding;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.LogisticViewModel;

public class LogisticFragment extends BaseViewModelFragment<FragmentLogisticBinding, LogisticViewModel> {

    private IdentityFragment identityFragment = IdentityFragment.newInstance();
    private OrderFragment orderFragment = OrderFragment.newInstance();

    public static LogisticFragment newInstance() {
        return new LogisticFragment();
    }

    public LogisticFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_logistic;
    }

    @Override
    protected LogisticViewModel initViewModel() {
        return ViewModelProviders.of(this).get(LogisticViewModel.class);
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
        getChildFragmentManager().beginTransaction()
                .replace(R.id.cl_identity, identityFragment)
                .replace(R.id.cl_order, orderFragment)
                .commit();
    }
}
