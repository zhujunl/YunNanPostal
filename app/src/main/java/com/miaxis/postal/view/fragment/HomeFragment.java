package com.miaxis.postal.view.fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.app.App;
import com.miaxis.postal.databinding.FragmentHomeBinding;
import com.miaxis.postal.manager.AmapManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.dialog.CardModeSelectDialogFragment;
import com.miaxis.postal.view.dialog.EditPasswordDialogFragment;
import com.miaxis.postal.viewModel.HomeViewModel;

import androidx.lifecycle.ViewModelProvider;

public class HomeFragment extends BaseViewModelFragment<FragmentHomeBinding, HomeViewModel> {

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_home;
    }

    @Override
    protected HomeViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(HomeViewModel.class);
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
        binding.ivConfig.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(ConfigFragment.newInstance())));
//        binding.clExpress.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(CardFragment.newInstance())));
        binding.clExpress.setOnClickListener(new OnLimitClickHelper(view -> {
            CardModeSelectDialogFragment.newInstance().show(getChildFragmentManager(), "CardModeSelectDialogFragment");
        }));
        binding.clRecord.setOnClickListener(new OnLimitClickHelper(view -> mListener.replaceFragment(RecordTabFragment.newInstance())));
        binding.tvEditPassword.setOnClickListener(new OnLimitClickHelper(view -> {
            EditPasswordDialogFragment.newInstance().show(getChildFragmentManager(), "EditPasswordDialogFragment");
        }));
        AmapManager.getInstance().startLocation(getActivity().getApplication());//GPS初始化，登录后初始化
        App.getInstance().uploadEnable=true;
    }

    @Override
    public void onResume() {
        super.onResume();
        PostalManager.getInstance().startPostal();
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(getContext())
                .title("确认退出登录？")
                .positiveText("确认")
                .onPositive((dialog, which) -> mListener.backToStack(LoginFragment.class))
                .negativeText("取消")
                .show();
    }
}
