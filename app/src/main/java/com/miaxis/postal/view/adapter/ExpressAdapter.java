package com.miaxis.postal.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.R;
import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.databinding.ItemExpressBodyBinding;
import com.miaxis.postal.databinding.ItemExpressHeaderBinding;
import com.miaxis.postal.view.auxiliary.OnLimitClickHelper;
import com.miaxis.postal.view.base.BaseAdapter;
import com.miaxis.postal.view.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

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
                headerBinding.getRoot().setOnClickListener(new OnLimitClickHelper(view -> {
                    if (headerListener != null) {
                        headerListener.onHeaderClick();
                    }
                }));
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BodyViewHolder) {
            setBodyItemValues((BodyViewHolder) holder, position);
        }
    }

    private void setBodyItemValues(BodyViewHolder holder, int position) {
        Express express = dataList.get(position - 1);
        holder.getBinding().setItem(express);
        if (express.isComplete()) {
            holder.getBinding().ivStatus.setImageResource(R.drawable.icon_success);
        } else {
            holder.getBinding().ivStatus.setImageResource(R.drawable.icon_loading);
        }
        List<Bitmap> photoList = express.getPhotoList();
        if (photoList != null && photoList.size() > 0) {
            holder.getBinding().ivExpressImage.setImageBitmap(photoList.get(0));
        }
        holder.itemView.setOnClickListener(new OnLimitClickHelper(view -> {
            if (bodyListener != null) {
                bodyListener.onBodyClick(holder.itemView, holder.getLayoutPosition());
            }
        }));
        if (!TextUtils.isEmpty(express.getBarCode())&&!express.getBarCode().startsWith(App.getInstance().BarHeader)){
            holder.getBinding().tvBarCode.setText(express.getBarCode());
        }else {
            holder.getBinding().tvBarCode.setText("");
        }
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

    static class HeaderViewHolder extends BaseViewHolder<ItemExpressHeaderBinding> {
        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class BodyViewHolder extends BaseViewHolder<ItemExpressBodyBinding> {
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
