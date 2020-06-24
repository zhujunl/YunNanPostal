package com.miaxis.postal.view.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Draft;
import com.miaxis.postal.databinding.ItemDraftBinding;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewHolder;
import com.miaxis.postal.view.base.BaseViewModelAdapter;

public class DraftAdapter extends BaseViewModelAdapter<Draft, ItemDraftBinding, DraftAdapter.MyViewHolder> {

    private OnItemClickListener listener;

    public DraftAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setContentView() {
        return R.layout.item_draft;
    }

    @Override
    protected MyViewHolder createViewHolder(View view, ItemDraftBinding binding) {
        MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Draft draft = dataList.get(position);
        holder.getBinding().setItem(draft);
        holder.getBinding().cvOrder.setOnClickListener(new OnLimitClickHelper(view -> {
            listener.onItemClick(holder.getBinding().cvOrder, holder.getLayoutPosition());
        }));
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class MyViewHolder extends BaseViewHolder<ItemDraftBinding> {
        MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}