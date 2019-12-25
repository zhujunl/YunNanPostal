package com.miaxis.postal.view.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.databinding.FragmentExpressBinding;
import com.miaxis.postal.manager.AmapManager;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.view.adapter.OrderAdapter;
import com.miaxis.postal.view.adapter.SpacesItemDecoration;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.ExpressViewModel;
import com.speedata.libid2.IDInfor;

import java.util.List;

public class ExpressFragment extends BaseViewModelFragment<FragmentExpressBinding, ExpressViewModel> {

    public static final int REQUEST_CODE = 11;

    private IDInfor idInfor;
    private Bitmap header;
    private TempIdDto tempIdDto;

    private OrderAdapter orderAdapter;
    private MaterialDialog scanDialog;

    private Uri imageUriCache = null;
    private Order orderCache = null;

    public static ExpressFragment newInstance(IDInfor idInfor, Bitmap header, TempIdDto tempIdDto) {
        ExpressFragment fragment = new ExpressFragment();
        fragment.setIdInfor(idInfor);
        fragment.setHeader(header);
        fragment.setTempIdDto(tempIdDto);
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
        viewModel.idInfor.set(idInfor);
        viewModel.header.set(header);
        viewModel.tempIdDto.set(tempIdDto);
    }

    @Override
    protected void initView() {
        initDialog();
        if (orderAdapter == null) {
            orderAdapter = new OrderAdapter(getContext(), viewModel);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.rvOrder.setLayoutManager(gridLayoutManager);
        binding.rvOrder.setAdapter(orderAdapter);
        binding.rvOrder.addItemDecoration(new SpacesItemDecoration(1));
        ((SimpleItemAnimator) binding.rvOrder.getItemAnimator()).setSupportsChangeAnimations(false);
        orderAdapter.setHeaderListener(() -> {
//            if (viewModel.checkInput()) {
//                if (binding.etPhone.isEnabled() && viewModel.getOrderList().size() == 0) {
//                    new MaterialDialog.Builder(getContext())
//                            .title("确认输入")
//                            .content("确认寄件人手机号码以及寄件地址输入正确？\n(确认后不可修改)")
//                            .positiveText("确认")
//                            .onPositive((dialog, which) -> {
//                                binding.etPhone.setEnabled(false);
//                                binding.etAddress.setEnabled(false);
//                                scanDialog.show();
//                                viewModel.startScan();
//                            })
//                            .negativeText("取消")
//                            .show();
//                } else {
                    scanDialog.show();
                    viewModel.startScan();
//                }
//            } else {
//                ToastManager.toast("请先输入寄件人手机号码以及寄件地址", ToastManager.INFO);
//            }
        });
        binding.ivAddress.setOnClickListener(v -> viewModel.getLocation());
        binding.fabConfirm.setOnClickListener(v -> {
            new MaterialDialog.Builder(getContext())
                    .title("确认离开")
                    .content("确认已完成该寄件人名下的所有订单了吗？")
                    .positiveText("确认")
                    .onPositive((dialog, which) -> mListener.backToStack(HomeFragment.class))
                    .negativeText("取消")
                    .show();
        });
        viewModel.orderList.observe(this, orderListObserver);
        viewModel.newOrder.observe(this, newOrderObserver);
        viewModel.repeatOrder.observe(this, repeatOrderObserver);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (imageUriCache != null && orderCache != null) {
                Bitmap bitmapFormUri = CameraManager.getBitmapFormUri(getContext(), imageUriCache);
                if (bitmapFormUri != null) {
                    viewModel.addOrder(orderCache, bitmapFormUri);
                    imageUriCache = null;
                    orderCache = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.orderList.removeObserver(orderListObserver);
    }

    private Observer<List<Order>> orderListObserver = orderList -> {
        orderAdapter.notifyDataSetChanged();
    };

    private Observer<Order> newOrderObserver = order -> {
        this.orderCache = order;
        this.imageUriCache = null;
        scanDialog.dismiss();
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUriCache = CameraManager.getOutputMediaFileUri(getContext());
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriCache);
        //Android7.0添加临时权限标记，此步千万别忘了
        openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        getActivity().startActivityForResult(openCameraIntent, REQUEST_CODE);
    };

    private Observer<Order> repeatOrderObserver = order -> {
        this.orderCache = null;
        this.imageUriCache = null;
        scanDialog.dismiss();
        ToastManager.toast("该条码编号已重复", ToastManager.INFO);
    };

    private void initDialog() {
        scanDialog = new MaterialDialog.Builder(getContext())
                .title("新建订单")
                .progress(true, 100)
                .content("请将扫描口对准条码进行扫描")
                .positiveText("取消扫描")
                .onPositive((dialog, which) -> viewModel.stopScan())
                .build();
    }

    public void setIdInfor(IDInfor idInfor) {
        this.idInfor = idInfor;
    }

    public void setHeader(Bitmap header) {
        this.header = header;
    }

    public void setTempIdDto(TempIdDto tempIdDto) {
        this.tempIdDto = tempIdDto;
    }
}
