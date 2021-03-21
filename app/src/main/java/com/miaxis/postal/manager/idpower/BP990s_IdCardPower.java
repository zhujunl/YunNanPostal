package com.miaxis.postal.manager.idpower;

import android.os.SystemClock;
import android.serialport.DeviceControlSpd;

import java.io.IOException;

/**
 * BP990_IdCardPower
 *
 * @author zhangyw
 * Created on 2021/3/21.
 */
public class BP990s_IdCardPower implements IIdCardPower {
    private static final String SERIAL_PORT = "/dev/ttyMT1";

    private DeviceControlSpd deviceControl;

    public BP990s_IdCardPower() {
        try {
            deviceControl = new DeviceControlSpd(DeviceControlSpd.PowerType.MAIN, 93, 94);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void powerOn() {
        if (deviceControl != null) {
            try {
                deviceControl.PowerOnDevice();
                SystemClock.sleep(500);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void powerOff() {
        if (deviceControl != null) {
            try {
                deviceControl.PowerOffDevice();
                SystemClock.sleep(500);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String ioPath() {
        return SERIAL_PORT;
    }
}
