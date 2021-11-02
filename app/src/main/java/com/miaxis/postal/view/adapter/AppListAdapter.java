package com.miaxis.postal.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.AppEntity;
import com.miaxis.postal.databinding.ItemAppBinding;
import com.miaxis.postal.view.base.BaseAdapter;
import com.miaxis.postal.view.base.BaseViewHolder;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class AppListAdapter extends BaseAdapter<AppEntity.DataBean, RecyclerView.ViewHolder> {

    private OnDownloadClickListener mOnDownloadClickListener;

    public AppListAdapter() {
        super(null);
    }

    public void setOnDownloadClickListener(OnDownloadClickListener onDownloadClickListener) {
        this.mOnDownloadClickListener = onDownloadClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppBinding bodyBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_app, parent, false);
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
        AppEntity.DataBean appItem = dataList.get(position);
        if (appItem != null) {
            appItem.position = String.valueOf(position + 1);
            holder.bind(appItem, this.mOnDownloadClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class BodyViewHolder extends BaseViewHolder<ItemAppBinding> implements View.OnClickListener {

        private OnDownloadClickListener mOnDownloadClickListener;

        BodyViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(AppEntity.DataBean appItem, OnDownloadClickListener onDownloadClickListener) {
            getBinding().setItem(appItem);
            getBinding().tvOperation.setOnClickListener(this);
            //            getBinding().tvDelete.setOnClickListener(this);
            this.mOnDownloadClickListener = onDownloadClickListener;
        }

        @Override
        public void onClick(View v) {
            if (this.mOnDownloadClickListener != null) {
                AppEntity.DataBean item = getBinding().getItem();
                if (item != null) {
                    if (item.isDownload()) {
                        if (v.getId() == R.id.tv_operation) {
                            this.mOnDownloadClickListener.onInstallClick(this, item, getLayoutPosition());
                        } else {
                            //                            this.mOnDownloadClickListener.onDeleteClick(this, item, getLayoutPosition());
                        }
                    } else {
                        this.mOnDownloadClickListener.onDownloadClick(this, item, getLayoutPosition());
                    }
                }
            }
        }
    }

    public interface OnDownloadClickListener {

        void onDownloadClick(BodyViewHolder view, AppEntity.DataBean appItem, int position);

        void onInstallClick(BodyViewHolder view, AppEntity.DataBean appItem, int position);

        //        void onDeleteClick(BodyViewHolder view, AppItem appItem, int position);

    }

}
