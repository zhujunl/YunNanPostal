package com.miaxis.postal.view.fragment;

import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.miaxis.postal.R;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.Photograph;
import com.miaxis.postal.data.event.ExpressEditEvent;
import com.miaxis.postal.data.event.TakePhotoEvent;
import com.miaxis.postal.databinding.FragmentInspectBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.adapter.InspectAdapter;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.InspectViewModel;
import com.miaxis.postal.viewModel.LoginViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

//开箱验视
public class InspectFragment extends BaseViewModelFragment<FragmentInspectBinding, InspectViewModel> {

    private final String TAG = "InspectFragment";
    private Express express;
    private InspectAdapter inspectAdapter;

    private String name, phone, goodName, goodCounts, sendAddress;

    public static InspectFragment newInstance(Express express) {
        InspectFragment inspectFragment = new InspectFragment();
        inspectFragment.setExpress(express);
        return inspectFragment;
    }

    public static InspectFragment newInstance(Express express, String name, String phone, String goodName, String goodCounts, String sendAddress) {
        InspectFragment inspectFragment = new InspectFragment();
        inspectFragment.setExpressOthers(name, phone, goodName, goodCounts, sendAddress);
        inspectFragment.setExpress(express);
        return inspectFragment;
    }

    public void setExpressOthers(String name, String phone, String goodName, String goodCounts, String sendAddress) {
        this.name = name;
        this.phone = phone;
        this.goodName = goodName;
        this.goodCounts = goodCounts;
        this.sendAddress = sendAddress;
    }

    private InspectFragment() {
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
        Log.e(TAG, "express:" + express);
        viewModel.photographList.observe(this, photographObserver);
        if (express != null) {
            viewModel.initExpress(express);
        }
        viewModel.setExpressOthers(name, phone, goodName, goodCounts, sendAddress);
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(name)) {
            binding.tvClientSNameLabel.setVisibility(View.VISIBLE);
            binding.editClientSName.setVisibility(View.VISIBLE);
            binding.tvClientSPhoneLabel.setVisibility(View.VISIBLE);
            binding.editClientSPhone.setVisibility(View.VISIBLE);
            binding.editClientSName.setText(name);
            binding.editClientSPhone.setText(phone);
        }
        String barCode = express.getBarCode();
        viewModel.showBarcodeImage(barCode);
        if (!TextUtils.isEmpty(barCode) && !barCode.startsWith(App.getInstance().BarHeader)) {
            binding.tvBarcode.setVisibility(View.VISIBLE);
            binding.ivBarcode.setVisibility(View.VISIBLE);
            binding.tvBarcode.setText(barCode);
        } else {
            binding.tvBarcode.setVisibility(View.GONE);
            binding.ivBarcode.setVisibility(View.GONE);
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
            if (checkInput()) {
                if (viewModel.getSelectSize() == viewModel.getPhotographList().size()) {
                    viewModel.makeModifyResult();
                    mListener.backToStack(null);
                } else {
                    new MaterialDialog.Builder(getContext())
                            .title("确认已选择的实物照片")
                            .content("未选择的实物照片将会被删除")
                            .positiveText("确认")
                            .onPositive((dialog, which) -> {
                                viewModel.makeModifyResult();
                                mListener.backToStack(null);
                            })
                            .negativeText("取消")
                            .show();
                }
            }
        }));
        binding.btnDraft.setOnClickListener(new OnLimitClickHelper(view -> {
            if (checkDraftInput()) {
                if (viewModel.getSelectSize() == viewModel.getPhotographList().size()) {
                    viewModel.makeDraftResult();
                    mListener.backToStack(null);
                } else {
                    new MaterialDialog.Builder(getContext())
                            .content("未选择的实物照片将会被删除")
                            .positiveText("确认")
                            .onPositive((dialog, which) -> {
                                viewModel.makeDraftResult();
                                mListener.backToStack(null);
                            })
                            .negativeText("取消")
                            .show();
                }
            }
        }));
        binding.fabAlarm.setOnLongClickListener(alarmListener);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onTakePhotoEvent(TakePhotoEvent event) {
        Log.e(TAG, "onTakePhotoEvent:" + event);
        List<Bitmap> photoList = event.getPhotoList();
        if (photoList != null && !photoList.isEmpty()) {
            viewModel.addPhotograph(photoList.get(0));
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (viewModel.needBackCheck()) {
            new MaterialDialog.Builder(getContext())
                    .title("确认退出？")
                    .content("未选择和未确认的实物照片将会被删除")
                    .positiveText("确认")
                    .onPositive((dialog, which) -> {
                        EventBus.getDefault().postSticky(new ExpressEditEvent(ExpressEditEvent.MODE_MODIFY, viewModel.express.get(), name, phone, goodName, goodCounts, sendAddress));
                        mListener.backToStack(null);
                    })
                    .negativeText("取消")
                    .show();
        } else {
            //
            // EventBus.getDefault().postSticky(new ExpressEditEvent(ExpressEditEvent.MODE_MODIFY, viewModel.express.get(), name, phone, goodName, goodCounts, sendAddress));
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
            Glide.with(this)
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
        //viewModel.alarm();
        //mListener.backToStack(null);
        return false;
    };

    private boolean checkInput() {
        List<Bitmap> selectList = viewModel.getSelectList();
        if (selectList == null || selectList.isEmpty()) {
            ToastManager.toast("请至少选择一张实物照片", ToastManager.ERROR);
            return false;
        }
        return true;
        //       if (viewModel.getSelectList().size() <= 0) {
        //            ToastManager.toast("请至少选择一张实物照片", ToastManager.INFO);
        //            return false;
        //        }
        //return true;
    }

    private boolean checkDraftInput() {
        if (viewModel.getSelectList().size() <= 0) {
            ToastManager.toast("请至少选择一张实物照片", ToastManager.ERROR);
            return false;
        }
        return true;
    }

}
