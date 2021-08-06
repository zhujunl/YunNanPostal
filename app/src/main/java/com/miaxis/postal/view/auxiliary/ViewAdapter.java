package com.miaxis.postal.view.auxiliary;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import androidx.databinding.BindingAdapter;

public class ViewAdapter {

    @BindingAdapter(value = {"isInvisible"}, requireAll = false)
    public static void isInvisible(View view, Boolean visibility) {
        if (visibility == null) return;
        if (visibility) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    @BindingAdapter(value = {"isGone"}, requireAll = false)
    public static void isGone(View view, Boolean visibility) {
        if (visibility == null) return;
        if (visibility) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter(value = {"imageSource"}, requireAll = false)
    public static void imageSource(ImageView view, Object o) {
        if (o == null) return;
        Glide.with(view)
                .load(o)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }

}
