package com.miaxis.postal.view.fragment;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.miaxis.postal.R;
import com.miaxis.postal.bridge.GlideApp;
import com.miaxis.postal.databinding.FragmentPhotoBinding;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.PhotoViewModel;

import androidx.lifecycle.ViewModelProvider;

public class PhotoFragment extends BaseViewModelFragment<FragmentPhotoBinding, PhotoViewModel> {

    private Object image;

    public static PhotoFragment newInstance(Object image) {
        PhotoFragment fragment = new PhotoFragment();
        fragment.setImage(image);
        return fragment;
    }

    public PhotoFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_photo;
    }

    @Override
    protected PhotoViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(PhotoViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        GlideApp.with(this)
                .load(image)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.pvPhoto);
        binding.pvPhoto.setOnClickListener(v -> onBackPressed());
        binding.clPhoto.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

    public void setImage(Object image) {
        this.image = image;
    }
}
