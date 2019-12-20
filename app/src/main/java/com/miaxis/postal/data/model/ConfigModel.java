package com.miaxis.postal.data.model;

import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.Config;

public class ConfigModel {

    public static void saveConfig(Config config) {
        config.setId(1L);
        AppDatabase.getInstance().configDao().deleteAll();
        AppDatabase.getInstance().configDao().insert(config);
    }

    public static Config loadConfig() {
        return AppDatabase.getInstance().configDao().loadConfig();
    }

}
