package com.miaxis.postal.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.miaxis.postal.R;
import com.miaxis.postal.bridge.GlideApp;
import com.miaxis.postal.data.entity.SimpleOrder;
import com.miaxis.postal.databinding.ItemOrderBinding;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.auxiliary.OnLimitClickListener;
import com.miaxis.postal.view.base.BaseAdapter;
import com.miaxis.postal.view.base.BaseViewHolder;
import com.miaxis.postal.view.base.BaseViewModelAdapter;
import com.miaxis.postal.viewModel.LogisticsViewModel;
import com.miaxis.postal.viewModel.OrderViewModel;

public class OrderAdapter extends BaseViewModelAdapter<SimpleOrder, ItemOrderBinding, OrderAdapter.MyViewHolder> {

    private OnItemClickListener listener;

    public OrderAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setContentView() {
        return R.layout.item_order;
    }

    @Override
    protected MyViewHolder createViewHolder(View view, ItemOrderBinding binding) {
        MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.MyViewHolder holder, int position) {
        SimpleOrder item = dataList.get(position);
        holder.getBinding().setItem(item);
        holder.getBinding().cvOrder.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(holder.getBinding().cvOrder, holder.getLayoutPosition());
            }
        });
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class MyViewHolder extends BaseViewHolder<ItemOrderBinding> {
        MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
