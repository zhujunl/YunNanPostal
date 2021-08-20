package com.miaxis.postal.view.fragment;

import android.text.TextUtils;
import android.util.Log;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Customer;
import com.miaxis.postal.data.entity.IDCard;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.Photograph;
import com.miaxis.postal.data.event.TakePhotoEvent;
import com.miaxis.postal.databinding.FragmentManualBinding;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.IDCardUtils;
import com.miaxis.postal.util.StringUtils;
import com.miaxis.postal.view.adapter.IDCardFilterAdapter;
import com.miaxis.postal.view.adapter.InterventionAdapter;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.InspectViewModel;
import com.miaxis.postal.viewModel.ManualViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class ManualFragment extends BaseViewModelFragment<FragmentManualBinding, ManualViewModel> {
    private final String TAG = "ManualFragment";
    private InterventionAdapter inspectAdapter;
    private IDCardFilterAdapter idCardFilterAdapter;
    private Customer customer;
    private IDCardRecord idCardRecord;

    //是否协议客户
    private boolean isAgreementCustomer = false;

    public static ManualFragment newInstance(IDCardRecord idCardRecord) {
        ManualFragment fragment = new ManualFragment();
        fragment.setIdCardRecord(idCardRecord);
        return fragment;
    }

    //    public static ManualFragment newInstance(IDCardRecord idCardRecord, boolean isAgreementCustomer) {
    //        ManualFragment fragment = new ManualFragment();
    //        fragment.setIdCardRecord(idCardRecord, isAgreementCustomer);
    //        return fragment;
    //    }

    public static ManualFragment newInstance(IDCardRecord idCardRecord, boolean isAgreementCustomer, Customer customer) {
        ManualFragment fragment = new ManualFragment();
        fragment.setIdCardRecord(idCardRecord, isAgreementCustomer);
        fragment.setCustomer(customer);
        return fragment;
    }

    public ManualFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_manual;
    }

    @Override
    protected ManualViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(ManualViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void initData() {
        if (idCardRecord != null) {
            viewModel.idCardRecord = idCardRecord;
            viewModel.name.set(idCardRecord.getName());
            binding.etName.setEnabled(false);
            viewModel.identityNumber.set(idCardRecord.getCardNumber());
            binding.etCardNumber.setEnabled(false);
        } else {
            binding.etCardNumber.requestFocus();
        }
        //binding.etCardNumber.setRawInputType(Configuration.KEYBOARD_QWERTY);
        viewModel.confirm.observe(this, confirmObserver);
        viewModel.idCardSearch.observe(this, idCardObserver);
        viewModel.alarmFlag.observe(this, alarmFlagObserver);
    }

    @Override
    protected void initView() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        initRecycleView();
        initAutoComplete();
        viewModel.photographList.observe(this, photographObserver);
        binding.btnConfirm.setOnClickListener(new OnLimitClickHelper(view -> {
            String cardValidate;
            if (TextUtils.isEmpty(binding.etName.getText().toString().replace((char) 12288, ' ').trim())) {
                ToastManager.toast("请输入寄件人姓名", ToastManager.ERROR);
            } else if (!StringUtils.isChineseWord(binding.etName.getText().toString().trim())) {
                ToastManager.toast("寄件人姓名只支持中文", ToastManager.ERROR);
            } else if (TextUtils.isEmpty(binding.etCardNumber.getText().toString().trim())) {
                ToastManager.toast("请输入寄件人证件号码", ToastManager.ERROR);
            } else if (!IDCardUtils.VALIDITY.equals(cardValidate = IDCardUtils.IDCardValidate(binding.etCardNumber.getText().toString().trim()))) {
                ToastManager.toast("" + cardValidate, ToastManager.ERROR);
            } else if (viewModel.getSelectSize() == 0) {
                ToastManager.toast("请拍摄并选择寄件人现场留档照片", ToastManager.ERROR);
            } else {
                viewModel.confirm();
            }
        }));
        binding.fabAlarm.setOnLongClickListener(v -> {
            viewModel.alarm();
            return false;
        });
        EventBus.getDefault().register(this);
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(HomeFragment.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.photographList.removeObserver(photographObserver);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onTakePhotoEvent(TakePhotoEvent event) {
        Log.e(TAG, "onTakePhotoEvent:" + event);
        if (event != null && event.getPhotoList() != null && !event.getPhotoList().isEmpty()) {
            viewModel.addPhotograph(event.getPhotoList().get(0));
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void initRecycleView() {
        inspectAdapter = new InterventionAdapter(getContext());
        inspectAdapter.setHeaderListener(headerListener);
        inspectAdapter.setBodyListener(bodyListener);
        inspectAdapter.setCheckBoxListener(checkBoxListener);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        binding.rvInspect.setLayoutManager(gridLayoutManager);
        binding.rvInspect.setAdapter(inspectAdapter);
        ((SimpleItemAnimator) binding.rvInspect.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private void initAutoComplete() {
        binding.etCardNumber.setOnClickListener(v -> {
            if (binding.etCardNumber.getText().toString().length() >= 2) {
                binding.etCardNumber.showDropDown();
            }
        });
        idCardFilterAdapter = new IDCardFilterAdapter(getContext());
        binding.etCardNumber.setAdapter(idCardFilterAdapter);
        idCardFilterAdapter.setListener((view, position) -> {
            IDCard idCard = idCardFilterAdapter.getDataList().get(position);
            viewModel.searchLocalIDCard(idCard.getCardNumber());
        });
        viewModel.loadIDCardList();
        if (viewModel.idCardListLiveData != null) {
            viewModel.idCardListLiveData.observe(this, idCardList -> {
                idCardFilterAdapter.setUnfilteredData((ArrayList<IDCard>) idCardList);
                //idCardFilterAdapter.notifyDataSetChanged();
            });
        }
    }

    private InterventionAdapter.OnHeaderClickListener headerListener = () -> {
        mListener.replaceFragment(CameraFragment.newInstance());
    };

    private InterventionAdapter.OnBodyClickListener bodyListener = (view, position) -> {
        mListener.replaceFragment(PhotoFragment.newInstance(inspectAdapter.getData(position - 1).getBitmap()));
    };

    private InterventionAdapter.OnBodyCheckBoxClickListener checkBoxListener = (view, position) -> {
        Photograph select = inspectAdapter.getDataList().get(position - 1);
        if (!select.isSelect()) {
            if (viewModel.getSelectSize() < InspectViewModel.MAX_COUNT) {
                updateSelectIcon(select, position);
            } else {
                ToastManager.toast("最多选择" + InspectViewModel.MAX_COUNT + "张实物图片", ToastManager.INFO);
            }
        } else {
            updateSelectIcon(select, position);
        }
    };

    private Observer<List<Photograph>> photographObserver = photographList -> {
        inspectAdapter.setDataList(photographList);
        inspectAdapter.notifyDataSetChanged();
        updateSelectText();
    };

    private Observer<Boolean> confirmObserver = flag -> {
        if (flag) {
            if (isAgreementCustomer) {
                mListener.replaceFragment(AgreementCustomersFragment.newInstance(viewModel.idCardRecord,customer));
            } else {
                mListener.replaceFragment(ExpressFragment.newInstance(viewModel.idCardRecord));
            }
        } else {
            ToastManager.toast("发生错误，请重新确认", ToastManager.ERROR);
        }
    };

    private Observer<Boolean> idCardObserver = flag -> {
        if (flag && viewModel.idCardRecordCache != null) {
            mListener.replaceFragment(FaceVerifyFragment.newInstance(viewModel.idCardRecordCache, isAgreementCustomer,customer));
        }
    };

    private Observer<Boolean> alarmFlagObserver = flag -> mListener.backToStack(HomeFragment.class);

    private void updateSelectText() {
        binding.tvSelect.setText(String.format("已选 %s / %s", viewModel.getSelectSize(), InspectViewModel.MAX_COUNT));
    }

    private void updateSelectIcon(Photograph select, int position) {
        inspectAdapter.getDataList().get(position - 1).setSelect(!select.isSelect());
        inspectAdapter.notifyItemChanged(position);
        updateSelectText();
    }

    public void setIdCardRecord(IDCardRecord idCardRecord) {
        this.idCardRecord = idCardRecord;
    }

    public void setIdCardRecord(IDCardRecord idCardRecord, boolean isAgreementCustomer) {
        this.idCardRecord = idCardRecord;
        this.isAgreementCustomer = isAgreementCustomer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
