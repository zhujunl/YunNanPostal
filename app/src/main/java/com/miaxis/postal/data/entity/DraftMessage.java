package com.miaxis.postal.data.entity;

import androidx.annotation.NonNull;

import java.util.List;

public class DraftMessage {

    @NonNull
    private IDCardRecord idCardRecord;
    @NonNull
    private List<Express> expressList;

    public DraftMessage(@NonNull IDCardRecord idCardRecord, @NonNull List<Express> expressList) {
        this.idCardRecord = idCardRecord;
        this.expressList = expressList;
    }

    @NonNull
    public IDCardRecord getIdCardRecord() {
        return idCardRecord;
    }

    public void setIdCardRecord(@NonNull IDCardRecord idCardRecord) {
        this.idCardRecord = idCardRecord;
    }

    @NonNull
    public List<Express> getExpressList() {
        return expressList;
    }

    public void setExpressList(@NonNull List<Express> expressList) {
        this.expressList = expressList;
    }
}
