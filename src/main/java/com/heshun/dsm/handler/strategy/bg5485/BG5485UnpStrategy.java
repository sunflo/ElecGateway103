package com.heshun.dsm.handler.strategy.bg5485;

import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.heshun.dsm.entity.Device;
import com.heshun.dsm.entity.ResultWrapper;
import com.heshun.dsm.handler.helper.PacketInCorrectException;
import com.heshun.dsm.handler.helper.UnRegistSupervisorException;
import com.heshun.dsm.handler.strategy.AbsDeviceUnpackStrategy;
import com.heshun.dsm.util.Utils;

/**
 * 温湿度传感器解包策略
 * 
 * @author huangxz
 * 
 */
public class BG5485UnpStrategy extends AbsDeviceUnpackStrategy<BG5485Convert, BG5485Packet> {

	public BG5485UnpStrategy(IoSession session, IoBuffer in, Device d) {
		super(session, in, d);
	}

	@Override
	public String getDeviceType() {
		return "BG5485";
	}

	@Override
	public BG5485Convert getConvert(BG5485Packet packet) {
		return new BG5485Convert(packet);
	}

	@Override
	protected BG5485Packet handleTotalQuery(int size, Map<Integer, ResultWrapper> ycData,
			Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData)
			throws PacketInCorrectException, UnRegistSupervisorException {
		BG5485Packet packet = new BG5485Packet(mDevice.vCpu);

		if (ycData.get(1).getDataTyp() == 0x0C) {
			packet.wet = (short) (Utils.bytes2Short(ycData.get(1).getOriginData()) & 0xFFFF);
		} else {
			packet.wet = (short) (Utils.byte2float(ycData.get(1).getOriginData()));
		}

		if (ycData.get(2).getDataTyp() == 0x0C) {
			packet.temp = (short) (Utils.bytes2Short(ycData.get(2).getOriginData()) & 0xFFFF);
		} else {
			packet.temp = (short) (Utils.byte2float(ycData.get(2).getOriginData()));
		}
		return packet;
	}

}
