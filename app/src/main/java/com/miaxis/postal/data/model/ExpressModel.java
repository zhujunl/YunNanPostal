package com.miaxis.postal.data.model;

import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.util.ValueUtil;

import java.util.List;

public class ExpressModel {

    public static void saveExpress(Express express) throws MyException{
        Branch selected = BranchModel.findSelected();
        if (selected == null) {
            throw new MyException("未选择品牌");
        }
        String orgCode = selected.orgCode;
        String orgNode = selected.orgNode;
        express.setOrgCode(orgCode);
        express.setOrgNode(orgNode);
        AppDatabase.getInstance().expressDao().insert(express);
    }

    public static List<Express> loadExpress(String verifyId) {
        return AppDatabase.getInstance().expressDao().loadExpress(ValueUtil.GlobalPhone, verifyId);
    }

    public static void deleteExpress(Express express) {
        AppDatabase.getInstance().expressDao().delete(express);
    }

    public static List<Express> loadAll() {
        return AppDatabase.getInstance().expressDao().loadAll(ValueUtil.GlobalPhone);
    }

    public static List<Express> loadExpressByPage(int pageNum, int pageSize) {
        return AppDatabase.getInstance().expressDao().loadExpressByPage(ValueUtil.GlobalPhone, pageNum, pageSize);
    }

    public static int loadExpressCount() {
        return AppDatabase.getInstance().expressDao().loadExpressCount(ValueUtil.GlobalPhone);
    }

    public static int loadExpressAllCount() {
        return AppDatabase.getInstance().expressDao().loadExpressAllCount();
    }

    public static Express loadExpressWithCode(String code) {
        return AppDatabase.getInstance().expressDao().loadExpressWithCode(ValueUtil.GlobalPhone, code);
    }

}
