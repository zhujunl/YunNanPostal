package com.miaxis.postal.view.adapter;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.miaxis.postal.R;
import com.miaxis.postal.databinding.ItemOrderImageBinding;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewHolder;
import com.miaxis.postal.view.base.BaseViewModelAdapter;

import androidx.annotation.NonNull;

public class OrderImageAdapter extends BaseViewModelAdapter<String, ItemOrderImageBinding, OrderImageAdapter.MyViewHolder> {

    private OnItemClickListener listener;

    public OrderImageAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setContentView() {
        return R.layout.item_order_image;
    }

    @Override
    protected MyViewHolder createViewHolder(View view, ItemOrderImageBinding binding) {
        MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String item = dataList.get(position);
        holder.getBinding().setItem(item);
        RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(30));
        Glide.with(context)
                .load(item)
                .apply(options)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.getBinding().ivImage);
        holder.getBinding().ivImage.setOnClickListener(new OnLimitClickHelper(view -> {
            if (listener != null) {
                listener.onItemClick(holder.getBinding().ivImage, holder.getLayoutPosition());
            }
        }));
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class MyViewHolder extends BaseViewHolder<ItemOrderImageBinding> {
        MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
