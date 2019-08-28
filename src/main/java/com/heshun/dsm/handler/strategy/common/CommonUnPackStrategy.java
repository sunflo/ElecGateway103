package com.heshun.dsm.handler.strategy.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.heshun.dsm.entity.Device;
import com.heshun.dsm.entity.ResultWrapper;
import com.heshun.dsm.entity.convert.AbsJsonConvert;
import com.heshun.dsm.entity.global.DataBuffer;
import com.heshun.dsm.handler.anno.Transform;
import com.heshun.dsm.handler.anno.Transform.DataGroup;
import com.heshun.dsm.handler.anno.Transform.DataType;
import com.heshun.dsm.handler.helper.PacketInCorrectException;
import com.heshun.dsm.handler.helper.UnRegistSupervisorException;
import com.heshun.dsm.handler.strategy.AbsDeviceUnpackStrategy;
import com.heshun.dsm.util.ELog;
import com.heshun.dsm.util.SessionUtils;
import com.heshun.dsm.util.Utils;

//通用解包策略,通过Pack注解确定解包步骤
public abstract class CommonUnPackStrategy<K extends CommonElecPack>
        extends AbsDeviceUnpackStrategy<CommonElecConvert<K>, K> {

    public CommonUnPackStrategy(IoSession session, IoBuffer in, Device d) {
        super(session, in, d);
    }

    @Override
    protected K handleTotalQuery(int size, Map<Integer, ResultWrapper> ycData, Map<Integer, ResultWrapper> yxData,
                                 Map<Integer, ResultWrapper> ymData) throws PacketInCorrectException, UnRegistSupervisorException {
        K origin = fetchOrInitDeviceConvert().getOriginal();
        Field[] fields = origin.getClass().getFields();

        for (Field f : fields) {
            f.setAccessible(true);
            if (!f.isAnnotationPresent(Transform.class))
                continue;
            Transform ts = f.getAnnotation(Transform.class);

            DataGroup group = ts.group();
            int index = ts.index();
            if (index < 0)
                continue;
            ResultWrapper wrapper;
            switch (group) {

                case YX:
                    wrapper = yxData.get(index);
                    break;
                case YM:
                    wrapper = ymData.get(index);
                    break;
                default:
                    wrapper = ycData.get(index);
                    break;
            }

            if (wrapper == null)
                continue;

            setValue(wrapper, f, origin);
        }

        return origin;
    }


    private CommonElecConvert<K> fetchOrInitDeviceConvert() {
        Map<Integer, Map<Integer, AbsJsonConvert<?>>> buffer = DataBuffer.getInstance().getBuffer();
        int logotype = SessionUtils.getLogoType(session);
        buffer.computeIfAbsent(logotype, k -> new HashMap<>());
        AbsJsonConvert<?> __temp = buffer.get(logotype).get(mDevice.vCpu);

        if (__temp == null) {
            __temp = getConvert(getPack(mDevice.vCpu));

            buffer.get(logotype).put(mDevice.vCpu, __temp);
        }
        return (CommonElecConvert<K>) __temp;

    }

    private void setValue(ResultWrapper wrapper, Field f, K origin) {
        try {
            switch (getFiledType(f)) {
                case SHT:
                    f.setFloat(origin, getShortValue(wrapper.getOriginData()));
                    break;
                case INT:
                    f.setInt(origin, Utils.byte2Int(wrapper.getOriginData(), true));
                    break;
                case LNG:
                    f.setLong(origin, (long) (Utils.byte2Int(wrapper.getOriginData(), true)));
                    break;
                default:
                    f.setFloat(origin, Utils.byte2float(wrapper.getOriginData()));
                    break;

            }
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        } catch (IllegalAccessException e) {

            e.printStackTrace();
        }

    }

    @Override
    public CommonElecConvert<K> getConvert(K packet) {
        return new CommonElecConvert<K>(packet);
    }

    @Override
    public String getDeviceType() {
        return mDevice.model;
    }

    public abstract K getPack(int cpu);

    private DataType getFiledType(Field f) {
        String _type = f.getGenericType().toString();
        switch (_type) {
            case "integer":
                return DataType.INT;
            case "double":
                return DataType.DBLE;
            case "short":
                return DataType.SHT;
            case "long":
                return DataType.LNG;
            default:
                return DataType.FLT;
        }
    }

    private short getShortValue(byte[] _singleData) {
        byte high, low;
        high = _singleData[0];
        low = _singleData[1];

        return (short) (Utils.bytes2Short(high, low) & 0xFFFF);
    }
}
