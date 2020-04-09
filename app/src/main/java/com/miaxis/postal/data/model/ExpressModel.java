package com.miaxis.postal.data.model;

import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.util.FileUtil;

import java.util.List;

public class ExpressModel {

    public static void saveExpress(Express express) {
        AppDatabase.getInstance().expressDao().insert(express);
    }

    public static List<Express> loadExpress(String verifyId) {
        return AppDatabase.getInstance().expressDao().loadExpress(verifyId);
    }

    public static void deleteExpress(Express express) {
        AppDatabase.getInstance().expressDao().delete(express);
        for (String path : express.getPhotoPathList()) {
            FileUtil.deleteImg(path);
        }
    }

    public static List<Express> loadAll() {
        return AppDatabase.getInstance().expressDao().loadAll();
    }

    public static List<Express> loadExpressByPage(int pageNum, int pageSize) {
        return AppDatabase.getInstance().expressDao().loadExpressByPage(pageNum, pageSize);
    }

    public static int loadExpressCount() {
        return AppDatabase.getInstance().expressDao().loadExpressCount();
    }

}
