package com.miaxis.postal.view.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.event.ExpressEditEvent;
import com.miaxis.postal.databinding.FragmentExpressBinding;
import com.miaxis.postal.manager.ScanManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.EmojiExcludeFilter;
import com.miaxis.postal.view.adapter.ExpressAdapter;
import com.miaxis.postal.view.adapter.SpacesItemDecoration;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.ExpressViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class ExpressFragment extends BaseViewModelFragment<FragmentExpressBinding, ExpressViewModel> {

    private IDCardRecord idCardRecord;
    private List<Express> expressList;

    private ExpressAdapter expressAdapter;
    //    private MaterialDialog scanDialog;

    //    private Handler handler;
    //    private int delay = 5;

    private boolean draft = false;

    public static ExpressFragment newInstance(IDCardRecord idCardRecord) {
        Log.e("ExpressFragment", "newInstance:" + idCardRecord);
        ExpressFragment fragment = new ExpressFragment();
        fragment.setIdCardRecord(idCardRecord);
        fragment.setDraft(false);
        fragment.setExpressList(new ArrayList<>());
        return fragment;
    }

    public static ExpressFragment newInstanceForDraft(IDCardRecord idCardRecord, @NonNull List<Express> expressList) {
        Log.e("ExpressFragment", "newInstanceForDraft:" + idCardRecord);
        ExpressFragment fragment = new ExpressFragment();
        fragment.setIdCardRecord(idCardRecord);
        fragment.setDraft(true);
        fragment.setExpressList(expressList);
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
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(ExpressViewModel.class);
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
        //initDialog();
        initRecycleView();
        //initReceiver();
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
        //        viewModel.scanFlag.observe(this, scanFlagObserver);
        viewModel.saveFlag.observe(this, saveFlagObserver);
        viewModel.deleteFlag.observe(this, deleteFlagObserver);
        //handler = new Handler(Looper.getMainLooper());
        EventBus.getDefault().register(this);
        binding.etAddress.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onExpressEditEvent(ExpressEditEvent event) {
        if (event.getMode() == ExpressEditEvent.MODE_MODIFY) {
            viewModel.modifyExpress(event.getExpress());
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
        //ScanManager.getInstance().powerOn();
        ScanManager.getInstance().powerOff();
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
        //        getActivity().unregisterReceiver(receiver);
        viewModel.expressList.removeObserver(expressListObserver);
        EventBus.getDefault().unregister(this);
    }

    //    private void initDialog() {
    //        scanDialog = new MaterialDialog.Builder(getContext())
    //                .title("新建订单")
    //                .progress(true, 100)
    //                .content("请将扫描口对准条码进行扫描")
    //                .build();
    //    }

    private void initRecycleView() {
        expressAdapter = new ExpressAdapter(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.rvExpress.setLayoutManager(gridLayoutManager);
        binding.rvExpress.setAdapter(expressAdapter);
        binding.rvExpress.addItemDecoration(new SpacesItemDecoration(1));
        ((SimpleItemAnimator) binding.rvExpress.getItemAnimator()).setSupportsChangeAnimations(false);
        expressAdapter.setHeaderListener(headerListener);
        expressAdapter.setBodyListener(bodyListener);
    }

    //    private void initReceiver() {
    //        IntentFilter intentFilter = new IntentFilter();
    //        intentFilter.addAction(ScanCodeReceiver.RECE_DATA_ACTION);
    //        getActivity().registerReceiver(receiver, intentFilter);
    //    }

    //    private Runnable scanDialogCountDownRunnable = new Runnable() {
    //        @Override
    //        public void run() {
    //            try {
    //                if (scanDialog.isShowing()) {
    //                    String text = "请将扫描口对准条码进行扫描(" + delay + "S)";
    //                    scanDialog.getContentView().setText(text);
    //                    delay--;
    //                    if (delay < 0) {
    //                        viewModel.stopScan();
    //                    } else {
    //                        handler.postDelayed(scanDialogCountDownRunnable, 1000);
    //                    }
    //                }
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    };

    //    private BroadcastReceiver receiver = new BroadcastReceiver() {
    //        @Override
    //        public void onReceive(Context context, Intent intent) {
    //            if (TextUtils.equals(intent.getAction(), ExpressViewModel.RECE_DATA_ACTION)) {
    //                String data = intent.getStringExtra("se4500");
    //                viewModel.handlerScanCode(data);
    //            }
    //        }
    //    };

    private ExpressAdapter.OnHeaderClickListener headerListener = () -> {
        //如果不为空并且没有正在扫描
        //viewModel.startScan();
        viewModel.waitMessage.setValue("");
        String randomBarCode = App.getInstance().getRandomBarCode();
        viewModel.removeRepeatEdit(randomBarCode);
        viewModel.makeNewExpress(randomBarCode);
    };

    private ExpressAdapter.OnBodyClickListener bodyListener = (view, position) -> {
        mListener.replaceFragment(InspectFragment.newInstance(expressAdapter.getData(position - 1)));
    };

    private Observer<List<Express>> expressListObserver = expressList -> {
        expressAdapter.setDataList(expressList);
        expressAdapter.notifyDataSetChanged();
    };

    //    private Observer<Boolean> scanFlagObserver = flag -> {
    //        handler.removeCallbacks(scanDialogCountDownRunnable);
    //        if (flag) {
    //            delay = 5;
    //            handler.post(scanDialogCountDownRunnable);
    //            scanDialog.show();
    //        } else {
    //            scanDialog.dismiss();
    //        }
    //    };

    private Observer<Express> newExpressObserver = express -> {
        mListener.replaceFragment(InspectFragment.newInstance(express));
    };

    private Observer<String> repeatExpressObserver = code -> {
        if (TextUtils.isEmpty(code)) {
            ToastManager.toast("该条码编号已重复", ToastManager.INFO);
        } else {
            Express expressByCode = viewModel.getExpressByCode(code);
            if (expressByCode != null) {
                mListener.replaceFragment(InspectFragment.newInstance(expressByCode));
            } else {
                ToastManager.toast("该条码编号已重复", ToastManager.INFO);
            }
        }
    };

    private Observer<Boolean> saveFlagObserver = flag -> mListener.backToStack(HomeFragment.class);

    private Observer<Boolean> deleteFlagObserver = flag -> mListener.backToStack(null);

    private View.OnClickListener submitClickListener = new OnLimitClickHelper(view -> {
        //        if (!viewModel.checkInput()) {
        //            ToastManager.toast("请输入寄件人手机号码和寄件地址", ToastManager.INFO);
        //            return;
        //        }
        List<Express> expressList = viewModel.getExpressList();
        if (expressList == null || expressList.isEmpty()) {
            ToastManager.toast("请至少完成一个订单", ToastManager.ERROR);
            return;
        }
        for (Express express : expressList) {
            List<Bitmap> photoList = express.getPhotoList();
            boolean empty = photoList == null || photoList.isEmpty();
            if (empty) {
                ToastManager.toast("您有未拍照的订单", ToastManager.ERROR);
                return;
            }
        }
        if (!viewModel.isAllComplete()) {
            ToastManager.toast("有订单处于未完成状态", ToastManager.ERROR);
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

    public void setExpressList(List<Express> expressList) {
        this.expressList = expressList;
    }

}
