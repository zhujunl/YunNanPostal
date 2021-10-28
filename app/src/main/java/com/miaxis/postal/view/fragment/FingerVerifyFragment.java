package com.miaxis.postal.view.fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.Customer;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.databinding.FragmentFingerVerifyBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.TTSManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.FingerVerifyViewModel;
import com.miaxis.postal.viewModel.LoginViewModel;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class FingerVerifyFragment extends BaseViewModelFragment<FragmentFingerVerifyBinding, FingerVerifyViewModel> {

    private MaterialDialog retryDialog;

    private volatile IDCardRecord idCardRecord;

    private MaterialDialog manualDialog;
    private Customer mCustomer;
    private Handler handler;
    private int delay = 21;

    private boolean pass = false;

    private boolean isAgreementCustomer = false;

    public static FingerVerifyFragment newInstance(IDCardRecord idCardRecord, boolean isAgreementCustomer, Customer customer) {
        FingerVerifyFragment fragment = new FingerVerifyFragment();
        fragment.setIdCardRecord(idCardRecord);
        fragment.setAgreementCustomer(isAgreementCustomer);
        fragment.setCustomer(customer);
        return fragment;
    }

    public FingerVerifyFragment() {
        // Required empty public constructor
    }

    public void setCustomer(Customer customer) {
        mCustomer = customer;
    }

    private Handler deviceHandler = new Handler();
    private Runnable task =new Runnable() {
        public void run() {
            // TODOAuto-generated method stub
            deviceHandler.postDelayed(this,10*1000);//设置延迟时间
            //需要执行的代码
            //获取设备状态,判断设备状态是启用还是禁用
            LoginViewModel loginViewModel = new LoginViewModel();
            Config config = ConfigManager.getInstance().getConfig();
            loginViewModel.getDevices(config.getDeviceIMEI());
            loginViewModel.deviceslist.observe(getActivity(), new Observer<DevicesStatusEntity.DataDTO>() {
                @Override
                public void onChanged(DevicesStatusEntity.DataDTO dataDTO) {
                    //如果是启用状态不做任何操作
                    if (dataDTO.getStatus().equals(ValueUtil.DEVICE_ENABLE)){

                    }else {
                        //如果从启用状态切换到了禁用状态强制退出登录跳到登录页面
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.finger_verify, new LoginFragment(), null)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });

        }
    };

    @Override
    protected int setContentView() {
        return R.layout.fragment_finger_verify;
    }

    @Override
    protected FingerVerifyViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(FingerVerifyViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.idCardRecordLiveData.setValue(idCardRecord);
        viewModel.initFingerResult.observe(this, fingerInitObserver);
        viewModel.fingerResultFlag.observe(this, fingerResultFlagObserver);
        viewModel.saveFlag.observe(this, saveFlagObserver);
        handler = new Handler(Looper.getMainLooper());
        //进入延时状态,一小时访问一次接口
        deviceHandler.postDelayed(task,3600000);//延迟调用
    }

    private MaterialDialog manualDialog2;

    @Override
    protected void initView() {
        initDialog();
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.tvSwitch.setOnClickListener(new OnLimitClickHelper(view -> {
            mListener.replaceFragment(FaceVerifyFragment.newInstance(idCardRecord));
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
    public void onStart() {
        super.onStart();
        if (retryDialog != null && retryDialog.isShowing()) {
            retryDialog.dismiss();
        }
        viewModel.initFingerDevice();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.releaseFingerDevice();
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
        if (!pass) {
            TTSManager.getInstance().stop();
        }
        manualDialog.dismiss();
        viewModel.releaseFingerDevice();
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

    private Observer<Status> fingerInitObserver = status -> {
        switch (status) {
            case FAILED:
                mListener.dismissWaitDialog();
                retryDialog = new MaterialDialog.Builder(getContext())
                        .title("初始化指纹模块失败，是否重试？")
                        .positiveText("重试")
                        .onPositive((dialog, which) -> {
                            viewModel.initFingerDevice();
                            dialog.dismiss();
                        })
                        .negativeText("退出")
                        .onNegative((dialog, which) -> {
                            dialog.dismiss();
                            mListener.backToStack(HomeFragment.class);
                        })
                        .autoDismiss(false)
                        .show();
                break;
            case LOADING:
                mListener.showWaitDialog("正在初始化指纹模块");
                break;
            case SUCCESS:
                mListener.dismissWaitDialog();
                TTSManager.getInstance().playVoiceMessageAdd("请按" + idCardRecord.getFingerprintPosition0() + "或" + idCardRecord.getFingerprintPosition1());
                viewModel.verifyFinger();
                break;
        }
    };

    private boolean needFingerResult = true;
    private Observer<Boolean> fingerResultFlagObserver = flag -> {
        Log.e("fingerResult", "1");
        if (!needFingerResult) {
            Log.e("fingerResult", "2");
            return;
        }
        Log.e("fingerResult", "3");
        TTSManager.getInstance().playVoiceMessageFlush((pass = flag) ? "核验通过" : "核验失败");
        handler.removeCallbacks(countDownRunnable);
        idCardRecord.setVerifyType("2");
        idCardRecord.setManualType("0");
        idCardRecord.setVerifyTime(new Date());
        needFingerResult = false;
        if (pass) {
            binding.ivBack.setEnabled(false);
            binding.tvSwitch.setEnabled(false);
            binding.tvManual.setEnabled(false);
            binding.fabAlarm.setEnabled(false);
            handler.postDelayed(() -> {
                try {
                    idCardRecord.setChekStatus(1);
                    if (isAgreementCustomer) {
                        mListener.replaceFragment(AgreementCustomersFragment.newInstance(idCardRecord,mCustomer));
                    } else {
                        mListener.replaceFragment(ExpressFragment.newInstance(idCardRecord));
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
                    needFingerResult = false;
                    handler.post(() -> {
                        try {
                            idCardRecord.setChekStatus(2);
                            if (isAgreementCustomer) {
                                mListener.replaceFragment(AgreementCustomersFragment.newInstance(idCardRecord,mCustomer));
                            } else {
                                mListener.replaceFragment(ExpressFragment.newInstance(idCardRecord));
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
                needFingerResult = true;
            }).build().show();
        }
    };

    private Observer<Boolean> saveFlagObserver = flag -> mListener.backToStack(HomeFragment.class);

    private View.OnLongClickListener alarmListener = v -> {
        //viewModel.alarm();
        return false;
    };

    private void initDialog() {
        manualDialog = new MaterialDialog.Builder(getContext())
                .title("人工干预")
                .content("是否进行人工干预？")
                .positiveText("确认")
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    idCardRecord.setChekStatus(0);
                    mListener.replaceFragment(ManualFragment.newInstance(idCardRecord));
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
                .content("指纹核验不通过，重新指纹核验或直接进入下一步。")
                .positiveText("下一步")
                .negativeText("重新核验")
                .autoDismiss(false)
                .cancelable(false)
                .build();
    }

    public void setIdCardRecord(IDCardRecord idCardRecord) {
        this.idCardRecord = idCardRecord;
    }

    public void setAgreementCustomer(boolean isAgreementCustomer) {
        this.isAgreementCustomer = isAgreementCustomer;
    }
}
