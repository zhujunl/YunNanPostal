package com.miaxis.postal.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.databinding.ItemSelectBranchBinding;
import com.miaxis.postal.view.base.BaseAdapter;
import com.miaxis.postal.view.base.BaseViewHolder;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class BranchSelectAdapter extends BaseAdapter<Branch, RecyclerView.ViewHolder> {

    private OnBodyClickListener bodyListener;

    public BranchSelectAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSelectBranchBinding bodyBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_select_branch, parent, false);
        BodyViewHolder baseViewHolder = new BodyViewHolder(bodyBinding.getRoot());
        baseViewHolder.setBinding(bodyBinding);
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BodyViewHolder) {
            setBodyItemValues((BodyViewHolder) holder, position);
        }
    }

    private void setBodyItemValues(BodyViewHolder holder, int position) {
        Branch branch = dataList.get(position);
        holder.bind(branch, bodyListener);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class BodyViewHolder extends BaseViewHolder<ItemSelectBranchBinding> implements View.OnClickListener {

        BodyViewHolder(View itemView) {
            super(itemView);
        }

        private OnBodyClickListener mOnBodyClickListener;

        public void bind(Branch branch, OnBodyClickListener onBodyClickListener) {
            getBinding().setItem(branch);
            this.mOnBodyClickListener = onBodyClickListener;
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (this.mOnBodyClickListener != null) {
                this.mOnBodyClickListener.onBodyClick(getBinding().getRoot(), getBinding().getItem(), getLayoutPosition());
            }
        }
    }

    public interface OnBodyClickListener {
        void onBodyClick(View view, Branch branch, int position);
    }

    public void setBodyListener(OnBodyClickListener bodyListener) {
        this.bodyListener = bodyListener;
    }
}
