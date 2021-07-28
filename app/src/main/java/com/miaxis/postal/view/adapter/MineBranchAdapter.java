package com.miaxis.postal.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.databinding.ItemMineBranchBinding;
import com.miaxis.postal.view.base.BaseAdapter;
import com.miaxis.postal.view.base.BaseViewHolder;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class MineBranchAdapter extends BaseAdapter<Branch, RecyclerView.ViewHolder> {

    private OnBodyClickListener bodyListener;

    public MineBranchAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMineBranchBinding bodyBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_mine_branch, parent, false);
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
        holder.bind(branch,this.bodyListener);
    }

    @Override
    public int getItemCount() {
        return dataList.size() ;
    }

    static class BodyViewHolder extends BaseViewHolder<ItemMineBranchBinding> implements View.OnLongClickListener {
        BodyViewHolder(View itemView) {
            super(itemView);
        }

        private MineBranchAdapter.OnBodyClickListener mOnBodyClickListener;

        public void bind(Branch branch, MineBranchAdapter.OnBodyClickListener onBodyClickListener) {
            getBinding().setItem(branch);
            this.mOnBodyClickListener = onBodyClickListener;
            this.itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (this.mOnBodyClickListener != null) {
                this.mOnBodyClickListener.onBodyClick(getBinding().getRoot(), getBinding().getItem(), getLayoutPosition());
            }
            return false;
        }
    }

    public interface OnBodyClickListener {
        void onBodyClick(View view,Branch branch, int position);
    }


    public void setBodyListener(OnBodyClickListener bodyListener) {
        this.bodyListener = bodyListener;
    }
}
