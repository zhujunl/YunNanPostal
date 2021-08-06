package com.miaxis.postal.data.repository;

import android.text.TextUtils;

import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.data.entity.WarnLog;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.model.BranchModel;
import com.miaxis.postal.data.model.WarnLogModel;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.util.DateUtil;
import com.miaxis.postal.util.ValueUtil;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class WarnLogRepository extends BaseRepository {

    private WarnLogRepository() {
    }

    public static WarnLogRepository getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final WarnLogRepository instance = new WarnLogRepository();
    }

    /**
     * ================================ 静态内部类单例写法 ================================
     **/

    public Integer uploadWarnLog(WarnLog warnLog, TempId tempId) throws MyException, IOException, NetResultFailedException {
        Branch selected = BranchModel.findSelected();
        if (selected == null) {
            throw new MyException("未选择品牌");
        }
        String orgCode = selected.orgCode;
        String orgNode = selected.orgNode;
        Config config = ConfigManager.getInstance().getConfig();
        //Courier courier = DataCacheManager.getInstance().getCourier();

        Response<ResponseEntity<Integer>> execute = PostalApi.uploadWarnLog(
                orgCode,
                orgNode,
                tempId != null ? tempId.getPersonId() : "",
                tempId != null ? tempId.getCheckId() : "",
                warnLog.getSendAddress(),
                warnLog.getSendName(),
                warnLog.getSendCardNo(),
                warnLog.getSendPhone(),
                warnLog.getExpressmanId(),
                config.getDeviceIMEI(),
                warnLog.getExpressmanName(),
                DateUtil.DATE_FORMAT.format(warnLog.getCreateTime())).execute();
        try {
            ResponseEntity<Integer> body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    return body.getData();
                } else {
                    throw new NetResultFailedException("服务端返回，" + body.getMessage());
                }
            }
        } catch (NetResultFailedException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回，空数据");
    }

    public void saveWarnLog(WarnLog warnLog) {
        WarnLogModel.saveWarnLog(warnLog);
    }

    public void updateWarnLog(WarnLog warnLog) {
        WarnLogModel.saveWarnLog(warnLog);
    }

    public List<WarnLog> loadAll() {
        return WarnLogModel.loadAll();
    }

    public int loadWarnLogCount() {
        return WarnLogModel.loadWarnLogCount();
    }

    public WarnLog findOldestWarnLog() {
        return WarnLogModel.findOldestWarnLog();
    }

    public void deleteWarnLog(WarnLog warnLog) {
        WarnLogModel.deleteWarnLog(warnLog);
    }

}
