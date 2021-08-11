package com.miaxis.postal.view.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Customer;
import com.miaxis.postal.databinding.FragmentCustomersDialogBinding;
import com.miaxis.postal.view.adapter.CustomerAdapter;
import com.miaxis.postal.view.adapter.SpacesItemDecoration;
import com.miaxis.postal.view.base.BaseViewModelDialogFragment;
import com.miaxis.postal.view.fragment.CardFragment;
import com.miaxis.postal.view.fragment.ManualFragment;
import com.miaxis.postal.viewModel.CustomersViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class CustomersDialogFragment extends BaseViewModelDialogFragment<FragmentCustomersDialogBinding, CustomersViewModel> implements CustomerAdapter.OnBodyClickListener {

    private CustomerAdapter branchAdapter;

    private final List<Customer> customers = new ArrayList<>();
    private boolean isAgreementCustomer;
    private boolean isNoCard;

    public static CustomersDialogFragment newInstance(boolean isAgreementCustomer, boolean isNoCard, List<Customer> customers) {
        CustomersDialogFragment branchSelectDialogFragment = new CustomersDialogFragment();
        branchSelectDialogFragment.setCustomers(customers);
        branchSelectDialogFragment.setAgreementCustomer(isAgreementCustomer);
        branchSelectDialogFragment.setNoCard(isNoCard);
        return branchSelectDialogFragment;
    }

    private CustomersDialogFragment() {
        // Required empty public constructor
    }

    public void setCustomers(List<Customer> customers) {
        if (customers != null && !customers.isEmpty()) {
            this.customers.clear();
            this.customers.addAll(customers);
        }
    }

    public void setNoCard(boolean noCard) {
        isNoCard = noCard;
    }

    public void setAgreementCustomer(boolean agreementCustomer) {
        isAgreementCustomer = agreementCustomer;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_customers_dialog;
    }

    @Override
    protected CustomersViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(CustomersViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void initData() {
        //        viewModel.idCardSearch.observe(this, idCardObserver);
    }

    @Override
    protected void initView() {
        binding.tvCreate.setOnClickListener(v -> skip(null));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvBranches.setLayoutManager(linearLayoutManager);
        branchAdapter = new CustomerAdapter(getContext());
        branchAdapter.setBodyListener(this);
        binding.rvBranches.setAdapter(branchAdapter);
        binding.rvBranches.addItemDecoration(new SpacesItemDecoration(2));
        ((SimpleItemAnimator) binding.rvBranches.getItemAnimator()).setSupportsChangeAnimations(false);
        branchAdapter.setDataList(customers);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.CENTER;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = (int) (dm.widthPixels * 0.8);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }

    @Override
    public void onBodyClick(View view, Customer customer, int position) {
        skip(customer);
        //        ValueUtil.GlobalPhone = this.userName;
        ////        SPUtils.getInstance().write(ValueUtil.GlobalPhone, branch.orgCode);
        ////        SPUtils.getInstance().write(ValueUtil.GlobalPhone+"node", branch.orgNode);
        //        ValueUtil.write(branch.orgCode,branch.orgNode,branch.orgName);
        //        App.getInstance().getThreadExecutor().execute(() -> {
        //            CourierModel.setLogin();
        //            mHandler.post(() -> mListener.replaceFragment(HomeFragment.newInstance()));
        //        });
    }

    private void skip(Customer customer) {
        if (isNoCard) {
            mListener.replaceFragment(ManualFragment.newInstance(null, this.isAgreementCustomer, customer));
        } else {
            mListener.replaceFragment(CardFragment.newInstance(isAgreementCustomer, customer));
        }
        dismiss();
    }
}
