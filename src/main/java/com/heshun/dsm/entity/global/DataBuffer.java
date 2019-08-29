package com.heshun.dsm.entity.global;

import java.util.HashMap;
import java.util.Map;

import com.heshun.dsm.entity.convert.AbsJsonConvert;

public class DataBuffer {

    private static DataBuffer instance;

    // 数据缓冲
    // private Map<Integer, List<Object>> mBuffer;

    private Map<Integer, Map<Integer, AbsJsonConvert<?>>> mBuffer;

    private DataBuffer() {
        mBuffer = new HashMap<>();
    }

    public static DataBuffer getInstance() {
        synchronized (DataBuffer.class) {
            if (null == instance) {
                instance = new DataBuffer();
            }
            return instance;
        }
    }

    public synchronized Map<Integer, Map<Integer, AbsJsonConvert<?>>> getBuffer() {
        return mBuffer;
    }

    public void clearCacheByLogotype(int logoType) {
        if (mBuffer != null) {
            mBuffer.remove(logoType);
        }
    }

    public void clearAll() {
        mBuffer = null;
        System.gc();
    }

}
