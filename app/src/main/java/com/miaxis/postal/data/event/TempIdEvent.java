package com.miaxis.postal.data.event;

import com.miaxis.postal.data.dto.TempIdDto;

public class TempIdEvent {

    private TempIdDto tempIdDto;

    public TempIdEvent(TempIdDto tempIdDto) {
        this.tempIdDto = tempIdDto;
    }

    public TempIdDto getTempIdDto() {
        return tempIdDto;
    }

    public void setTempIdDto(TempIdDto tempIdDto) {
        this.tempIdDto = tempIdDto;
    }
}
