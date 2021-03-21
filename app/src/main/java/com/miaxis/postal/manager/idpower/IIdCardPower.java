package com.miaxis.postal.manager.idpower;

/**
 * IIdPower
 *
 * @author zhangyw
 * Created on 2021/3/21.
 */
public interface IIdCardPower {

    void powerOn();
    void powerOff();

    String ioPath();
}
