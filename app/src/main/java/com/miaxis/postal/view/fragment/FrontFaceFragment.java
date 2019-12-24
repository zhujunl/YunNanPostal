package com.miaxis.postal.view.fragment;

import android.hardware.Camera;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.databinding.FragmentFrontFaceBinding;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.custom.RoundBorderView;
import com.miaxis.postal.view.custom.RoundFrameLayout;
import com.miaxis.postal.viewModel.FrontFaceViewModel;

public class FrontFaceFragment extends BaseViewModelFragment<FragmentFrontFaceBinding, FrontFaceViewModel> {

    private Courier courier;

    private RoundBorderView roundBorderView;
    private RoundFrameLayout roundFrameLayout;

    public static FrontFaceFragment newInstance(Courier courier) {
        FrontFaceFragment fragment = new FrontFaceFragment();
        fragment.setCourier(courier);
        return fragment;
    }

    public FrontFaceFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_front_face;
    }

    @Override
    protected FrontFaceViewModel initViewModel() {
        return ViewModelProviders.of(this).get(FrontFaceViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.courierLiveData.setValue(courier);
        binding.rtvCamera.getViewTreeObserver().addOnGlobalLayoutListener(globalListener);
    }

    @Override
    protected void initView() {

    }

    private ViewTreeObserver.OnGlobalLayoutListener globalListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            binding.rtvCamera.getViewTreeObserver().removeOnGlobalLayoutListener(globalListener);
            ViewGroup.LayoutParams layoutParams = binding.rtvCamera.getLayoutParams();
            layoutParams.width = binding.flCamera.getWidth();
            layoutParams.height = binding.flCamera.getHeight();
            binding.rtvCamera.setLayoutParams(layoutParams);
            binding.rtvCamera.turnRound();
            CameraManager.getInstance().openFrontCamera(binding.rtvCamera, cameraListener);
        }
    };

    private CameraManager.OnCameraOpenListener cameraListener = previewSize -> {
        FrameLayout.LayoutParams textureViewLayoutParams = (FrameLayout.LayoutParams) binding.rtvCamera.getLayoutParams();
        int newHeight = textureViewLayoutParams.width * previewSize.width / previewSize.height;
        int newWidth = textureViewLayoutParams.width;

        roundFrameLayout = new RoundFrameLayout(getContext());
        int sideLength = Math.min(newWidth, newHeight);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(sideLength, sideLength);
        roundFrameLayout.setLayoutParams(layoutParams);
        FrameLayout parentView = (FrameLayout) binding.rtvCamera.getParent();
        parentView.removeView(binding.rtvCamera);
        parentView.addView(roundFrameLayout);

        roundFrameLayout.addView(binding.rtvCamera);
        FrameLayout.LayoutParams newTextureViewLayoutParams = new FrameLayout.LayoutParams(newWidth, newHeight);
        newTextureViewLayoutParams.topMargin = -(newHeight - newWidth) / 2;
        binding.rtvCamera.setLayoutParams(newTextureViewLayoutParams);

        View siblingView = roundFrameLayout != null ? roundFrameLayout : binding.rtvCamera;
        roundBorderView = new RoundBorderView(getContext());
        ((FrameLayout) siblingView.getParent()).addView(roundBorderView, siblingView.getLayoutParams());

        new Handler(Looper.getMainLooper()).post(() -> {
            roundFrameLayout.setRadius(Math.min(roundFrameLayout.getWidth(), roundFrameLayout.getHeight()) / 2);
            roundFrameLayout.turnRound();
            roundBorderView.setRadius(Math.min(roundBorderView.getWidth(), roundBorderView.getHeight()) / 2);
            roundBorderView.turnRound();
        });

    };

    public void setCourier(Courier courier) {
        this.courier = courier;
    }
}
