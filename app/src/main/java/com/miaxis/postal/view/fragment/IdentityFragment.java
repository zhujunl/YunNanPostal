package com.miaxis.postal.view.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.bridge.GlideApp;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.event.DrawRectEvent;
import com.miaxis.postal.data.event.OpenCameraEvent;
import com.miaxis.postal.databinding.FragmentIdentityBinding;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.FaceManager;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.IdentityViewModel;
import com.speedata.libid2.IDInfor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class IdentityFragment extends BaseViewModelFragment<FragmentIdentityBinding, IdentityViewModel> {

    public static IdentityFragment newInstance() {
        return new IdentityFragment();
    }

    public IdentityFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_identity;
    }

    @Override
    protected IdentityViewModel initViewModel() {
        return ViewModelProviders.of(this).get(IdentityViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        viewModel.initCardResult.observe(this, initCardResultObserver);
        viewModel.idInfoLiveData.observe(this, idInfoObserver);
        viewModel.verifyResult.observe(this, verifyResultObserver);
        viewModel.verifyFace.observe(this, verifyFaceObserver);
    }

    @Override
    protected void initView() {
        binding.ivHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.replaceFragment(InspectFragment.newInstance(null));
//                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                Uri imageUriCache = CameraManager.getOutputMediaFileUri(getContext());
//                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriCache);
//                //Android7.0添加临时权限标记，此步千万别忘了
//                openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                getActivity().startActivityForResult(openCameraIntent, 11);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        viewModel.initCardResult.removeObserver(initCardResultObserver);
        viewModel.idInfoLiveData.removeObserver(idInfoObserver);
        viewModel.verifyResult.removeObserver(verifyResultObserver);
        viewModel.verifyFace.removeObserver(verifyFaceObserver);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenCameraEvent(OpenCameraEvent event) {
        int rootHeight = binding.clCamera.getHeight();
        int rootWidth = rootHeight * event.getPreviewHeight() / event.getPreviewWidth();
        binding.rsvRect.setRootSize(rootWidth, rootHeight);
        binding.rsvRect.setZoomRate((float) rootWidth / FaceManager.ZOOM_WIDTH);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDrawRectEvent(DrawRectEvent event) {
        if (event.getFaceNum() != -1) {
            binding.rsvRect.drawRect(event.getFaceInfos(), event.getFaceNum());
        }
    }

    private Observer<Status> initCardResultObserver = status -> {
        switch (status) {
            case FAILED:
                new MaterialDialog.Builder(getContext())
                        .title("初始化身份证阅读器失败，是否重试？")
                        .positiveText("重试")
                        .onPositive((dialog, which) -> {
                            viewModel.startReadCard();
                            dialog.dismiss();
                        })
                        .negativeText("退出")
                        .onNegative((dialog, which) -> mListener.exitApp())
                        .autoDismiss(false)
                        .show();
            case LOADING:
                mListener.showWaitDialog("正在初始化身份证阅读器");
                break;
            case SUCCESS:
                mListener.dismissWaitDialog();
                break;
        }
    };

    private Observer<IDInfor> idInfoObserver = idInfo -> {
        GlideApp.with(this).load(idInfo.getBmps()).into(binding.ivHeader);
        if (viewModel.verifyResult.getValue() == Status.LOADING) {
            binding.clCamera.setVisibility(View.VISIBLE);
            new Thread(() -> CameraManager.getInstance().openCamera(binding.csvCamera.getHolder())).start();
        }
    };

    private Observer<Status> verifyResultObserver = result -> {
        switch (result) {
            case SUCCESS:
                GlideApp.with(this).load(R.drawable.icon_success).into(binding.ivResult);
                break;
            case LOADING:
                GlideApp.with(this).load(R.drawable.icon_loading).into(binding.ivResult);
                break;
            case FAILED:
                GlideApp.with(this).load(R.drawable.icon_failed).into(binding.ivResult);
                break;
        }
    };

    private Observer<Bitmap> verifyFaceObserver = bitmap -> {
        GlideApp.with(this).load(bitmap).into(binding.ivFaceHeader);
        binding.rsvRect.clearDraw();
        binding.clCamera.setVisibility(View.GONE);
        binding.ivFaceHeader.setVisibility(View.VISIBLE);
        CameraManager.getInstance().closeCamera();
    };

}
