package com.miaxis.postal.data.bean;

/**
 * @author Tank
 * @date 2021/9/22 1:37 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class Statistical {

    public String date;

    public int noCheckNumber;

    public String orgType;

    public int checkNumber;

    public int expressmanId;

    public int sendNumber;

    public String NAME;

    public Statistical() {
    }

    public Statistical(String date, int noCheckNumber, String orgType, int checkNumber, int expressmanId, int sendNumber, String NAME) {
        this.date = date;
        this.noCheckNumber = noCheckNumber;
        this.orgType = orgType;
        this.checkNumber = checkNumber;
        this.expressmanId = expressmanId;
        this.sendNumber = sendNumber;
        this.NAME = NAME;
    }

    @Override
    public String toString() {
        return "Statistical{" +
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
