package com.miaxis.postal.view.auxiliary;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.miaxis.postal.bridge.GlideApp;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by tang.yf on 2018/11/5.
 */

/**
 * 适配Banner的图片加载
 */
public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        GlideApp.with(context).load(path).into(imageView);
    }

}
