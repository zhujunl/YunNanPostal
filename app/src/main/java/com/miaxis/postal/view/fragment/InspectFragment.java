package com.miaxis.postal.view.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.miaxis.postal.R;
import com.miaxis.postal.bridge.GlideApp;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.Photograph;
import com.miaxis.postal.data.event.ExpressEditEvent;
import com.miaxis.postal.data.event.TakePhotoEvent;
import com.miaxis.postal.databinding.FragmentInspectBinding;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.view.adapter.InspectAdapter;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.auxiliary.OnLimitClickListener;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.InspectViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class InspectFragment extends BaseViewModelFragment<FragmentInspectBinding, InspectViewModel> {

    private Express express;
    private InspectAdapter inspectAdapter;

    public static InspectFragment newInstance(Express express) {
        InspectFragment inspectFragment = new InspectFragment();
        inspectFragment.setExpress(express);
        return inspectFragment;
    }

    public InspectFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_inspect;
    }

    @Override
    protected InspectViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(InspectViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.barcodeImageUpdate.observe(this, barcodeImageUpdateObserver);
    }

    @Override
    protected void initView() {
        initRecycleView();
        viewModel.photographList.observe(this, photographObserver);
        if (viewModel.express.get() == null) {
            viewModel.express.set(express);
            viewModel.initExpress(express);
        }
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.ivDelete.setOnClickListener(new OnLimitClickHelper(view -> {
            new MaterialDialog.Builder(getContext())
                    .title("确认删除？")
                    .positiveText("确认")
                    .onPositive((dialog, which) -> {
                        viewModel.makeDeleteResult();
                        mListener.backToStack(null);
                    })
                    .negativeText("取消")
                    .show();
        }));
        binding.btnConfirm.setOnClickListener(new OnLimitClickHelper(view -> {
            if (TextUtils.isEmpty(binding.etInfo.getText().toString())) {
                ToastManager.toast("请输入物品名称及描述", ToastManager.INFO);
            } else if (viewModel.getSelectList().size() != 0) {
                new MaterialDialog.Builder(getContext())
                        .title("确认已选择的实物照片")
                        .content("未选择的实物照片将会被删除")
                        .positiveText("确认")
                        .onPositive((dialog, which) -> {
                            viewModel.makeModifyResult(binding.etInfo.getText().toString());
                            mListener.backToStack(null);
                        })
                        .negativeText("取消")
                        .show();
            } else {
                ToastManager.toast("请至少选择一张实物照片", ToastManager.INFO);
            }
        }));
        binding.fabAlarm.setOnLongClickListener(alarmListener);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onTakePhotoEvent(TakePhotoEvent event) {
        viewModel.addPhotograph(event.getPhotoList());
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (viewModel.needBackCheck()) {
            new MaterialDialog.Builder(getContext())
                    .title("确认退出？")
                    .content("未选择和未确认的实物照片将会被删除")
                    .positiveText("确认")
                    .onPositive((dialog, which) -> mListener.backToStack(null))
                    .negativeText("取消")
                    .show();
        } else {
            mListener.backToStack(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.photographList.removeObserver(photographObserver);
        EventBus.getDefault().unregister(this);
    }

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

    private Observer<List<Photograph>> photographObserver = photographList -> {
        inspectAdapter.setDataList(photographList);
        inspectAdapter.notifyDataSetChanged();
        updateSelectText();
    };

    private Observer<Boolean> barcodeImageUpdateObserver = flag -> {
        if (flag) {
            GlideApp.with(this)
                    .load(viewModel.barcodeBitmapCache)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.ivBarcode);
        }
    };

    private void updateSelectText() {
        binding.tvSelect.setText(String.format("已选 %s / %s", viewModel.getSelectSize(), InspectViewModel.MAX_COUNT));
    }

    private void updateSelectIcon(Photograph select, int position) {
        inspectAdapter.getDataList().get(position - 1).setSelect(!select.isSelect());
        inspectAdapter.notifyItemChanged(position);
        updateSelectText();
    }

    public void setExpress(Express express) {
        this.express = express;
    }

    private View.OnLongClickListener alarmListener = v -> {
        viewModel.alarm();
        mListener.backToStack(null);
        return false;
    };

}
