package com.miaxis.postal.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.miaxis.postal.R;
import com.miaxis.postal.bridge.GlideApp;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.databinding.ItemOrderBodyBinding;
import com.miaxis.postal.databinding.ItemOrderHeaderBinding;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.auxiliary.OnLimitClickListener;
import com.miaxis.postal.view.base.BaseViewHolder;
import com.miaxis.postal.viewModel.ExpressViewModel;
import com.miaxis.postal.viewModel.OrderViewModel;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_BODY = 1;

    protected Context context;

    private ExpressViewModel viewModel;

    private OnHeaderClickListener headerListener;
    private OnBodyClickListener bodyListener;

    public OrderAdapter(Context context, ExpressViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                ItemOrderHeaderBinding headerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_order_header, parent, false);
                HeaderViewHolder headerViewHolder = new HeaderViewHolder(headerBinding.getRoot());
                headerViewHolder.setBinding(headerBinding);
                return headerViewHolder;
            case TYPE_BODY:
            default:
                ItemOrderBodyBinding bodyBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_order_body, parent, false);
                BodyViewHolder baseViewHolder = new BodyViewHolder(bodyBinding.getRoot());
                baseViewHolder.setBinding(bodyBinding);
                return baseViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            setHeaderItemValues((HeaderViewHolder) holder, position);
        } else if (holder instanceof BodyViewHolder) {
            setBodyItemValues((BodyViewHolder) holder, position);
        }
    }

    private void setHeaderItemValues(HeaderViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new OnLimitClickHelper(view -> {
            if (headerListener != null) {
                headerListener.onHeaderClick();
            }
        }));
    }

    private void setBodyItemValues(BodyViewHolder holder, int position) {
        int i = position - 1;
        Order order = viewModel.getOrderList().get(i < 0 ? 0 : i);
        holder.getBinding().setItem(order);
        switch (order.getStatus()) {
            case SUCCESS:
                GlideApp.with(holder.itemView).load(R.drawable.icon_success).into(holder.getBinding().ivUpload);
                break;
            case LOADING:
                GlideApp.with(holder.itemView).load(R.drawable.icon_loading).into(holder.getBinding().ivUpload);
                break;
            case FAILED:
                GlideApp.with(holder.itemView).load(R.drawable.icon_failed).into(holder.getBinding().ivUpload);
                break;
        }
        holder.itemView.setOnClickListener(new OnLimitClickHelper(view -> {
            if (bodyListener != null) {
                bodyListener.onBodyClick(holder.itemView, position);
            }
        }));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_BODY;
        }
    }

    @Override
    public int getItemCount() {
        return viewModel.getOrderList().size() + 1;
    }

    class HeaderViewHolder extends BaseViewHolder<ItemOrderHeaderBinding> {
        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class BodyViewHolder extends BaseViewHolder<ItemOrderBodyBinding> {
        BodyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnHeaderClickListener {
        void onHeaderClick();
    }

    public interface OnBodyClickListener {
        void onBodyClick(View view, int position);
    }

    public void setHeaderListener(OnHeaderClickListener headerListener) {
        this.headerListener = headerListener;
    }

    public void setBodyListener(OnBodyClickListener bodyListener) {
        this.bodyListener = bodyListener;
    }
}
