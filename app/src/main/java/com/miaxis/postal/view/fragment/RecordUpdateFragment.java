package com.miaxis.postal.view.fragment;

import android.content.res.Configuration;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.data.entity.Photograph;
import com.miaxis.postal.data.event.TakePhotoEvent;
import com.miaxis.postal.databinding.FragmentRecordUpdateBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.adapter.InspectAdapter;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.auxiliary.OnLimitClickListener;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.InspectViewModel;
import com.miaxis.postal.viewModel.LoginViewModel;
import com.miaxis.postal.viewModel.RecordUpdateViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

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
                                .replace(R.id.record_update, new LoginFragment(), null)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });

        }
    };


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
        //进入延时状态,一小时访问一次接口
        deviceHandler.postDelayed(task,3600000);//延迟调用
    }

    @Override
    protected void initView() {
        initRecycleView();
        viewModel.photographList.observe(this, photographObserver);
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.btnUpdate.setOnClickListener(new OnLimitClickHelper(confirmClickListener));
        binding.etWeight.setRawInputType(Configuration.KEYBOARD_QWERTY);
        viewModel.showBarcodeImage(order.getOrderCode());
        if (!TextUtils.isEmpty(order.getOrderCode()) && !order.getOrderCode().startsWith(App.getInstance().BarHeader)) {
            binding.tvBarcode.setVisibility(View.VISIBLE);
            binding.ivBarcode.setVisibility(View.VISIBLE);
            binding.tvBarcode.setText(order.getOrderCode());
        }else {
            binding.tvBarcode.setVisibility(View.GONE);
            binding.ivBarcode.setVisibility(View.GONE);
        }
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
            Glide.with(this)
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
        } else if (TextUtils.isEmpty(binding.etSenderPhone.getText().toString())) {
            ToastManager.toast("请输入寄件人手机号码", ToastManager.INFO);
            return false;
        } else if (TextUtils.isEmpty(binding.etSenderAddress.getText().toString())) {
            ToastManager.toast("请输入寄件地址", ToastManager.INFO);
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
