package com.miaxis.postal.view.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.miaxis.postal.R;
import com.miaxis.postal.databinding.FragmentCameraBinding;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.custom.RoundFrameLayout;
import com.miaxis.postal.viewModel.CameraViewModel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class CameraFragment extends BaseViewModelFragment<FragmentCameraBinding, CameraViewModel> {

    private RoundFrameLayout roundFrameLayout;

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_camera;
    }

    @Override
    protected CameraViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(CameraViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.thumbnail.observe(this, thumbnailObserver);
        viewModel.confirmFlag.observe(this, confirmFlagObserver);
    }

    @Override
    protected void initView() {
        binding.tvCamera.getViewTreeObserver().addOnGlobalLayoutListener(globalListener);
        binding.ivTakePhoto.setOnClickListener(new OnLimitClickHelper(view -> viewModel.takePicture()));
        binding.ivConfirm.setOnClickListener(new OnLimitClickHelper(view -> viewModel.confirmPicture()));
        binding.ivRetry.setOnClickListener(new OnLimitClickHelper(view -> viewModel.retry()));
        binding.ivThumbnail.setOnClickListener(new OnLimitClickHelper(view -> onBackPressed()));
    }

    @Override
    public void onBackPressed() {
        viewModel.summary();
        mListener.backToStack(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CameraManager.getInstance().closeCamera();
    }

    private Observer<Bitmap> thumbnailObserver = bitmap -> {
        if (bitmap == null) {
            Glide.with(CameraFragment.this).clear(binding.ivThumbnail);
        } else {
            Glide.with(CameraFragment.this)
                    .load(bitmap)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.ivThumbnail);
        }
    };

    private Observer<Boolean> confirmFlagObserver = flag -> {
        if (flag) {
            onBackPressed();
        }
    };

    private ViewTreeObserver.OnGlobalLayoutListener globalListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            binding.tvCamera.getViewTreeObserver().removeOnGlobalLayoutListener(globalListener);
            ViewGroup.LayoutParams layoutParams = binding.tvCamera.getLayoutParams();
            layoutParams.width = binding.flCamera.getWidth();
            layoutParams.height = binding.flCamera.getHeight();
            binding.tvCamera.setLayoutParams(layoutParams);
            CameraManager.getInstance().resetRetryTime();
            CameraManager.getInstance().openBackCamera(binding.tvCamera, cameraListener);
        }
    };

    private CameraManager.OnCameraOpenListener cameraListener = previewSize -> {
        FrameLayout.LayoutParams textureViewLayoutParams = (FrameLayout.LayoutParams) binding.tvCamera.getLayoutParams();
        int newHeight = textureViewLayoutParams.width * previewSize.width / previewSize.height;
        int newWidth = textureViewLayoutParams.width;

        roundFrameLayout = new RoundFrameLayout(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(newWidth, newHeight);
        roundFrameLayout.setLayoutParams(layoutParams);
        roundFrameLayout.setBackgroundColor(Color.BLACK);
        FrameLayout parentView = (FrameLayout) binding.tvCamera.getParent();
        parentView.removeView(binding.tvCamera);
        parentView.addView(roundFrameLayout);

        roundFrameLayout.addView(binding.tvCamera);
        FrameLayout.LayoutParams newTextureViewLayoutParams = new FrameLayout.LayoutParams(newWidth, newHeight);
        newTextureViewLayoutParams.topMargin = -(newHeight - newWidth) / 2;
        binding.tvCamera.setLayoutParams(newTextureViewLayoutParams);
    };

}
