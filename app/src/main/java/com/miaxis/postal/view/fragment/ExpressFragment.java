package com.miaxis.postal.view.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.provider.MediaStore;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.databinding.FragmentExpressBinding;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.view.adapter.ExpressAdapter;
import com.miaxis.postal.view.adapter.SpacesItemDecoration;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.ExpressViewModel;
import com.speedata.libid2.IDInfor;

import java.util.List;

public class ExpressFragment extends BaseViewModelFragment<FragmentExpressBinding, ExpressViewModel> {

    public static final int REQUEST_CODE = 11;

    private IDInfor idInfor;
    private Bitmap header;
    private TempId tempId;

    private ExpressAdapter expressAdapter;
    private MaterialDialog scanDialog;

    private Uri imageUriCache = null;
    private Express expressCache = null;

    public static ExpressFragment newInstance(IDInfor idInfor, Bitmap header, TempId tempId) {
        ExpressFragment fragment = new ExpressFragment();
        fragment.setIdInfor(idInfor);
        fragment.setHeader(header);
        fragment.setTempId(tempId);
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
        return ViewModelProviders.of(this).get(ExpressViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.idInfor.set(idInfor);
        viewModel.header.set(header);
        viewModel.tempId.set(tempId);
    }

    @Override
    protected void initView() {
        initDialog();
        if (expressAdapter == null) {
            expressAdapter = new ExpressAdapter(getContext(), viewModel);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.rvExpress.setLayoutManager(gridLayoutManager);
        binding.rvExpress.setAdapter(expressAdapter);
        binding.rvExpress.addItemDecoration(new SpacesItemDecoration(1));
        ((SimpleItemAnimator) binding.rvExpress.getItemAnimator()).setSupportsChangeAnimations(false);
        expressAdapter.setHeaderListener(() -> {
//            if (viewModel.checkInput()) {
//                if (binding.etPhone.isEnabled() && viewModel.getPhotoList().size() == 0) {
//                    new MaterialDialog.Builder(getContext())
//                            .title("确认输入")
//                            .content("确认寄件人手机号码以及寄件地址输入正确？\n(确认后不可修改)")
//                            .positiveText("确认")
//                            .onPositive((dialog, which) -> {
//                                binding.etPhone.setEnabled(false);
//                                binding.etAddress.setEnabled(false);
//                                scanDialog.show();
//                                viewModel.startScan();
//                            })
//                            .negativeText("取消")
//                            .show();
//                } else {
                    scanDialog.show();
                    viewModel.startScan();
//                }
//            } else {
//                ToastManager.toast("请先输入寄件人手机号码以及寄件地址", ToastManager.INFO);
//            }
        });
        binding.ivAddress.setOnClickListener(v -> viewModel.getLocation());
        binding.fabConfirm.setOnClickListener(v -> {
            new MaterialDialog.Builder(getContext())
                    .title("确认离开")
                    .content("确认已完成该寄件人名下的所有订单了吗？")
                    .positiveText("确认")
                    .onPositive((dialog, which) -> mListener.backToStack(HomeFragment.class))
                    .negativeText("取消")
                    .show();
        });
        viewModel.expressList.observe(this, expressListObserver);
        viewModel.newExpress.observe(this, newExpressObserver);
        viewModel.repeatExpress.observe(this, repeatExpressObserver);
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(getContext())
                .title("确认离开页面？")
                .positiveText("确认")
                .onPositive((dialog, which) -> mListener.backToStack(HomeFragment.class))
                .negativeText("取消")
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (imageUriCache != null && expressCache != null) {
                Bitmap bitmapFormUri = CameraManager.getBitmapFormUri(getContext(), imageUriCache);
                if (bitmapFormUri != null) {
                    viewModel.addExpress(expressCache, bitmapFormUri);
                    imageUriCache = null;
                    expressCache = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.expressList.removeObserver(expressListObserver);
    }

    private Observer<List<Express>> expressListObserver = expressList -> {
        expressAdapter.notifyDataSetChanged();
    };

    private Observer<Express> newExpressObserver = express -> {
        this.expressCache = express;
        this.imageUriCache = null;
        scanDialog.dismiss();
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUriCache = CameraManager.getOutputMediaFileUri(getContext());
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriCache);
        //Android7.0添加临时权限标记，此步千万别忘了
        openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        getActivity().startActivityForResult(openCameraIntent, REQUEST_CODE);
    };

    private Observer<Express> repeatExpressObserver = express -> {
        this.expressCache = null;
        this.imageUriCache = null;
        scanDialog.dismiss();
        ToastManager.toast("该条码编号已重复", ToastManager.INFO);
    };

    private void initDialog() {
        scanDialog = new MaterialDialog.Builder(getContext())
                .title("新建订单")
                .progress(true, 100)
                .content("请将扫描口对准条码进行扫描")
                .positiveText("取消扫描")
                .onPositive((dialog, which) -> viewModel.stopScan())
                .build();
    }

    public void setIdInfor(IDInfor idInfor) {
        this.idInfor = idInfor;
    }

    public void setHeader(Bitmap header) {
        this.header = header;
    }

    public void setTempId(TempId tempId) {
        this.tempId = tempId;
    }
}
