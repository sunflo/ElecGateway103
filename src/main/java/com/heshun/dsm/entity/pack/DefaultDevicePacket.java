package com.heshun.dsm.entity.pack;

import java.util.HashMap;

/**
 * 预留，提供公用属性
 *
 * @author huangxz
 */
public class DefaultDevicePacket extends HashMap<String, Object> {
    public int address = 0;

    public boolean notify = false;

    public DefaultDevicePacket(int address) {
        this.address = address;
    }
}
