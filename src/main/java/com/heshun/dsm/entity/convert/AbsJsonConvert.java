package com.heshun.dsm.entity.convert;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.heshun.dsm.entity.pack.DefaultDevicePacket;
import com.heshun.dsm.util.Utils;

public abstract class AbsJsonConvert<T extends DefaultDevicePacket> {

    private JSONObject mJson = new JSONObject();

    protected T mPacket;

    /**
     * 此变量用于区别是否立即传送扰动值
     */
    public int notify = 0;

    public String timeStamp;

    public String gatherTime;

    public AbsJsonConvert(T packet) {

        this.mPacket = packet;
        this.notify = packet.notify;
        gatherTime = Utils.getCurrentTime();

    }

    public JSONObject toJsonObj(String ip) {
        mJson.put("ip", ip);
        mJson.put("type", getType());
        mJson.put("timeStamp", getTimeStamp());
        mJson.put("gatherTime", gatherTime);
        mJson.put("address", mPacket.address);
        return mJson;
    }

    protected String getTimeStamp() {
        long curr = System.currentTimeMillis();
        long fiveM = 5 * 60 * 1000;
        long lastT = curr / fiveM * fiveM;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (notify == 0)
            return sdf.format(new Date(lastT));
        else
            return sdf.format(new Date(curr));
    }

    public abstract String getType();

    public T getOriginal() {
        return mPacket;
    }

}
