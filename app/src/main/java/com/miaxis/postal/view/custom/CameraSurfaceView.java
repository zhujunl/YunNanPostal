package com.miaxis.postal.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.miaxis.postal.data.event.SurfaceCreateEvent;
import com.miaxis.postal.manager.CameraManager;

import org.greenrobot.eventbus.EventBus;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context context;
    private SurfaceHolder surfaceHolder;
    private final Byte cameraLock = 3;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        context = context;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFormat(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    public SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        CameraManager.getInstance().openCamera(holder);
//        EventBus.getDefault().post(new SurfaceCreateEvent());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        CameraManager.getInstance().closeCamera();
    }

}
