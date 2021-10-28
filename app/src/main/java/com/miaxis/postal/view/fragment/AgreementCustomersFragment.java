package com.miaxis.postal.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.miaxis.postal.R;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Customer;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.event.ExpressEditEvent;
import com.miaxis.postal.databinding.FragmentAgreementCustomersBinding;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.EmojiExcludeFilter;
import com.miaxis.postal.view.adapter.CEditViewAdapter;
import com.miaxis.postal.view.adapter.ExpressAdapter;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.component.ScanCodeReceiver;
import com.miaxis.postal.viewModel.AgreementCustomersModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class AgreementCustomersFragment extends BaseViewModelFragment<FragmentAgreementCustomersBinding, AgreementCustomersModel> {

    private static final String TAG = "AgreementCustomers";

    private IDCardRecord idCardRecord;
    private ExpressAdapter expressAdapter;
    private MaterialDialog scanDialog;
    private Express express = new Express();
    private Handler handler;
    private int delay = 3;
    private CEditViewAdapter cEditViewAdapter;
    private boolean draft = false;
    private Customer customer;

    public static AgreementCustomersFragment newInstance(IDCardRecord idCardRecord, Customer customer) {
        AgreementCustomersFragment fragment = new AgreementCustomersFragment();
        fragment.setIdCardRecord(idCardRecord);
        fragment.setDraft(false);
        fragment.setCustomer(customer);
        return fragment;
    }

    public static AgreementCustomersFragment newInstanceForDraft(IDCardRecord idCardRecord, @NonNull Express express) {
        AgreementCustomersFragment fragment = new AgreementCustomersFragment();
        fragment.setIdCardRecord(idCardRecord);
        fragment.setDraft(true);
        fragment.setExpress(express);
        return fragment;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setExpress(Express express) {
        this.express = express;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_agreement_customers;
    }

    @Override
    protected AgreementCustomersModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(AgreementCustomersModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.idCardRecordLiveData.setValue(idCardRecord);
    }

    @Override
    protected void initView() {
        Log.e(TAG, "--------------------------------------------------------");
        initDialog();
        initRecycleView();
        initReceiver();
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.ivAddress.setOnClickListener(new OnLimitClickHelper(view -> viewModel.getLocation()));

        viewModel.initExpressAndCustomer(this.express, this.customer);
        //        if (TextUtils.isEmpty(this.express.getBarCode())) {
        //            viewModel.startScan();
        //        }
        if (!TextUtils.isEmpty(idCardRecord.getSenderAddress())) {
            viewModel.address.setValue(idCardRecord.getSenderAddress());
        } else {
            viewModel.getLocation();
        }
        binding.ivDelete.setVisibility(draft ? View.VISIBLE : View.INVISIBLE);
        binding.ivDelete.setOnClickListener(deleteListener);
        binding.btnSubmit.setOnClickListener(submitClickListener);
        binding.btnDraft.setOnClickListener(draftClickListener);
        binding.fabAlarm.setOnLongClickListener(alarmListener);
        viewModel.rqCode.observe(this, s -> {
            if (TextUtils.isEmpty(s)) {
                binding.tvBarCode.setText("");
            } else {
                if (s.startsWith(App.getInstance().BarHeader)) {
                    binding.tvBarCode.setText("");
                } else {
                    binding.tvBarCode.setText(s);
                }
            }
        });
        cEditViewAdapter = new CEditViewAdapter(binding.llClientSName);
        cEditViewAdapter.bind((popupWindow, customer) -> {
            if (customer != null) {
                viewModel.clientName.setValue(customer.name);
                viewModel.clientPhone.setValue(customer.phone);
            }
        });
        viewModel.Customers.observe(this, cEditViewAdapter::bind);

        viewModel.expressLiveData.observe(this, expressObserver);
        viewModel.repeat.observe(this, repeatObserver);
        viewModel.scanFlag.observe(this, scanFlagObserver);
        viewModel.saveFlag.observe(this, saveFlagObserver);
        viewModel.deleteFlag.observe(this, deleteFlagObserver);
        binding.btnQrCode.setOnClickListener(v -> {
            //如果不为空并且没有正在扫描
            viewModel.startScan();
        });
        handler = new Handler(Looper.getMainLooper());
        EventBus.getDefault().register(this);

        binding.btnInspection.setOnClickListener(v -> {
            if (!setChecked()) {
                return;
            }
            if (TextUtils.isEmpty(viewModel.rqCode.getValue())) {
                String randomBarCode = App.getInstance().getRandomBarCode();
                viewModel.rqCode.setValue(randomBarCode);
            }
            Express value = viewModel.expressLiveData.getValue();
            Log.e(TAG, "value:" + value);
            mListener.replaceFragment(InspectFragment.newInstance(
                    value,
                    viewModel.clientName.getValue(),
                    viewModel.clientPhone.getValue(),
                    viewModel.itemName.getValue(),
                    viewModel.theQuantityOfGoods.getValue(),
                    viewModel.address.getValue()
                    )
            );
        });
        viewModel.showPicture.observe(this, path -> {
            if (TextUtils.isEmpty(path)) {
                Glide.with(AgreementCustomersFragment.this).clear(binding.imgInspectionImage);
            } else {
                Glide.with(AgreementCustomersFragment.this).load(path).centerCrop().into(binding.imgInspectionImage);
            }
            Express value = viewModel.expressLiveData.getValue();
            if (value != null) {
                List<String> objects = new ArrayList<>();
                objects.add(path);
                value.setPhotoPathList(objects);
            }
        });

        binding.editClientSName.setFilters(new InputFilter[]{new EmojiExcludeFilter(), new InputFilter.LengthFilter(15)});
        binding.editClientSPhone.setFilters(new InputFilter[]{new EmojiExcludeFilter(), new InputFilter.LengthFilter(15)});
        binding.editItemName.setFilters(new InputFilter[]{new EmojiExcludeFilter(), new InputFilter.LengthFilter(15)});
        binding.etAddress.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onExpressEditEvent(ExpressEditEvent event) {
        Log.e(TAG, "ExpressEditEvent:   event:" + event);
        if (event.getMode() == ExpressEditEvent.MODE_MODIFY) {
            //viewModel.modifyExpress(event.getExpress());
            if (event.getExpress() != null && event.getExpress().getPhotoPathList() != null && !event.getExpress().getPhotoPathList().isEmpty()) {
                viewModel.showPicture.postValue(event.getExpress().getPhotoPathList().get(0));
                viewModel.clientName.setValue(event.getName());
                viewModel.clientPhone.setValue(event.getPhone());
                viewModel.itemName.setValue(event.getGoodName());
                viewModel.theQuantityOfGoods.setValue(event.getGoodCounts());
                viewModel.address.setValue(event.getSendAddress());
            }
        } else if (event.getMode() == ExpressEditEvent.MODE_ALARM) {
            //viewModel.modifyExpress(event.getExpress());
            binding.fabAlarm.performLongClick();
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(getContext())
                .title("确认离开页面？")
                .positiveText("确认")
                .onPositive((dialog, which) -> {
                    mListener.backToStack(draft ? null : HomeFragment.class);
                })
                .negativeText("取消")
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cEditViewAdapter != null) {
            cEditViewAdapter.clear();
        }
        getContext().unregisterReceiver(receiver);
        viewModel.expressLiveData.removeObserver(expressObserver);
        EventBus.getDefault().unregister(this);
    }

    private void initDialog() {
        scanDialog = new MaterialDialog.Builder(getContext())
                .title("新建订单")
                .progress(true, 100)
                .content("请将扫描口对准条码进行扫描")
                .build();
    }

    private void initRecycleView() {
        expressAdapter = new ExpressAdapter(getContext());
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ScanCodeReceiver.RECE_DATA_ACTION);
        getContext().registerReceiver(receiver, intentFilter);
    }

    private Runnable scanDialogCountDownRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (scanDialog.isShowing()) {
                    String text = "请将扫描口对准条码进行扫描(" + delay + "S)";
                    scanDialog.getContentView().setText(text);
                    delay--;
                    if (delay < 0) {
                        viewModel.stopScan();
                    } else {
                        handler.postDelayed(scanDialogCountDownRunnable, 1000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), AgreementCustomersModel.RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                viewModel.handlerScanCode(data);
            }
        }
    };

    private Observer<Express> expressObserver = express -> {
        List<Express> objects = new ArrayList<>();
        objects.add(express);
        expressAdapter.setDataList(objects);
    };

    private Observer<Boolean> scanFlagObserver = flag -> {
        handler.removeCallbacks(scanDialogCountDownRunnable);
        if (flag) {
            delay = 3;
            handler.post(scanDialogCountDownRunnable);
            scanDialog.show();
        } else {
            scanDialog.dismiss();
        }
    };

    private Observer<Boolean> repeatObserver = aBoolean -> ToastManager.toast("该条码编号已重复", ToastManager.ERROR);

    private Observer<Boolean> saveFlagObserver = flag -> mListener.backToStack(HomeFragment.class);

    private Observer<Boolean> deleteFlagObserver = flag -> mListener.backToStack(null);

    private View.OnClickListener submitClickListener = new OnLimitClickHelper(view -> {
        if (!setChecked()) {
            return;
        }
        //        if (!viewModel.isAllComplete()) {
        //            ToastManager.toast("有订单处于未完成状态", ToastManager.INFO);
        //            return;
        //        }
        if (viewModel.address == null || TextUtils.isEmpty(viewModel.address.getValue()) || TextUtils.isEmpty(viewModel.address.getValue().trim())) {
            ToastManager.toast("请输入寄件地址", ToastManager.INFO);
            return;
        }
        if (viewModel.expressLiveData == null || viewModel.expressLiveData.getValue() == null ||
                viewModel.expressLiveData.getValue().getPhotoPathList() == null ||
                viewModel.expressLiveData.getValue().getPhotoPathList().isEmpty()) {
            ToastManager.toast("请先开箱验视", ToastManager.ERROR);
            return;
        }
        new MaterialDialog.Builder(getContext())
                .title("确认上传？")
                .positiveText("确认")
                .onPositive((dialog, which) -> viewModel.saveComplete())
                .negativeText("取消")
                .show();
    });

    private View.OnClickListener draftClickListener = new OnLimitClickHelper(view -> {
        new MaterialDialog.Builder(getContext())
                .title("确认存入草稿箱？")
                .positiveText("确认")
                .onPositive((dialog, which) -> viewModel.saveDraft())
                .negativeText("取消")
                .show();
    });

    private View.OnClickListener deleteListener = new OnLimitClickHelper(view -> {
        new MaterialDialog.Builder(getContext())
                .title("确认删除？")
                .positiveText("确认")
                .onPositive((dialog, which) -> viewModel.deleteSelf())
                .negativeText("取消")
                .show();
    });

    private View.OnLongClickListener alarmListener = v -> {
        //viewModel.alarm();
        return false;
    };

    public void setIdCardRecord(IDCardRecord idCardRecord) {
        this.idCardRecord = idCardRecord;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    private boolean setChecked() {
        if (viewModel.clientName == null || TextUtils.isEmpty(viewModel.clientName.getValue()) || TextUtils.isEmpty(viewModel.clientName.getValue().trim())) {
            ToastManager.toast("请输入客户名称", ToastManager.INFO);
            return false;
        }
        if (viewModel.clientPhone == null || TextUtils.isEmpty(viewModel.clientPhone.getValue()) || TextUtils.isEmpty(viewModel.clientPhone.getValue().trim())) {
            ToastManager.toast("请输入客户手机号", ToastManager.INFO);
            return false;
        }
        if (viewModel.itemName == null || TextUtils.isEmpty(viewModel.itemName.getValue()) || TextUtils.isEmpty(viewModel.itemName.getValue().trim())) {
            ToastManager.toast("请输入货物名称", ToastManager.INFO);
            return false;
        }
        if (viewModel.theQuantityOfGoods == null || TextUtils.isEmpty(viewModel.theQuantityOfGoods.getValue()) || TextUtils.isEmpty(viewModel.theQuantityOfGoods.getValue().trim())) {
            ToastManager.toast("请输入货物数量", ToastManager.INFO);
            return false;
        }
        return true;
    }
}