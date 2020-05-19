package com.miaxis.postal.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.databinding.FragmentFaceVerifyBinding;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.TTSManager;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.custom.RoundBorderView;
import com.miaxis.postal.view.custom.RoundFrameLayout;
import com.miaxis.postal.viewModel.FaceVerifyViewModel;

public class FaceVerifyFragment extends BaseViewModelFragment<FragmentFaceVerifyBinding, FaceVerifyViewModel> {

    private IDCardRecord idCardRecord;

    private RoundBorderView roundBorderView;
    private RoundFrameLayout roundFrameLayout;

    private Handler handler;
    private int delay = 21;

    private boolean pass = false;

    public static FaceVerifyFragment newInstance(IDCardRecord idCardRecord) {
        FaceVerifyFragment fragment = new FaceVerifyFragment();
        fragment.setIdCardRecord(idCardRecord);
        return fragment;
    }

    public FaceVerifyFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_face_verify;
    }

    @Override
    protected FaceVerifyViewModel initViewModel() {
        return ViewModelProviders.of(this).get(FaceVerifyViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.idCardRecordLiveData.setValue(idCardRecord);
        viewModel.idCardRecordLiveData.observe(this, idCardRecordObserver);
        viewModel.verifyFlag.observe(this, verifyFlagObserver);
        binding.rtvCamera.getViewTreeObserver().addOnGlobalLayoutListener(globalListener);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.tvSwitch.setOnClickListener(new OnLimitClickHelper(view -> {
            mListener.replaceFragment(FingerVerifyFragment.newInstance(idCardRecord));
        }));
        handler.post(countDownRunnable);
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(HomeFragment.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(countDownRunnable);
        mListener.dismissWaitDialog();
        viewModel.stopFaceVerify();
        if (!pass) {
            TTSManager.getInstance().stop();
        }
        CameraManager.getInstance().closeBackCamera();
    }

    private Runnable countDownRunnable = new Runnable() {
        @Override
        public void run() {
            delay--;
            viewModel.countDown.set(delay + " S");
            if (delay <= 0) {
                onBackPressed();
            } else {
                handler.postDelayed(countDownRunnable, 1000);
            }
        }
    };

    private Observer<IDCardRecord> idCardRecordObserver = mIdCardRecord -> {
        TTSManager.getInstance().playVoiceMessageAdd("请核验人脸");
        viewModel.startFaceVerify(idCardRecord);
    };

    private Observer<IDCardRecord> verifyFlagObserver = mIdCardRecord -> {
        pass = true;
        binding.ivBack.setEnabled(false);
        binding.tvSwitch.setEnabled(false);
        handler.postDelayed(() -> {
            try {
                mListener.replaceFragment(ExpressFragment.newInstance(mIdCardRecord));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1000);
    };

    private ViewTreeObserver.OnGlobalLayoutListener globalListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            binding.rtvCamera.getViewTreeObserver().removeOnGlobalLayoutListener(globalListener);
            ViewGroup.LayoutParams layoutParams = binding.rtvCamera.getLayoutParams();
            layoutParams.width = binding.flCamera.getWidth();
            layoutParams.height = binding.flCamera.getHeight();
            binding.rtvCamera.setLayoutParams(layoutParams);
            binding.rtvCamera.turnRound();
            CameraManager.getInstance().resetRetryTime();
            CameraManager.getInstance().openBackCamera(binding.rtvCamera, cameraListener);
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

    public void setIdCardRecord(IDCardRecord idCardRecord) {
        this.idCardRecord = idCardRecord;
    }
}
