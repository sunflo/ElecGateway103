package com.heshun.dsm.common.global;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.heshun.dsm.entity.Device;

/**
 * 设备关系缓存类,系统开启时从 config初始化
 * 
 * @author huangxz
 *
 */
@Deprecated
public class DeviceCachManager {
	private static DeviceCachManager instance;

	/**
	 * Map<T, HashMap<K, V>> | T,K,V分别代表ip，cpu，设备实体
	 */
	private Map<String, HashMap<Integer, List<Device>>> mDevices;

	private DeviceCachManager() {
		mDevices = new HashMap<String, HashMap<Integer, List<Device>>>();
	}

	public static DeviceCachManager getInstance() {
		synchronized (DeviceCachManager.class) {
			if (null == instance) {
				instance = new DeviceCachManager();
			}
		}
		return instance;
	}

	public void initDevice(Map<String, HashMap<Integer, List<Device>>> device) {
		this.mDevices = device;
	}

	public Map<String, HashMap<Integer, List<Device>>> getDevices() {
		return mDevices;
	}

}
