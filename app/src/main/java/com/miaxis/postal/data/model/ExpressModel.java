package com.miaxis.postal.data.model;

import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.util.SPUtils;
import com.miaxis.postal.util.ValueUtil;

import java.util.List;

public class ExpressModel {

    public static void saveExpress(Express express) {
        String orgCode = SPUtils.getInstance().read(ValueUtil.GlobalPhone, "");
        express.setOrgCode(orgCode);
        AppDatabase.getInstance().expressDao().insert(express);
    }

    public static List<Express> loadExpress(String verifyId) {
        String orgCode = SPUtils.getInstance().read(ValueUtil.GlobalPhone, "");
        return AppDatabase.getInstance().expressDao().loadExpress(ValueUtil.GlobalPhone, orgCode, verifyId);
    }

    public static void deleteExpress(Express express) {
        AppDatabase.getInstance().expressDao().delete(express);
    }

    public static List<Express> loadAll() {
        String orgCode = SPUtils.getInstance().read(ValueUtil.GlobalPhone, "");
        return AppDatabase.getInstance().expressDao().loadAll(ValueUtil.GlobalPhone, orgCode);
    }

    public static List<Express> loadExpressByPage(int pageNum, int pageSize) {
        String orgCode = SPUtils.getInstance().read(ValueUtil.GlobalPhone, "");
        return AppDatabase.getInstance().expressDao().loadExpressByPage(ValueUtil.GlobalPhone, orgCode, pageNum, pageSize);
    }

    public static int loadExpressCount() {
        String orgCode = SPUtils.getInstance().read(ValueUtil.GlobalPhone, "");
        return AppDatabase.getInstance().expressDao().loadExpressCount(ValueUtil.GlobalPhone, orgCode);
    }

    public static int loadExpressAllCount() {
        return AppDatabase.getInstance().expressDao().loadExpressAllCount();
    }

    public static Express loadExpressWithCode(String code) {
        String orgCode = SPUtils.getInstance().read(ValueUtil.GlobalPhone, "");
        return AppDatabase.getInstance().expressDao().loadExpressWithCode(ValueUtil.GlobalPhone, orgCode, code);
    }

}
