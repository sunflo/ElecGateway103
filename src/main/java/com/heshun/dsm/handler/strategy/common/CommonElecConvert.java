package com.heshun.dsm.handler.strategy.common;

import java.lang.reflect.Field;

import com.alibaba.fastjson.JSONObject;
import com.heshun.dsm.entity.convert.AbsJsonConvert;
import com.heshun.dsm.entity.pack.DefaultDevicePacket;
import com.heshun.dsm.handler.anno.Transform;
import com.heshun.dsm.handler.anno.Transform.DataType;

public class CommonElecConvert<T extends DefaultDevicePacket> extends AbsJsonConvert<T> {

    public CommonElecConvert(T packet) {
        super(packet);
    }

    @Override
    public String getType() {
        return "eqa300";
    }

    @Override
    public JSONObject toJsonObj(String ip) {
        JSONObject jo = super.toJsonObj(ip);
        Field[] fs = mPacket.getClass().getFields();
        for (Field f : fs) {
            if (!f.isAnnotationPresent(Transform.class))
                continue;

            f.setAccessible(true);
            Transform tf = f.getAnnotation(Transform.class);

            String tag = tf.tag();

            jo.put(tag, getValue(f, tf));

        }
        return jo;
    }

    private Object getValue(Field f, Transform tf) {
        try {
            int ratio = tf.ratio();
            DataType type = getFiledType(f);
            Object origin;
            switch (type) {
                case SHT:
                    origin = f.getShort(mPacket);
                    break;
                case FLT:
                    origin = f.getFloat(mPacket);
                    break;
                case LNG:
                    origin = f.getLong(mPacket);
                    break;
                default:
                    origin = f.getInt(mPacket);
                    break;
            }

            return withRatio(origin, ratio);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private Object withRatio(Object o, int ratio) {

        if (ratio == 0 || ratio == 1)
            return o;
        if (o instanceof Short || o instanceof Integer)
            return ratio > 0 ? (int) (((float) o) * ratio) : (int) (((float) o) / ratio);
        else if (o instanceof Float)
            return ratio > 0 ? ((float) o) * ratio : ((float) o) / ratio;
        else if (o instanceof Double)
            return ratio > 0 ? ((double) o) * ratio : ((double) o / ratio);
        else if (o instanceof Long)

            return ratio > 0 ? (long) (Double.valueOf(o.toString()) * ratio) : (long) (Double.valueOf(o.toString()) / ratio);
        return o;

    }

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

}
