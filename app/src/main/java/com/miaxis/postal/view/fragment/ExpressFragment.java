package com.miaxis.postal.view.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.data.event.ExpressEditEvent;
import com.miaxis.postal.databinding.FragmentExpressBinding;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.view.adapter.ExpressAdapter;
import com.miaxis.postal.view.adapter.SpacesItemDecoration;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.auxiliary.OnLimitClickListener;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.ExpressViewModel;
import com.speedata.libid2.IDInfor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class ExpressFragment extends BaseViewModelFragment<FragmentExpressBinding, ExpressViewModel> {

    private IDCardRecord idCardRecord;

    private ExpressAdapter expressAdapter;
    private MaterialDialog scanDialog;

    public static ExpressFragment newInstance(IDCardRecord idCardRecord) {
        ExpressFragment fragment = new ExpressFragment();
        fragment.setIdCardRecord(idCardRecord);
        return fragment;
    }

    public ExpressFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_express;
    }

    @Override
    protected ExpressViewModel initViewModel() {
        return ViewModelProviders.of(this).get(ExpressViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.idCardRecord.set(idCardRecord);
    }

    @Override
    protected void initView() {
        initDialog();
        initRecycleView();
        binding.ivAddress.setOnClickListener(v -> viewModel.getLocation());
        binding.fabConfirm.setOnClickListener(confirmClickListener);
        viewModel.expressList.observe(this, expressListObserver);
        viewModel.newExpress.observe(this, newExpressObserver);
        viewModel.repeatExpress.observe(this, repeatExpressObserver);
        viewModel.stopScanFlag.observe(this, stopScanFlagObserver);
        viewModel.uploadFlag.observe(this, uploadFlagObserver);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onExpressEditEvent(ExpressEditEvent event) {
        if (event.getMode() == ExpressEditEvent.MODE_MODIFY) {
            viewModel.modifyExpress(event.getExpress());
        } else if (event.getMode() == ExpressEditEvent.MODE_DELETE) {
            viewModel.deleteExpress(event.getExpress());
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(getContext())
                .title("确认离开页面？")
                .positiveText("确认")
                .onPositive((dialog, which) -> mListener.backToStack(HomeFragment.class))
                .negativeText("取消")
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.expressList.removeObserver(expressListObserver);
        EventBus.getDefault().unregister(this);
    }

    private void initDialog() {
        scanDialog = new MaterialDialog.Builder(getContext())
                .title("新建订单")
                .progress(true, 100)
                .content("请将扫描口对准条码进行扫描")
                .positiveText("取消扫描")
                .onPositive((dialog, which) -> viewModel.stopScan())
                .build();
    }

    private void initRecycleView() {
        expressAdapter = new ExpressAdapter(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.rvExpress.setLayoutManager(gridLayoutManager);
        binding.rvExpress.setAdapter(expressAdapter);
        binding.rvExpress.addItemDecoration(new SpacesItemDecoration(1));
        ((SimpleItemAnimator) binding.rvExpress.getItemAnimator()).setSupportsChangeAnimations(false);
        expressAdapter.setHeaderListener(headerListener);
        expressAdapter.setBodyListener(bodyListener);
    }

    private ExpressAdapter.OnHeaderClickListener headerListener = () -> {
        scanDialog.show();
        viewModel.startScan();
    };

    private ExpressAdapter.OnBodyClickListener bodyListener = (view, position) -> {
        mListener.replaceFragment(InspectFragment.newInstance(expressAdapter.getData(position - 1)));
    };

    private Observer<List<Express>> expressListObserver = expressList -> {
        expressAdapter.setDataList(expressList);
        expressAdapter.notifyDataSetChanged();
    };

    private Observer<Boolean> stopScanFlagObserver = flag -> scanDialog.dismiss();

    private Observer<Express> newExpressObserver = express -> {
        scanDialog.dismiss();
        mListener.replaceFragment(InspectFragment.newInstance(express));
    };

    private Observer<String> repeatExpressObserver = code -> {
        scanDialog.dismiss();
        if (TextUtils.isEmpty(code)) {
            ToastManager.toast("该条码编号已重复", ToastManager.INFO);
        } else {
            Express expressByCode = viewModel.getExpressByCode(code);
            if (expressByCode != null) {
                mListener.replaceFragment(InspectFragment.newInstance(expressByCode));
            } else {
                ToastManager.toast("该条码编号已重复", ToastManager.INFO);
            }
        }
    };

    private Observer<Boolean> uploadFlagObserver = flag -> mListener.backToStack(HomeFragment.class);

    private View.OnClickListener confirmClickListener = new OnLimitClickHelper(view -> {
        if (!viewModel.checkInput()) {
            ToastManager.toast("请输入寄件人手机号码和寄件地址", ToastManager.INFO);
            return;
        }
        if (viewModel.getExpressList().isEmpty()) {
            ToastManager.toast("请至少完成一个订单", ToastManager.INFO);
            return;
        }
        viewModel.uploadExpress();
    });

    public void setIdCardRecord(IDCardRecord idCardRecord) {
        this.idCardRecord = idCardRecord;
    }
}
