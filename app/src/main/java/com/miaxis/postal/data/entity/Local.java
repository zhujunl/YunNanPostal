package com.miaxis.postal.data.entity;

public class Local {

    private Express express;
    private IDCardRecord idCardRecord;

    public Local() {
    }

    public Local(Express express, IDCardRecord idCardRecord) {
        this.express = express;
        this.idCardRecord = idCardRecord;
    }

    public Express getExpress() {
        return express;
    }

    public void setExpress(Express express) {
        this.express = express;
    }

    public IDCardRecord getIdCardRecord() {
        return idCardRecord;
    }

    public void setIdCardRecord(IDCardRecord idCardRecord) {
        this.idCardRecord = idCardRecord;
    }
}
