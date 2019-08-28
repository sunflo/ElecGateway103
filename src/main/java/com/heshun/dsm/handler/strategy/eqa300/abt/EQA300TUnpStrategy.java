package com.heshun.dsm.handler.strategy.eqa300.abt;

import com.heshun.dsm.entity.Device;
import com.heshun.dsm.entity.ResultWrapper;
import com.heshun.dsm.handler.helper.PacketInCorrectException;
import com.heshun.dsm.handler.helper.UnRegistSupervisorException;
import com.heshun.dsm.handler.strategy.AbsDeviceUnpackStrategy;
import com.heshun.dsm.util.Utils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 智瀚EQA300带无功，视在功率的解包策略
 *
 * @author huangxz
 */
public class EQA300TUnpStrategy extends AbsDeviceUnpackStrategy<EQA300TConvert, EQA300TPacket> {
    Method[] methods;

    public EQA300TUnpStrategy(IoSession session, IoBuffer in, Device d) {

        super(session, in, d);
        dealActive = true;
        methods = new Method[37];
        try {
            methods[0] = EQA300TPacket.class.getMethod("set_pid", short.class);
            methods[1] = EQA300TPacket.class.getMethod("set_port", short.class);
            methods[2] = EQA300TPacket.class.getMethod("set_wireType", short.class);
            methods[3] = EQA300TPacket.class.getMethod("set_version", short.class);
            methods[4] = EQA300TPacket.class.getMethod("set_dpt", short.class);
            methods[5] = EQA300TPacket.class.getMethod("set_dct", short.class);
            methods[6] = EQA300TPacket.class.getMethod("set_dqt", short.class);
            methods[7] = EQA300TPacket.class.getMethod("set_ua", short.class);
            methods[8] = EQA300TPacket.class.getMethod("set_ub", short.class);
            methods[9] = EQA300TPacket.class.getMethod("set_uc", short.class);
            methods[10] = EQA300TPacket.class.getMethod("set_uab", short.class);
            methods[11] = EQA300TPacket.class.getMethod("set_ubc", short.class);
            methods[12] = EQA300TPacket.class.getMethod("set_uca", short.class);
            methods[13] = EQA300TPacket.class.getMethod("set_ia", short.class);
            methods[14] = EQA300TPacket.class.getMethod("set_ib", short.class);
            methods[15] = EQA300TPacket.class.getMethod("set_ic", short.class);
            // ADD
            methods[16] = EQA300TPacket.class.getMethod("set_freq", short.class);
            //
            methods[17] = EQA300TPacket.class.getMethod("set_pa", short.class);
            methods[18] = EQA300TPacket.class.getMethod("set_pb", short.class);
            methods[19] = EQA300TPacket.class.getMethod("set_pc", short.class);
            methods[20] = EQA300TPacket.class.getMethod("set_ptotal", short.class);
            // ADD
            methods[21] = EQA300TPacket.class.getMethod("set_qa", short.class);
            methods[22] = EQA300TPacket.class.getMethod("set_qb", short.class);
            methods[23] = EQA300TPacket.class.getMethod("set_qc", short.class);
            methods[24] = EQA300TPacket.class.getMethod("set_qtotal", short.class);
            // ADD
            methods[25] = EQA300TPacket.class.getMethod("set_sa", short.class);
            methods[26] = EQA300TPacket.class.getMethod("set_sb", short.class);
            methods[27] = EQA300TPacket.class.getMethod("set_sc", short.class);
            methods[28] = EQA300TPacket.class.getMethod("set_stotal", short.class);

            methods[29] = EQA300TPacket.class.getMethod("set_pfa", short.class);
            methods[30] = EQA300TPacket.class.getMethod("set_pfb", short.class);
            methods[31] = EQA300TPacket.class.getMethod("set_pfc", short.class);
            methods[32] = EQA300TPacket.class.getMethod("set_pftotal", short.class);
            methods[33] = EQA300TPacket.class.getMethod("setEpiHigh", byte[].class);
            methods[34] = EQA300TPacket.class.getMethod("setEpiLow", byte[].class);
            // ADD
            methods[35] = EQA300TPacket.class.getMethod("set_eqlHigh", byte[].class);
            methods[36] = EQA300TPacket.class.getMethod("set_eqlLow", byte[].class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void setParam(int index, byte high, byte low, EQA300TPacket pack) {

        try {
            if (index == methods.length - 2 || index == methods.length - 1 || index == methods.length - 3
                    || index == methods.length - 4) {
                methods[index].invoke(pack, new byte[]{high, low});
                return;
            }
            methods[index].invoke(pack, (short) (Utils.bytes2Short(high, low) & 0xFFFF));
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getDeviceType() {
        return "eqa300t";
    }

    @Override
    public EQA300TConvert getConvert(EQA300TPacket packet) {
        return new EQA300TConvert(packet);
    }

    @Override
    protected EQA300TPacket handleTotalQuery(int size, Map<Integer, ResultWrapper> ycData,
                                             Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) throws PacketInCorrectException,
            UnRegistSupervisorException {
        EQA300TPacket packet = new EQA300TPacket(mDevice.vCpu);
        try {
            for (int i = 0; i < 37; i++) {
                byte[] _singleData = ycData.get(i + 1).getOriginData();
                byte high, low;
                high = _singleData[0];
                low = _singleData[1];

                setParam(i, high, low, packet);
            }
        } catch (NullPointerException e) {
            throw new PacketInCorrectException();
        }

        return packet;
    }

    @Override
    protected EQA300TPacket handleActive(int size, Map<Integer, ResultWrapper> ycData,
                                         Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) throws PacketInCorrectException {
        EQA300TPacket packet = new EQA300TPacket(mDevice.vCpu);
        try {
            for (int i = 0; i < 37; i++) {
                byte[] _singleData = ycData.get(i + 1).getOriginData();
                byte high, low;
                high = _singleData[0];
                low = _singleData[1];

                setParam(i, high, low, packet);
            }
        } catch (NullPointerException e) {
            // e.printStackTrace();
            throw new PacketInCorrectException();
        }

        return packet;
    }
}
