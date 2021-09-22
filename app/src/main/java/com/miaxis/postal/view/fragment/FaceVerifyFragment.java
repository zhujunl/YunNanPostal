package com.miaxis.postal.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Customer;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.databinding.FragmentFaceVerifyBinding;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.TTSManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.custom.RoundBorderView;
import com.miaxis.postal.view.custom.RoundFrameLayout;
import com.miaxis.postal.viewModel.FaceVerifyViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class FaceVerifyFragment extends BaseViewModelFragment<FragmentFaceVerifyBinding, FaceVerifyViewModel> {

    private volatile IDCardRecord idCardRecord;

    private RoundBorderView roundBorderView;
    private RoundFrameLayout roundFrameLayout;

    private MaterialDialog manualDialog;
    private Customer customer;

    private Handler handler;
    private int delay = 21;

    private boolean pass = false;

    private boolean isAgreementCustomer = false;

    public static FaceVerifyFragment newInstance(IDCardRecord idCardRecord) {
        FaceVerifyFragment fragment = new FaceVerifyFragment();
        fragment.setIdCardRecord(idCardRecord);
        return fragment;
    }


    public static FaceVerifyFragment newInstance(IDCardRecord idCardRecord, boolean isAgreementCustomer, Customer customer) {
        FaceVerifyFragment fragment = new FaceVerifyFragment();
        fragment.setIdCardRecord(idCardRecord);
        fragment.setCustomer(customer);
        Bundle bundle = new Bundle();
        bundle.putBoolean("agreementCustomer", isAgreementCustomer);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isAgreementCustomer = getArguments().getBoolean("agreementCustomer", false);
        }
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
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(FaceVerifyViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.readCardNum = idCardRecord.getCardNumber();
        viewModel.idCardRecordLiveData.setValue(idCardRecord);
        if (viewModel.idCardRecordLiveData.getValue() != null ) {
            viewModel.cardFingerprint.set(viewModel.idCardRecordLiveData.getValue().getFingerprint0());
        }
        viewModel.idCardRecordLiveData.observe(this, idCardRecordObserver);
        viewModel.verifyFlag.observe(this, verifyFlagObserver);
        viewModel.verifyFailedFlag.observe(this, verifyFailedObserver);
        viewModel.saveFlag.observe(this, saveFlagObserver);
        binding.rtvCamera.getViewTreeObserver().addOnGlobalLayoutListener(globalListener);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void initView() {
        initDialog();
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.tvSwitch.setOnClickListener(new OnLimitClickHelper(view -> {
            mListener.replaceFragment(FingerVerifyFragment.newInstance(idCardRecord,isAgreementCustomer,customer));
        }));
        binding.tvManual.setOnClickListener(new OnLimitClickHelper(view -> {
            if (!manualDialog.isShowing()) {
                manualDialog.show();
            }
        }));
        binding.fabAlarm.setOnLongClickListener(alarmListener);
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
        manualDialog.dismiss();
        manualDialog2.dismiss();
        CameraManager.getInstance().closeBackCamera();
    }

    private Runnable countDownRunnable = new Runnable() {
        @Override
        public void run() {
            delay--;
            viewModel.countDown.set(delay + " S");
            if (delay <= 0) {
                if (!manualDialog.isShowing()) {
                    manualDialog.show();
                }
            } else {
                handler.postDelayed(countDownRunnable, 1000);
            }
        }
    };

    private Observer<IDCardRecord> idCardRecordObserver = mIdCardRecord -> {
        TTSManager.getInstance().playVoiceMessageAdd("请核验人脸");
        viewModel.startFaceVerify(idCardRecord);
    };

    private Observer<Boolean> saveFlagObserver = flag -> mListener.backToStack(HomeFragment.class);

    private Observer<Boolean> verifyFailedObserver = flag -> {
        binding.tvManual.setVisibility(View.VISIBLE);
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

    private View.OnLongClickListener alarmListener = v -> {
        //viewModel.alarm();
        return false;
    };

    private MaterialDialog manualDialog2;

    private void initDialog() {
        manualDialog = new MaterialDialog.Builder(getContext())
                .title("人工干预")
                .content("是否进行人工干预？")
                .positiveText("确认")
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    mListener.replaceFragment(ManualFragment.newInstance(idCardRecord, isAgreementCustomer,customer));
                })
                .negativeText("放弃")
                .onNegative((dialog, which) -> {
                    dialog.dismiss();
                    onBackPressed();
                })
                .autoDismiss(false)
                .cancelable(false)
                .build();


        manualDialog2 = new MaterialDialog.Builder(getContext())
                .title("错误")
                .content("人证核验不通过，重新人证核验或直接进入下一步。")
                .positiveText("下一步")
                .negativeText("重新核验")
                .autoDismiss(false)
                .cancelable(false)
                .build();
    }

    private Observer<IDCardRecord> verifyFlagObserver = mIDCardRecord -> {
        if (mIDCardRecord != null) {
            if (pass = mIDCardRecord.getChekStatus() == 1) {
                binding.ivBack.setEnabled(false);
                binding.tvSwitch.setEnabled(false);
                binding.tvManual.setEnabled(false);
                binding.fabAlarm.setEnabled(false);
            }
            handler.removeCallbacks(countDownRunnable);
            if (pass) {
                mIDCardRecord.setVerifyType("1");
                mIDCardRecord.setManualType("0");
                handler.postDelayed(() -> {
                    try {
                        if (isAgreementCustomer) {
                            mListener.replaceFragment(AgreementCustomersFragment.newInstance(mIDCardRecord,customer));
                        } else {
                            mListener.replaceFragment(ExpressFragment.newInstance(mIDCardRecord));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 1000);
            } else {
                MaterialDialog.Builder builder = manualDialog2.getBuilder();
                builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        mIDCardRecord.setVerifyType("1");
                        mIDCardRecord.setManualType("0");
                        handler.post(() -> {
                            try {
                                if (isAgreementCustomer) {
                                    mListener.replaceFragment(AgreementCustomersFragment.newInstance(mIDCardRecord,customer));
                                } else {
                                    mListener.replaceFragment(ExpressFragment.newInstance(mIDCardRecord));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }).onNegative((dialog, which) -> {
                    dialog.dismiss();
                    delay = 21;
                    handler.post(countDownRunnable);
                    viewModel.startFaceVerify(idCardRecord);
                }).build().show();
            }
        } else {
            ToastManager.toast("遇到错误，请退出重试", ToastManager.ERROR);
        }
    };

    public void setIdCardRecord(IDCardRecord idCardRecord) {
        this.idCardRecord = idCardRecord;
    }

}
