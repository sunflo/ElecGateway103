package com.heshun.dsm.handler.strategy._trial;

import com.alibaba.fastjson.JSONObject;
import com.heshun.dsm.entity.convert.AbsJsonConvert;
import com.heshun.dsm.entity.driver.DeviceDriver;
import com.heshun.dsm.entity.driver.DriverItem;

import java.util.Map;

public class GeneralMeterConvert extends AbsJsonConvert<GeneralMeterPack> {
    private DeviceDriver mDriver;

    GeneralMeterConvert(GeneralMeterPack packet, DeviceDriver d) {
        super(packet);
        this.mDriver = d;
    }

    @Override
    public String getType() {
        return mDriver.getMask();
    }

    @Override
    public JSONObject toJsonObj(String ip) {
        JSONObject jsonObject = super.toJsonObj(ip);
        Map<String, Object> quotas = mPacket.getmDataCache();
        for (Map.Entry<String, Object> entry : quotas.entrySet()) {
            String key = entry.getKey();
            DriverItem policy = mDriver.get(key);
            jsonObject.put(policy.getmTag(), withRatio(entry.getValue(), policy.getmRatio()));
        }
        return jsonObject;
    }


    private Object withRatio(Object o, int ratio) {

        if (ratio == 0 || ratio == 1)
            return o;
        if (o instanceof Short || o instanceof Integer)
            return ratio > 0 ? (int) (((float) o) * ratio) : (int) (((float) o) / ratio);
        else if (o instanceof Float)
            return ratio > 0 ? ((float) o) * ratio : ((float) o) / ratio;
        else if (o instanceof Double)
            return ratio > 0 ? ((double) o) * ratio : ((double) o) / ratio;
        else if (o instanceof Long)
            return ratio > 0 ? (long) (Double.valueOf(o.toString()) * ratio) : (long) (Double.valueOf(o.toString()) / ratio);
        return o;

    }
}
