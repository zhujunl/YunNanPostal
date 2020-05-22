package com.miaxis.postal.manager;

import com.miaxis.postal.data.entity.Courier;

public class DataCacheManager {

    private DataCacheManager() {
    }

    public static DataCacheManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final DataCacheManager instance = new DataCacheManager();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    private Courier courier;

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }
}
