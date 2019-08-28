package com.heshun.dsm.handler.strategy.switchmodule.jingci;

import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.heshun.dsm.cmd.Command;
import com.heshun.dsm.entity.Device;
import com.heshun.dsm.entity.ResultWrapper;
import com.heshun.dsm.entity.convert.AbsJsonConvert;
import com.heshun.dsm.entity.global.DataBuffer;
import com.heshun.dsm.handler.helper.UnRegistSupervisorException;
import com.heshun.dsm.handler.strategy.AbsDeviceUnpackStrategy;

/**
 * 开关量模块
 *
 * @author huangxz
 */
public class SwitchModuleUnpStrategy4JCS extends
        AbsDeviceUnpackStrategy<SwitchModule_JCSConvert, SwitchModulePacket4JCS> {

    public SwitchModuleUnpStrategy4JCS(IoSession session, IoBuffer in, Device d) {
        super(session, in, d);
        dealChange = true;
    }

    @Override
    public String getDeviceType() {
        return "SwitchModule_JCS";
    }

    @Override
    public SwitchModule_JCSConvert getConvert(SwitchModulePacket4JCS packet) {
        return new SwitchModule_JCSConvert(packet);
    }

    @Override
    protected SwitchModulePacket4JCS handleChange(int size, Map<Integer, ResultWrapper> ycData,
                                                  Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) {
        AbsJsonConvert<?> c;

        if (DataBuffer.getInstance().getBuffer() == null
                || DataBuffer.getInstance().getBuffer().get(getLogotype()) == null
                || DataBuffer.getInstance().getBuffer().get(getLogotype()).get(mDevice.vCpu) == null) {
            c = new SwitchModule_JCSConvert(new SwitchModulePacket4JCS(mDevice.vCpu));
        } else {
            c = DataBuffer.getInstance().getBuffer().get(getLogotype()).get(mDevice.vCpu);
        }

        SwitchModule_JCSConvert orignal = (SwitchModule_JCSConvert) c;
        SwitchModulePacket4JCS packet = orignal.getOriginal();

        packet.notify = 1;
        try {
            packet.smoke1 = yxData.get(1).getOriginData()[0] != 1;
            if (packet.smoke1) {
                session.write(Command.getControlCommand(packet.address, 1, true));
            } else {
                session.write(Command.getControlCommand(packet.address, 1, false));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            packet.smoke2 = yxData.get(2).getOriginData()[0] != 1;
            if (packet.smoke2) {
                session.write(Command.getControlCommand(packet.address, 1, true));
            } else {
                session.write(Command.getControlCommand(packet.address, 1, false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return packet;
    }

    @Override
    protected SwitchModulePacket4JCS handleTotalQuery(int size, Map<Integer, ResultWrapper> ycData,
                                                      Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) throws
            UnRegistSupervisorException {
        SwitchModulePacket4JCS packet = new SwitchModulePacket4JCS(mDevice.vCpu);
        packet.smoke1 = yxData.get(1).getOriginData()[0] != 2;
        packet.smoke2 = yxData.get(2).getOriginData()[0] != 2;
        //

        return packet;
    }
}
