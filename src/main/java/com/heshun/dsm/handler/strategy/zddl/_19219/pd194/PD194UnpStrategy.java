package com.heshun.dsm.handler.strategy.zddl._19219.pd194;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.heshun.dsm.entity.Device;
import com.heshun.dsm.entity.ResultWrapper;
import com.heshun.dsm.entity.convert.AbsJsonConvert;
import com.heshun.dsm.entity.global.DataBuffer;
import com.heshun.dsm.handler.helper.IgnorePackageException;
import com.heshun.dsm.handler.helper.PacketInCorrectException;
import com.heshun.dsm.handler.helper.UnRegistSupervisorException;
import com.heshun.dsm.handler.strategy.AbsDeviceUnpackStrategy;
import com.heshun.dsm.util.SessionUtils;
import com.heshun.dsm.util.Utils;

/**
 * 智瀚EQA300带无功，视在功率的解包策略
 * 
 * @author huangxz
 * 
 */
public class PD194UnpStrategy extends AbsDeviceUnpackStrategy<PD194Convert, PD194Packet> {
	Map<Integer, Method> methods_07;
	Map<Integer, Method> methods_0A;

	public PD194UnpStrategy(IoSession session, IoBuffer in, Device d) {

		super(session, in, d);
		dealActive = true;
		methods_07 = new HashMap<>();
		methods_0A = new HashMap<>();
		try {
			methods_07.put(1, PD194Packet.class.getMethod("set_ua", float.class));
			methods_07.put(2, PD194Packet.class.getMethod("set_ub", float.class));
			methods_07.put(3, PD194Packet.class.getMethod("set_uc", float.class));

			methods_07.put(4, PD194Packet.class.getMethod("set_ia", float.class));
			methods_07.put(5, PD194Packet.class.getMethod("set_ib", float.class));
			methods_07.put(6, PD194Packet.class.getMethod("set_ic", float.class));

			methods_07.put(7, PD194Packet.class.getMethod("set_ptotal", float.class));
			 
			methods_07.put(8, PD194Packet.class.getMethod("set_qtotal", float.class));
			 
			methods_07.put(9, PD194Packet.class.getMethod("set_pftotal", float.class));

			methods_07.put(10, PD194Packet.class.getMethod("set_freq", float.class));

			methods_0A.put(1, PD194Packet.class.getMethod("setEpi", long.class));
			methods_0A.put(2, PD194Packet.class.getMethod("setEql", long.class));

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDeviceType() {
		return "PD194";
	}

	@Override
	public PD194Convert getConvert(PD194Packet packet) {
		return new PD194Convert(packet);
	}

	@Override
	protected PD194Packet handleTotalQuery(int size, Map<Integer, ResultWrapper> ycData,
			Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) throws PacketInCorrectException,
			UnRegistSupervisorException {
		PD194Packet packet = fetchOrInitDeviceConvert().getOriginal();

		for (Entry<Integer, ResultWrapper> entry : ycData.entrySet()) {
			int index = entry.getKey();
			ResultWrapper result = ycData.get(index);
			if(result.illegal()) {
				break;
			}
			Method m = methods_07.get(index);
			if (m == null)
				continue;
			try {
				m.invoke(packet, Utils.byte2float(result.getOriginData()));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		for (Entry<Integer, ResultWrapper> entry : ymData.entrySet()) {
			int index = entry.getKey();
			ResultWrapper result = ymData.get(index);
			if(result.illegal()) {
				break;
			}
			Method m = methods_0A.get(index);
			if (m == null)
				continue;
			try {
				m.invoke(packet, (long) (Utils.byte2Int(result.getOriginData(), true)));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		return packet;
	}

	protected PD194Convert fetchOrInitDeviceConvert() {
		Map<Integer, Map<Integer, AbsJsonConvert<?>>> buffer = DataBuffer.getInstance().getBuffer();
		int logotype = SessionUtils.getLogoType(session);
		Map<Integer, AbsJsonConvert<?>> _temp = buffer.get(logotype);
		if (_temp == null) {
			buffer.put(logotype, new HashMap<Integer, AbsJsonConvert<?>>());
		}
		AbsJsonConvert<?> __temp = buffer.get(logotype).get(mDevice.vCpu);

		if (__temp == null) {
			__temp = new PD194Convert(new PD194Packet(mDevice.vCpu));

			buffer.get(logotype).put(mDevice.vCpu, __temp);
		}
		return (PD194Convert) __temp;

	}

	@Override
	protected PD194Packet handleActive(int size, Map<Integer, ResultWrapper> ycData,
			Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) throws IgnorePackageException,
			PacketInCorrectException {
		PD194Packet packet = fetchOrInitDeviceConvert().getOriginal();
		

		for (Entry<Integer, ResultWrapper> entry : ycData.entrySet()) {
			int index = entry.getKey();
			ResultWrapper result = ycData.get(index);
			if(result.illegal()) {
				break;
			}
			Method m = methods_07.get(index);
			if (m == null)
				continue;
			try {
				m.invoke(packet, Utils.byte2float(result.getOriginData()));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		for (Entry<Integer, ResultWrapper> entry : ymData.entrySet()) {
			int index = entry.getKey();
			ResultWrapper result = ymData.get(index);
			if(result.illegal()) {
				break;
			}
			Method m = methods_0A.get(index);
			if (m == null)
				continue;
			try {
				m.invoke(packet, (long) (Utils.byte2Int(result.getOriginData(), true)));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		return packet;
	}
}
