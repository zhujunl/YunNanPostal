package com.miaxis.postal.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.miaxis.postal.R;
import com.miaxis.postal.bridge.GlideApp;
import com.miaxis.postal.data.entity.Local;
import com.miaxis.postal.databinding.ItemLocalBinding;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewHolder;
import com.miaxis.postal.view.base.BaseViewModelAdapter;

import androidx.annotation.NonNull;

public class LocalAdapter extends BaseViewModelAdapter<Local, ItemLocalBinding, LocalAdapter.MyViewHolder> {

    private OnItemClickListener listener;

    public LocalAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setContentView() {
        return R.layout.item_local;
    }

    @Override
    protected MyViewHolder createViewHolder(View view, ItemLocalBinding binding) {
        MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            Local item = dataList.get(position);
            holder.getBinding().setItem(item);
            holder.getBinding().tvUpload.setText(item.getExpress().isUpload() ? "已上传" : "未上传");
            holder.getBinding().tvError.setOnClickListener(null);

            if (!item.getExpress().isUpload()) {
                if (PostalManager.isNetworkAvailable()) {
                    holder.getBinding().tvError.setText("信息提示：" + (TextUtils.isEmpty(item.getExpress().getUploadError()) ? "等待上传" : item.getExpress().getUploadError()));
                    if (!TextUtils.isEmpty(item.getExpress().getUploadError())) {
                        holder.getBinding().tvError.setOnClickListener(v -> ToastManager.toast(item.getExpress().getUploadError(), ToastManager.ERROR));
                    }
                } else {
                    holder.getBinding().tvError.setText("网络连接失败！请检查网络");
                }
            }
            holder.getBinding().tvUpload.setTextColor(item.getExpress().isUpload()
                    ? context.getResources().getColor(R.color.darkgreen)
                    : context.getResources().getColor(R.color.darkred));
            if (item.getExpress() != null && item.getExpress().getPhotoPathList() != null && !item.getExpress().getPhotoPathList().isEmpty()) {
                RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(30));
                GlideApp.with(holder.getBinding().ivImage)
                        .load(item.getExpress().getPhotoPathList().get(0))
                        .apply(options)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.getBinding().ivImage);
            }
            holder.getBinding().cvOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(holder.getBinding().cvOrder, holder.getLayoutPosition());
                }
            });
            holder.getBinding().ivImage.setOnClickListener(new OnLimitClickHelper(view -> {
                try {
                    if (listener != null) {
                        listener.onThumbnail(item.getExpress().getPhotoPathList().get(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class MyViewHolder extends BaseViewHolder<ItemLocalBinding> {
        MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onThumbnail(String url);
    }

}