package com.miaxis.postal.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.miaxis.postal.R;
import com.miaxis.postal.databinding.ItemInspectBodyBinding;
import com.miaxis.postal.databinding.ItemInspectHeaderBinding;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseViewHolder;
import com.miaxis.postal.viewModel.InspectViewModel;

public class InspectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_BODY = 1;

    protected Context context;

    private InspectViewModel viewModel;

    private OnHeaderClickListener headerListener;

    public InspectAdapter(Context context, InspectViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                ItemInspectHeaderBinding headerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_inspect_header, parent, false);
                HeaderViewHolder headerViewHolder = new HeaderViewHolder(headerBinding.getRoot());
                headerViewHolder.setBinding(headerBinding);
                return headerViewHolder;
            case TYPE_BODY:
            default:
                ItemInspectBodyBinding bodyBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_inspect_body, parent, false);
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
        return viewModel.getPhotoList().size() + 1;
    }

    class HeaderViewHolder extends BaseViewHolder<ItemInspectHeaderBinding> {
        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class BodyViewHolder extends BaseViewHolder<ItemInspectBodyBinding> {
        BodyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnHeaderClickListener {
        void onHeaderClick();
    }

    public void setHeaderListener(OnHeaderClickListener headerListener) {
        this.headerListener = headerListener;
    }
}
