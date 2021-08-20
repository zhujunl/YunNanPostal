package com.miaxis.postal.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Customer;
import com.miaxis.postal.databinding.ItemCustomerBinding;
import com.miaxis.postal.view.base.BaseAdapter;
import com.miaxis.postal.view.base.BaseViewHolder;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class CustomerAdapter extends BaseAdapter<Customer, RecyclerView.ViewHolder> {

    private OnBodyClickListener bodyListener;

    public CustomerAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomerBinding bodyBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_customer, parent, false);
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
        Customer customer = dataList.get(position);
        holder.bind(customer, bodyListener);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class BodyViewHolder extends BaseViewHolder<ItemCustomerBinding> implements View.OnClickListener {

        BodyViewHolder(View itemView) {
            super(itemView);
        }

        private OnBodyClickListener mOnBodyClickListener;

        public void bind(Customer customer, OnBodyClickListener onBodyClickListener) {
            getBinding().setItem(customer);
            this.mOnBodyClickListener = onBodyClickListener;
            this.itemView.setOnClickListener(this);
            getBinding().tvName.setText(customer.name + "[" + customer.phone + "]");
        }

        @Override
        public void onClick(View v) {
            if (this.mOnBodyClickListener != null) {
                this.mOnBodyClickListener.onBodyClick(getBinding().getRoot(), getBinding().getItem(), getLayoutPosition());
            }
        }
    }

    public interface OnBodyClickListener {
        void onBodyClick(View view, Customer customer, int position);
    }

    public void setBodyListener(OnBodyClickListener bodyListener) {
        this.bodyListener = bodyListener;
    }
}
