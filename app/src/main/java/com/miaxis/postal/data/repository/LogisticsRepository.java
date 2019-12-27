package com.miaxis.postal.data.repository;

public class LogisticsRepository extends BaseRepository {

    private LogisticsRepository() {
    }

    public static LogisticsRepository getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final LogisticsRepository instance = new LogisticsRepository();
    }

    /**
     * ================================ 静态内部类单例写法 ================================
     **/

}
