package com.heshun.dsm.handler.strategy._trial;

import com.alibaba.fastjson.JSONObject;
import com.heshun.dsm.entity.driver.DeviceDriver;
import com.heshun.dsm.entity.driver.DriverItem;
import com.heshun.dsm.entity.pack.DefaultDevicePacket;

import java.util.Map;

public class GeneralMeterPack extends DefaultDevicePacket {


    GeneralMeterPack(int address, DeviceDriver driver) {
        super(address);

        for (Map.Entry<String, DriverItem> entry : driver.entrySet()) {
            // 预先占位
            put(entry.getKey(), 0);
        }
    }

    GeneralMeterPack(int address) {
        super(address);
    }

    public JSONObject parseJson() {
        return new JSONObject(this);
    }


}
