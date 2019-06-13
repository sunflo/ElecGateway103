package com.heshun.dsm.handler.strategy.common;

import com.heshun.dsm.entity.pack.DefaultDevicePacket;

/**
 * 	通用电表
 * 
 * @author huanxz
 *
 */
public abstract class CommonElecPack extends DefaultDevicePacket {

	public CommonElecPack(int address) {
		super(address);
	}
	
	public abstract String getDeviceType() ;
	

}
