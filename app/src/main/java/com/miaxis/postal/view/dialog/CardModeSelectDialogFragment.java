package com.miaxis.postal.view.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.databinding.FragmentCardModeSelectDialogBinding;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelDialogFragment;
import com.miaxis.postal.view.fragment.CardFragment;
import com.miaxis.postal.view.fragment.ManualFragment;
import com.miaxis.postal.viewModel.CardModeSelectViewModel;

import androidx.lifecycle.ViewModelProvider;

public class CardModeSelectDialogFragment extends BaseViewModelDialogFragment<FragmentCardModeSelectDialogBinding, CardModeSelectViewModel> {

    private boolean isAgreementCustomer = false;
    //    private boolean isNoCard = false;

    public static CardModeSelectDialogFragment newInstance(boolean isAgreementCustomer) {
        CardModeSelectDialogFragment fragment = new CardModeSelectDialogFragment();
        fragment.setState(isAgreementCustomer);
        return fragment;
    }

    public CardModeSelectDialogFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_card_mode_select_dialog;
    }

    @Override
    protected CardModeSelectViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(CardModeSelectViewModel.class);
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
        //        viewModel.itemList.observe(this, new Observer<List<Customer>>() {
        //            @Override
        //            public void onChanged(List<Customer> customers) {
        //                Log.e("CardModeSelectDialog", "" + customers);
        //                if (ListUtils.isNullOrEmpty(customers)) {
        //                    if (isNoCard) {
        //                        mListener.replaceFragment(ManualFragment.newInstance(null, isAgreementCustomer, null));
        //                    } else {
        //                        mListener.replaceFragment(CardFragment.newInstance(isAgreementCustomer, null));
        //                    }
        //                } else {
        //                    CustomersDialogFragment.newInstance(isAgreementCustomer, isNoCard, customers).show(getParentFragmentManager(), "CustomersDialogFragment");
        //                }
        //                dismiss();
        //            }
        //        });
        binding.tvHasCard.setOnClickListener(new OnLimitClickHelper(view -> {
            //            if (isAgreementCustomer) {
            //                isNoCard = false;
            //                viewModel.show(ValueUtil.GlobalPhone);
            //            } else {
            //                mListener.replaceFragment(CardFragment.newInstance(false, null));
            //                dismiss();
            //            }
            mListener.replaceFragment(CardFragment.newInstance(isAgreementCustomer, null));
            dismiss();
        }));
        binding.tvNoCard.setOnClickListener(new OnLimitClickHelper(view -> {
            //            if (isAgreementCustomer) {
            //                isNoCard = true;
            //                viewModel.show(ValueUtil.GlobalPhone);
            //            } else {
            //                mListener.replaceFragment(ManualFragment.newInstance(null, false, null));
            //                dismiss();
            //            }
            mListener.replaceFragment(ManualFragment.newInstance(null, isAgreementCustomer, null));
            dismiss();
        }));
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

    //    private Observer<Boolean> idCardObserver = idCard -> {
    //        if (idCard) {
    //            mListener.replaceFragment(FaceVerifyFragment.newInstance(viewModel.idCardRecord));
    //        } else {
    //            mListener.replaceFragment(ManualFragment.newInstance(binding.etCardNumber.getText().toString()));
    //        }
    //        dismiss();
    //    };

    public void setState(boolean isAgreementCustomer) {
        this.isAgreementCustomer = isAgreementCustomer;
    }

}
