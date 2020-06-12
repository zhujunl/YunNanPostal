package com.miaxis.postal.view.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.bridge.GlideApp;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.data.entity.Photograph;
import com.miaxis.postal.data.event.TakePhotoEvent;
import com.miaxis.postal.databinding.FragmentRecordUpdateBinding;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.view.adapter.InspectAdapter;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.auxiliary.OnLimitClickListener;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.InspectViewModel;
import com.miaxis.postal.viewModel.RecordUpdateViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class RecordUpdateFragment extends BaseViewModelFragment<FragmentRecordUpdateBinding, RecordUpdateViewModel> {

    private InspectAdapter inspectAdapter;

    private Order order;

    public static RecordUpdateFragment newInstance(Order order) {
        RecordUpdateFragment fragment = new RecordUpdateFragment();
        fragment.setOrder(order);
        return fragment;
    }

    public RecordUpdateFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_record_update;
    }

    @Override
    protected RecordUpdateViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(RecordUpdateViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.orderCodeImageUpdate.observe(this, orderCodeImageUpdateObserver);
        viewModel.updateResult.observe(this, updateResultObserver);
    }

    @Override
    protected void initView() {
        initRecycleView();
        viewModel.photographList.observe(this, photographObserver);
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.btnUpdate.setOnClickListener(new OnLimitClickHelper(confirmClickListener));
        viewModel.showBarcodeImage(order.getOrderCode());
        viewModel.initOrder(order);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(getContext())
                .title("确认退出？")
                .content("已修改内容将丢失")
                .positiveText("确认")
                .onPositive((dialog, which) -> mListener.backToStack(null))
                .negativeText("取消")
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.photographList.removeObserver(photographObserver);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onTakePhotoEvent(TakePhotoEvent event) {
        viewModel.addPhotograph(event.getPhotoList());
        EventBus.getDefault().removeStickyEvent(event);
    }

    private Observer<List<Photograph>> photographObserver = photographList -> {
        inspectAdapter.setDataList(photographList);
        inspectAdapter.notifyDataSetChanged();
        updateSelectText();
    };

    private Observer<Boolean> orderCodeImageUpdateObserver = flag -> {
        if (flag) {
            GlideApp.with(this)
                    .load(viewModel.orderCodeBitmapCache)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.ivBarcode);
        }
    };

    private Observer<Boolean> updateResultObserver = result -> {
        if (result) {
            mListener.backToStack(null);
        }
    };

    private InspectAdapter.OnHeaderClickListener headerListener = () -> {
        mListener.replaceFragment(CameraFragment.newInstance());
    };

    private InspectAdapter.OnBodyClickListener bodyListener = (view, position) -> {
        mListener.replaceFragment(PhotoFragment.newInstance(inspectAdapter.getData(position - 1).getBitmap()));
    };

    private InspectAdapter.OnBodyCheckBoxClickListener checkBoxListener = (view, position) -> {
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

    private OnLimitClickListener confirmClickListener = view -> {
        if (checkInput()) {
            new MaterialDialog.Builder(getContext())
                    .content("确认修改？")
                    .positiveText("确认")
                    .onPositive((dialog, which) -> {
                        viewModel.updateOrder();
                    })
                    .negativeText("取消")
                    .show();
        }
    };

    private void initRecycleView() {
        inspectAdapter = new InspectAdapter(getContext());
        inspectAdapter.setHeaderListener(headerListener);
        inspectAdapter.setBodyListener(bodyListener);
        inspectAdapter.setCheckBoxListener(checkBoxListener);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        binding.rvInspect.setLayoutManager(gridLayoutManager);
        binding.rvInspect.setAdapter(inspectAdapter);
        ((SimpleItemAnimator) binding.rvInspect.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private void updateSelectText() {
        binding.tvSelect.setText(String.format("已选 %s / %s", viewModel.getSelectSize(), InspectViewModel.MAX_COUNT));
    }

    private void updateSelectIcon(Photograph select, int position) {
        inspectAdapter.getDataList().get(position - 1).setSelect(!select.isSelect());
        inspectAdapter.notifyItemChanged(position);
        updateSelectText();
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(binding.etInfo.getText().toString())) {
            ToastManager.toast("请输入物品名称", ToastManager.INFO);
            return false;
        } else if (TextUtils.isEmpty(binding.etWeight.getText().toString())) {
            ToastManager.toast("请输入物品重量", ToastManager.INFO);
            return false;
        } else if (TextUtils.isEmpty(binding.etAddresseeName.getText().toString())) {
            ToastManager.toast("请输入收件人姓名", ToastManager.INFO);
            return false;
        } else if (TextUtils.isEmpty(binding.etAddresseePhone.getText().toString())) {
            ToastManager.toast("请输入收件人手机号码", ToastManager.INFO);
            return false;
        } else if (TextUtils.isEmpty(binding.etAddresseeAddress.getText().toString())) {
            ToastManager.toast("请输入收件地址", ToastManager.INFO);
            return false;
        } else if (viewModel.getSelectList().size() <= 0) {
            ToastManager.toast("请至少选择一张实物照片", ToastManager.INFO);
            return false;
        }
        return true;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
