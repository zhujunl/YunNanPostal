package com.miaxis.postal.view.auxiliary;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.miaxis.postal.bridge.GlideApp;

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
        GlideApp.with(view)
                .load(o)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }

}
