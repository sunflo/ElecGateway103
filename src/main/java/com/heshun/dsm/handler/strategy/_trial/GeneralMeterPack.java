package com.heshun.dsm.handler.strategy._trial;

import com.heshun.dsm.entity.driver.DeviceDriver;
import com.heshun.dsm.entity.driver.DriverItem;
import com.heshun.dsm.entity.pack.DefaultDevicePacket;

import java.util.Map;

public class GeneralMeterPack extends DefaultDevicePacket {
//    private Map<String, Object> mDataCache;

    GeneralMeterPack(int address, DeviceDriver driver) {
        super(address);

        for (Map.Entry<String, DriverItem> entry : driver.entrySet()) {
            // 预先占位
            put(entry.getKey(), 0);
        }
    }

//    public Map<String, Object> getmDataCache() {
//        return mDataCache;
//    }


}
