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
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.event.ExpressEditEvent;
import com.miaxis.postal.databinding.FragmentAgreementCustomersBinding;
import com.miaxis.postal.manager.ScanManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.EmojiExcludeFilter;
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


    private IDCardRecord idCardRecord;
    private List<Express> expressList;

    private ExpressAdapter expressAdapter;
    private MaterialDialog scanDialog;

    private Handler handler;
    private int delay = 5;

    private boolean draft = false;

    public static AgreementCustomersFragment newInstance(IDCardRecord idCardRecord) {
        Log.e("ExpressFragment", "newInstance:" + idCardRecord);
        AgreementCustomersFragment fragment = new AgreementCustomersFragment();
        fragment.setIdCardRecord(idCardRecord);
        fragment.setDraft(false);
        fragment.setExpressList(new ArrayList<>());
        return fragment;
    }

    public static AgreementCustomersFragment newInstanceForDraft(IDCardRecord idCardRecord, @NonNull List<Express> expressList) {
        Log.e("ExpressFragment", "newInstanceForDraft:" + idCardRecord);
        AgreementCustomersFragment fragment = new AgreementCustomersFragment();
        fragment.setIdCardRecord(idCardRecord);
        fragment.setDraft(true);
        fragment.setExpressList(expressList);
        return fragment;
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
        viewModel.idCardRecord.set(idCardRecord);
    }


    @Override
    protected void initView() {
        initDialog();
        initRecycleView();
        initReceiver();
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.ivAddress.setOnClickListener(new OnLimitClickHelper(view -> viewModel.getLocation()));
        if (expressList != null && !expressList.isEmpty()) {
            viewModel.initExpressList(expressList);
            expressAdapter.notifyDataSetChanged();
        }
        if (!TextUtils.isEmpty(idCardRecord.getSenderAddress())) {
            viewModel.address.set(idCardRecord.getSenderAddress());
        } else {
            viewModel.getLocation();
        }
        binding.ivDelete.setVisibility(draft ? View.VISIBLE : View.INVISIBLE);
        binding.ivDelete.setOnClickListener(deleteListener);
        binding.btnSubmit.setOnClickListener(submitClickListener);
        binding.btnDraft.setOnClickListener(draftClickListener);
        binding.fabAlarm.setOnLongClickListener(alarmListener);
        viewModel.expressList.observe(this, expressListObserver);
        viewModel.newExpress.observe(this, newExpressObserver);
        viewModel.repeatExpress.observe(this, repeatExpressObserver);
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
            mListener.replaceFragment(InspectFragment.newInstance(viewModel.newExpress.getValue(),
                    viewModel.clientName.get(),
                    viewModel.clientPhone.get()));
        });
        viewModel.showPicture.observe(this, s -> Glide.with(AgreementCustomersFragment.this).load(s).into(binding.imgInspectionImage));
        binding.editClientSName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        binding.editItemName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        binding.etAddress.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onExpressEditEvent(ExpressEditEvent event) {
        if (event.getMode() == ExpressEditEvent.MODE_MODIFY) {
            viewModel.modifyExpress(event.getExpress());
            if (event.getExpress() != null && event.getExpress().getPhotoList() != null && !event.getExpress().getPhotoList().isEmpty()) {
                viewModel.showPicture.postValue(event.getExpress().getPhotoList().get(0));
            }
        } else if (event.getMode() == ExpressEditEvent.MODE_DELETE) {
            viewModel.deleteExpress(event.getExpress());
        } else if (event.getMode() == ExpressEditEvent.MODE_ALARM) {
            viewModel.modifyExpress(event.getExpress());
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

    private static final String TAG = "Mx-ExpressFragment";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
        ScanManager.getInstance().powerOn();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
        ScanManager.getInstance().powerOff();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
        viewModel.expressList.removeObserver(expressListObserver);
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
        expressAdapter.setHeaderListener(headerListener);
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ScanCodeReceiver.RECE_DATA_ACTION);
        getActivity().registerReceiver(receiver, intentFilter);
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

    private ExpressAdapter.OnHeaderClickListener headerListener = () -> {
        //如果不为空并且没有正在扫描
        //viewModel.startScan();
    };


    private Observer<List<Express>> expressListObserver = expressList -> {
        expressAdapter.setDataList(expressList);
        expressAdapter.notifyDataSetChanged();
    };

    private Observer<Boolean> scanFlagObserver = flag -> {
        handler.removeCallbacks(scanDialogCountDownRunnable);
        if (flag) {
            delay = 5;
            handler.post(scanDialogCountDownRunnable);
            scanDialog.show();
        } else {
            scanDialog.dismiss();
        }
    };

    private Observer<Express> newExpressObserver = express -> {

    };

    private Observer<String> repeatExpressObserver = code -> {
        if (TextUtils.isEmpty(code)) {
            ToastManager.toast("该条码编号已重复", ToastManager.INFO);
        } else {
            Express expressByCode = viewModel.getExpressByCode(code);
            if (expressByCode == null) {
                ToastManager.toast("该条码编号已重复", ToastManager.INFO);
            }
        }
    };


    private Observer<Boolean> saveFlagObserver = flag -> mListener.backToStack(HomeFragment.class);

    private Observer<Boolean> deleteFlagObserver = flag -> mListener.backToStack(null);

    private View.OnClickListener submitClickListener = new OnLimitClickHelper(view -> {
        if (!setChecked()) {
            return;
        }
        if (!viewModel.isAllComplete()) {
            ToastManager.toast("有订单处于未完成状态", ToastManager.INFO);
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
        viewModel.alarm();
        return false;
    };

    public void setIdCardRecord(IDCardRecord idCardRecord) {
        this.idCardRecord = idCardRecord;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public void setExpressList(List<Express> expressList) {
        this.expressList = expressList;
    }

    private boolean setChecked() {
        if (TextUtils.isEmpty(viewModel.rqCode.get())) {
            String randomBarCode = App.getInstance().getRandomBarCode();
            //viewModel.rqCode.set(randomBarCode);
            viewModel.makeNewExpress(randomBarCode);
        }
        if (viewModel.clientName == null || TextUtils.isEmpty(viewModel.clientName.get()) || TextUtils.isEmpty(viewModel.clientName.get().trim())) {
            ToastManager.toast("请输入客户名称", ToastManager.INFO);
            return false;
        }
        if (viewModel.clientPhone == null || TextUtils.isEmpty(viewModel.clientPhone.get()) || TextUtils.isEmpty(viewModel.clientPhone.get().trim())) {
            ToastManager.toast("请输入客户手机号", ToastManager.INFO);
            return false;
        }
        if (viewModel.itemName == null || TextUtils.isEmpty(viewModel.itemName.get()) || TextUtils.isEmpty(viewModel.itemName.get().trim())) {
            ToastManager.toast("请输入货物名称", ToastManager.INFO);
            return false;
        }
        if (viewModel.theQuantityOfGoods == null || TextUtils.isEmpty(viewModel.theQuantityOfGoods.get()) || TextUtils.isEmpty(viewModel.theQuantityOfGoods.get().trim())) {
            ToastManager.toast("请输入货物数量", ToastManager.INFO);
            return false;
        }
        return true;
    }
}