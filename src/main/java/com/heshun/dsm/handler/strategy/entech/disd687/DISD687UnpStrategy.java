package com.heshun.dsm.handler.strategy.entech.disd687;

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
public class DISD687UnpStrategy extends AbsDeviceUnpackStrategy<DISD687Convert, DISD687Packet> {
	Map<Integer, Method> methods_07;
	Map<Integer, Method> methods_0A;

	public DISD687UnpStrategy(IoSession session, IoBuffer in, Device d) {

		super(session, in, d);
		dealActive = true;
		methods_07 = new HashMap<>();
		methods_0A = new HashMap<>();
		try {
			methods_07.put(1, DISD687Packet.class.getMethod("set_ua", float.class));
			methods_07.put(2, DISD687Packet.class.getMethod("set_ub", float.class));
			methods_07.put(3, DISD687Packet.class.getMethod("set_uc", float.class));

			methods_07.put(4, DISD687Packet.class.getMethod("set_uab", float.class));
			methods_07.put(5, DISD687Packet.class.getMethod("set_ubc", float.class));
			methods_07.put(6, DISD687Packet.class.getMethod("set_uca", float.class));

			methods_07.put(7, DISD687Packet.class.getMethod("set_ia", float.class));
			methods_07.put(8, DISD687Packet.class.getMethod("set_ib", float.class));
			methods_07.put(9, DISD687Packet.class.getMethod("set_ic", float.class));

			methods_07.put(10, DISD687Packet.class.getMethod("set_freq", float.class));

			methods_07.put(11, DISD687Packet.class.getMethod("set_pa", float.class));
			methods_07.put(12, DISD687Packet.class.getMethod("set_pb", float.class));
			methods_07.put(13, DISD687Packet.class.getMethod("set_pc", float.class));
			methods_07.put(14, DISD687Packet.class.getMethod("set_ptotal", float.class));

			methods_07.put(15, DISD687Packet.class.getMethod("set_qa", float.class));
			methods_07.put(16, DISD687Packet.class.getMethod("set_qb", float.class));
			methods_07.put(17, DISD687Packet.class.getMethod("set_qc", float.class));
			methods_07.put(18, DISD687Packet.class.getMethod("set_qtotal", float.class));

			methods_07.put(19, DISD687Packet.class.getMethod("set_pfa", float.class));
			methods_07.put(20, DISD687Packet.class.getMethod("set_pfb", float.class));
			methods_07.put(21, DISD687Packet.class.getMethod("set_pfc", float.class));
			methods_07.put(22, DISD687Packet.class.getMethod("set_pftotal", float.class));

			methods_0A.put(1, DISD687Packet.class.getMethod("setEpi", long.class));
			methods_0A.put(3, DISD687Packet.class.getMethod("setEql", long.class));

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDeviceType() {
		return "disd687";
	}

	@Override
	public DISD687Convert getConvert(DISD687Packet packet) {
		return new DISD687Convert(packet);
	}

	@Override
	protected DISD687Packet handleTotalQuery(int size, Map<Integer, ResultWrapper> ycData,
			Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) throws PacketInCorrectException,
			UnRegistSupervisorException {
		DISD687Packet packet = fetchOrInitDeviceConvert().getOriginal();

		 

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

	@Override
	protected DISD687Packet handleActive(int size, Map<Integer, ResultWrapper> ycData,
			Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) throws IgnorePackageException,
			PacketInCorrectException {
		DISD687Packet packet = fetchOrInitDeviceConvert().getOriginal();
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

	protected DISD687Convert fetchOrInitDeviceConvert() {
		Map<Integer, Map<Integer, AbsJsonConvert<?>>> buffer = DataBuffer.getInstance().getBuffer();
		int logotype = SessionUtils.getLogoType(session);
		Map<Integer, AbsJsonConvert<?>> _temp = buffer.get(logotype);
		if (_temp == null) {
			buffer.put(logotype, new HashMap<Integer, AbsJsonConvert<?>>());
		}
		AbsJsonConvert<?> __temp = buffer.get(logotype).get(mDevice.vCpu);

		if (__temp == null) {
			__temp = new DISD687Convert(new DISD687Packet(mDevice.vCpu));

			buffer.get(logotype).put(mDevice.vCpu, __temp);
		}
		return (DISD687Convert) __temp;

	}
}
