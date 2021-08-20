package com.miaxis.postal.view.adapter;

import android.content.Context;
import android.view.View;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.SimpleOrder;
import com.miaxis.postal.databinding.ItemOrderBinding;
import com.miaxis.postal.view.base.BaseViewHolder;
import com.miaxis.postal.view.base.BaseViewModelAdapter;

import androidx.annotation.NonNull;


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
