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
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.databinding.ItemExpressBodyBinding;
import com.miaxis.postal.databinding.ItemExpressHeaderBinding;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseAdapter;
import com.miaxis.postal.view.base.BaseViewHolder;
import com.miaxis.postal.viewModel.ExpressViewModel;

public class ExpressAdapter extends BaseAdapter<Express, RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_BODY = 1;

    private OnHeaderClickListener headerListener;
    private OnBodyClickListener bodyListener;

    public ExpressAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                ItemExpressHeaderBinding headerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_express_header, parent, false);
                HeaderViewHolder headerViewHolder = new HeaderViewHolder(headerBinding.getRoot());
                headerViewHolder.setBinding(headerBinding);
                return headerViewHolder;
            case TYPE_BODY:
            default:
                ItemExpressBodyBinding bodyBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_express_body, parent, false);
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
        Express express = dataList.get(position - 1);
        holder.getBinding().setItem(express);
        holder.itemView.setOnClickListener(new OnLimitClickHelper(view -> {
            if (bodyListener != null) {
                bodyListener.onBodyClick(holder.itemView, holder.getLayoutPosition());
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
        return dataList.size() + 1;
    }

    class HeaderViewHolder extends BaseViewHolder<ItemExpressHeaderBinding> {
        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class BodyViewHolder extends BaseViewHolder<ItemExpressBodyBinding> {
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
