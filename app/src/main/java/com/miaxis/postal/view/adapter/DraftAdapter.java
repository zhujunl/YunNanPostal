package com.miaxis.postal.view.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Draft;
import com.miaxis.postal.databinding.ItemDraftBinding;
import com.miaxis.postal.view.base.BaseViewHolder;
import com.miaxis.postal.view.base.BaseViewModelAdapter;

public class DraftAdapter  extends BaseViewModelAdapter<Draft, ItemDraftBinding, DraftAdapter.MyViewHolder> {

    private OnItemClickListener listener;

    public DraftAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setContentView() {
        return R.layout.item_local;
    }

    @Override
    protected MyViewHolder createViewHolder(View view, ItemDraftBinding binding) {
        MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        try {
//            Local item = dataList.get(position);
//            holder.getBinding().setItem(item);
//            holder.getBinding().tvUpload.setText(item.getExpress().isUpload() ? "已上传" : "未上传");
//            holder.getBinding().tvUpload.setTextColor(item.getExpress().isUpload()
//                    ? context.getResources().getColor(R.color.darkgreen)
//                    : context.getResources().getColor(R.color.darkred));
//            if (item.getExpress() != null && item.getExpress().getPhotoPathList() != null && !item.getExpress().getPhotoPathList().isEmpty()) {
//                RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(30));
//                GlideApp.with(context)
//                        .load(item.getExpress().getPhotoPathList().get(0))
//                        .apply(options)
//                        .skipMemoryCache(true)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .into(holder.getBinding().ivImage);
//            }
//            holder.getBinding().cvOrder.setOnClickListener(v -> {
//                if (listener != null) {
//                    listener.onItemClick(holder.getBinding().cvOrder, holder.getLayoutPosition());
//                }
//            });
//            holder.getBinding().ivImage.setOnClickListener(new OnLimitClickHelper(view -> {
//                try {
//                    if (listener != null) {
//                        listener.onThumbnail(item.getExpress().getPhotoPathList().get(0));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
        void onThumbnail(String url);
    }

}