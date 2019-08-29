package com.heshun.dsm.handler.strategy._trial;

import com.heshun.dsm.entity.Device;
import com.heshun.dsm.entity.ResultWrapper;
import com.heshun.dsm.entity.driver.DeviceDriverLoader;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import java.util.Map;

public class FujiGeneralMeterUnpackStrategy extends GeneralMeterUnpackStrategy {

    public FujiGeneralMeterUnpackStrategy(IoSession session, IoBuffer in, Device d) {
        super(session, in, d);
        mDriver = DeviceDriverLoader.load(d.model);
        dealChange = true;
    }

    @Override
    protected GeneralMeterPack handleChange(int size, Map<Integer, ResultWrapper> ycData, Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) {
        GeneralMeterConvert c = fetchOrInitDeviceConvert();
        GeneralMeterPack update = new GeneralMeterPack(c.getOriginal().address);
        update.notify = 2;
        if (yxData != null && yxData.size() > 0) {
            for (Map.Entry<Integer, ResultWrapper> entry : yxData.entrySet()) {
                byte _v = entry.getValue().getOriginData()[0];

                c.getOriginal().put(entry.getKey().toString(), _v >= 1 ? _v - 1 : 0);
                update.put(entry.getKey().toString(), _v >= 1 ? _v - 1 : 0);
            }
        }
        return update;
    }
}