package com.miaxis.postal.data.dto;

import com.miaxis.postal.data.bean.Statistical;
import com.miaxis.postal.data.exception.MyException;


public class StatisticalDto implements Mapper<Statistical> {

    public String date;

    public int noCheckNumber;

    public String orgType;

    public int checkNumber;

    public int expressmanId;

    public int sendNumber;

    public String NAME;

    public StatisticalDto() {
    }

    @Override
    public Statistical transform() throws MyException {
        return new Statistical(
                this.date, this.noCheckNumber,
                this.orgType, this.checkNumber,
                this.expressmanId, this.sendNumber,
                this.NAME);
    }

    @Override
    public String toString() {
        return "StatisticalDto{" +
                "date='" + date + '\'' +
                ", noCheckNumber=" + noCheckNumber +
                ", orgType='" + orgType + '\'' +
                ", checkNumber=" + checkNumber +
                ", expressmanId=" + expressmanId +
                ", sendNumber=" + sendNumber +
                ", NAME='" + NAME + '\'' +
                '}';
    }

}
