package com.heshun.dsm.handler.strategy._trial;

import com.heshun.dsm.entity.Device;
import com.heshun.dsm.entity.ResultWrapper;
import com.heshun.dsm.entity.convert.AbsJsonConvert;
import com.heshun.dsm.entity.driver.DeviceDriver;
import com.heshun.dsm.entity.driver.DeviceDriverLoader;
import com.heshun.dsm.entity.driver.DriverItem;
import com.heshun.dsm.entity.global.DataBuffer;
import com.heshun.dsm.handler.helper.PacketInCorrectException;
import com.heshun.dsm.handler.helper.UnRegistSupervisorException;
import com.heshun.dsm.handler.strategy.AbsDeviceUnpackStrategy;
import com.heshun.dsm.util.SessionUtils;
import com.heshun.dsm.util.Utils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import java.util.HashMap;
import java.util.Map;

public class GeneralMeterUnpackStrategy extends AbsDeviceUnpackStrategy<GeneralMeterConvert, GeneralMeterPack> {
    protected DeviceDriver mDriver;

    public GeneralMeterUnpackStrategy(IoSession session, IoBuffer in, Device d) {
        super(session, in, d);
        mDriver = DeviceDriverLoader.load(d.model);
        dealActive = true;
    }

    @Override
    protected GeneralMeterPack handleTotalQuery(int size, Map<Integer, ResultWrapper> ycData, Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) throws PacketInCorrectException, UnRegistSupervisorException {
        return actionHandle(size, ycData, yxData, ymData);

    }

    private GeneralMeterPack actionHandle(int size, Map<Integer, ResultWrapper> ycData, Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) {
        GeneralMeterConvert cvt = fetchOrInitDeviceConvert();
        GeneralMeterPack p = cvt.getOriginal();

        for (Map.Entry<String, Object> entry : p.entrySet()) {
            String key = entry.getKey();
            String[] s = key.split("-");
            String group = s[0];
            int index = Integer.valueOf(s[1]);
            DriverItem policy = mDriver.get(key);

            Map<Integer, ResultWrapper> from = null;
            switch (group) {
                case "7":
                    from = ycData;
                    break;
                case "8":
                    from = yxData;
                    break;
                case "10":
                    from = ymData;
                    break;
            }

            if (from != null) {
                ResultWrapper rw = from.get(index);
                if (rw == null)
                    continue;
                if (rw.getDataTyp() == 0x07) {
                    p.put(key, Utils.byte2float(rw.getOriginData(), policy.isReverse()));
                } else if (rw.getDataTyp() == 0x09) {
                    p.put(key, (int) rw.getOriginData()[0]);
                } else {
                    p.put(key, (long) (Utils.byte2Int(rw.getOriginData(), !policy.isReverse())));
                }
            }

        }
        return p;
    }

    @Override
    protected GeneralMeterPack handleActive(int size, Map<Integer, ResultWrapper> ycData, Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) {
        return actionHandle(size, ycData, yxData, ymData);
    }


    @Override
    public GeneralMeterConvert getConvert(GeneralMeterPack packet) {
        return new GeneralMeterConvert(packet, mDriver);
    }

    @Override
    public String getDeviceType() {
        return mDriver.getName();
    }

    protected GeneralMeterConvert fetchOrInitDeviceConvert() {
        Map<Integer, Map<Integer, AbsJsonConvert<?>>> buffer = DataBuffer.getInstance().getBuffer();
        int logotype = SessionUtils.getLogoType(session);
        buffer.computeIfAbsent(logotype, k -> new HashMap<>());
        GeneralMeterConvert result = (GeneralMeterConvert) buffer.get(logotype).get(mDevice.vCpu);

        if (result == null) {
            result = getConvert(new GeneralMeterPack(mDevice.vCpu, mDriver));
            buffer.get(logotype).put(mDevice.vCpu, result);
        }
        return result;

    }
}
