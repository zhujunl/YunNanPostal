package com.miaxis.postal.data.event;

import com.miaxis.postal.data.entity.Express;

public class ExpressEditEvent {

    public static final int MODE_MODIFY = 1;
    public static final int MODE_DELETE = 2;

    private int mode;
    private Express express;

    public ExpressEditEvent(int mode, Express express) {
        this.mode = mode;
        this.express = express;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public Express getExpress() {
        return express;
    }

    public void setExpress(Express express) {
        this.express = express;
    }
}
