package com.miaxis.postal.data.dto;

import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.data.exception.MyException;

public class TempIdDto implements Mapper<TempId> {

    private String personId;
    private String checkId;

    public TempIdDto() {
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getCheckId() {
        return checkId;
    }

    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }

    @Override
    public TempId transform() throws MyException {
        try {
            return TempId.TempIdBuilder.aTempId()
                    .personId(personId)
                    .checkId(checkId)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException("解析临时ID信息失败，原因：" + e.getMessage());
        }
    }
}
