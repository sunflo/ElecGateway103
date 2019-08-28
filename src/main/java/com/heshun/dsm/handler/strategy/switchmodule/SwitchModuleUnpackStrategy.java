package com.heshun.dsm.handler.strategy.switchmodule;

import com.heshun.dsm.entity.Device;
import com.heshun.dsm.entity.ResultWrapper;
import com.heshun.dsm.entity.convert.AbsJsonConvert;
import com.heshun.dsm.entity.global.DataBuffer;
import com.heshun.dsm.handler.helper.UnRegistSupervisorException;
import com.heshun.dsm.handler.strategy.AbsDeviceUnpackStrategy;
import com.heshun.dsm.service.SystemHelper;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import java.util.Map;
import java.util.Map.Entry;

public class SwitchModuleUnpackStrategy extends AbsDeviceUnpackStrategy<SwitchModuleConvert, SwitchModulePacket> {
    private SwitchModuleProxy mProxy;

    public SwitchModuleUnpackStrategy(IoSession session, IoBuffer in, Device d) {
        super(session, in, d);
        mProxy = new SwitchModuleProxy();
        dealChange = true;
        SystemHelper.loadSwitchConfig(session, mDevice.vCpu);
    }

    @Override
    public SwitchModuleConvert getConvert(SwitchModulePacket packet) {
        return new SwitchModuleConvert(packet);
    }

    @Override
    protected SwitchModulePacket handleChange(int size, Map<Integer, ResultWrapper> ycData,
                                              Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) {
        AbsJsonConvert<?> c;

        if (DataBuffer.getInstance().getBuffer() == null
                || DataBuffer.getInstance().getBuffer().get(getLogotype()) == null
                || DataBuffer.getInstance().getBuffer().get(getLogotype()).get(mDevice.vCpu) == null) {
            c = new SwitchModuleConvert(new SwitchModulePacket(mDevice.vCpu));
        } else {
            c = DataBuffer.getInstance().getBuffer().get(getLogotype()).get(mDevice.vCpu);
        }

        SwitchModuleConvert original = (SwitchModuleConvert) c;
        SwitchModulePacket packet = original.getOriginal();
        packet.notify = 1;
        for (Entry<Integer, ResultWrapper> entry : yxData.entrySet()) {
            packet.mFlags.put(String.valueOf(entry.getKey()), entry.getValue().getOriginData()[0] != 1);
        }

        mProxy.handleControl(session, ycData, yxData, ymData);
        return packet;
    }

    @Override
    public String getDeviceType() {
        return "switch";
    }

    @Override
    protected SwitchModulePacket handleTotalQuery(int size, Map<Integer, ResultWrapper> ycData,
                                                  Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) {
        SwitchModulePacket packet = new SwitchModulePacket(mDevice.vCpu);
        for (Entry<Integer, ResultWrapper> entry : yxData.entrySet()) {
            packet.mFlags.put(String.valueOf(entry.getKey()), entry.getValue().getOriginData()[0] != 1);
        }
        return packet;
    }

}
