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

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.databinding.FragmentOrderBinding;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.ScanManager;
import com.miaxis.postal.view.adapter.OrderAdapter;
import com.miaxis.postal.view.adapter.SpacesItemDecoration;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.OrderViewModel;
import com.scandecode.ScanDecode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends BaseViewModelFragment<FragmentOrderBinding, OrderViewModel> {

    public static final int REQUEST_CODE = 11;

    private OrderAdapter orderAdapter;
    private MaterialDialog scanDialog;

    private Uri imageUriCache = null;
    private Order orderCache = null;

    public static OrderFragment newInstance() {
        return new OrderFragment();
    }

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_order;
    }

    @Override
    protected OrderViewModel initViewModel() {
        return ViewModelProviders.of(this).get(OrderViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        getLifecycle().addObserver(viewModel);
    }

    @Override
    protected void initView() {
        initDialog();
        if (orderAdapter == null) {
            orderAdapter = new OrderAdapter(getContext(), viewModel);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.rvOrder.setLayoutManager(gridLayoutManager);
        binding.rvOrder.setAdapter(orderAdapter);
        binding.rvOrder.addItemDecoration(new SpacesItemDecoration(5));
        ((SimpleItemAnimator) binding.rvOrder.getItemAnimator()).setSupportsChangeAnimations(false);
        orderAdapter.setHeaderListener(() -> {
            if (viewModel.verifyResult) {
                scanDialog.show();
                viewModel.startScan();
            }
        });
        viewModel.orderList.observe(this, orderListObserver);
        viewModel.newOrder.observe(this, newOrderObserver);
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
        scanDialog.dismiss();
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUriCache = CameraManager.getOutputMediaFileUri(getContext());
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriCache);
        //Android7.0添加临时权限标记，此步千万别忘了
        openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        getActivity().startActivityForResult(openCameraIntent, REQUEST_CODE);
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

}
