package com.miaxis.postal.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.databinding.ItemSearchOrderBinding;
import com.miaxis.postal.util.ListUtils;
import com.miaxis.postal.view.base.BaseAdapter;
import com.miaxis.postal.view.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class OrderListAdapter extends BaseAdapter<Order, RecyclerView.ViewHolder> {

    private OnClickListener mOnClickListener;

    public OrderListAdapter() {
        super(null);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchOrderBinding bodyBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_search_order, parent, false);
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
        Order order = dataList.get(position);
        if (order != null) {
            holder.bind(order, this.mOnClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class BodyViewHolder extends BaseViewHolder<ItemSearchOrderBinding> implements View.OnClickListener {

        private OnClickListener mOnClickListener;

        BodyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(Order order, OnClickListener onClickListener) {
            getBinding().setItem(order);
            if (order == null) {
                return;
            }
            this.mOnClickListener = onClickListener;
            List<String> imageList = order.getImageList();
            Glide.with(getBinding().ivPhoto).load(ListUtils.isNullOrEmpty(imageList) ? null : imageList.get(0)).error(R.drawable.ic_logo_postal_playstore).into(getBinding().ivPhoto);
        }

        @Override
        public void onClick(View v) {
            if (this.mOnClickListener != null) {
                Order order = getBinding().getItem();
                this.mOnClickListener.onItemClick(this, order, getLayoutPosition());
            }
        }
    }

    public interface OnClickListener {

        void onItemClick(BodyViewHolder view, Order order, int position);

    }

}
