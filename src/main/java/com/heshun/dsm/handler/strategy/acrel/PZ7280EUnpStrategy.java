package com.heshun.dsm.handler.strategy.acrel;

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
 * 
 * 安科瑞PZ42E
 * @author keda
 *
 */
public class PZ7280EUnpStrategy extends AbsDeviceUnpackStrategy<PZ7280EConvert, PZ7280EPacket> {
	Map<Integer, Method> methods_07;
	Map<Integer, Method> methods_0A;

	public PZ7280EUnpStrategy(IoSession session, IoBuffer in, Device d) {

		super(session, in, d);
		dealActive = true;
		methods_07 = new HashMap<>();
		methods_0A = new HashMap<>();
		try {
			methods_07.put(1, PZ7280EPacket.class.getMethod("set_ua", float.class));
			methods_07.put(2, PZ7280EPacket.class.getMethod("set_ub", float.class));
			methods_07.put(3, PZ7280EPacket.class.getMethod("set_uc", float.class));

			methods_07.put(4, PZ7280EPacket.class.getMethod("set_uab", float.class));
			methods_07.put(5, PZ7280EPacket.class.getMethod("set_ubc", float.class));
			methods_07.put(6, PZ7280EPacket.class.getMethod("set_uca", float.class));

			methods_07.put(7, PZ7280EPacket.class.getMethod("set_ia", float.class));
			methods_07.put(8, PZ7280EPacket.class.getMethod("set_ib", float.class));
			methods_07.put(9, PZ7280EPacket.class.getMethod("set_ic", float.class));

			methods_07.put(10, PZ7280EPacket.class.getMethod("set_pa", float.class));
			methods_07.put(11, PZ7280EPacket.class.getMethod("set_pb", float.class));
			methods_07.put(12, PZ7280EPacket.class.getMethod("set_pc", float.class));
			methods_07.put(13, PZ7280EPacket.class.getMethod("set_ptotal", float.class));

			methods_07.put(14, PZ7280EPacket.class.getMethod("set_qa", float.class));
			methods_07.put(15, PZ7280EPacket.class.getMethod("set_qb", float.class));
			methods_07.put(16, PZ7280EPacket.class.getMethod("set_qc", float.class));
			methods_07.put(17, PZ7280EPacket.class.getMethod("set_qtotal", float.class));

			methods_07.put(18, PZ7280EPacket.class.getMethod("set_pfa", float.class));
			methods_07.put(19, PZ7280EPacket.class.getMethod("set_pfb", float.class));
			methods_07.put(20, PZ7280EPacket.class.getMethod("set_pfc", float.class));
			methods_07.put(21, PZ7280EPacket.class.getMethod("set_pftotal", float.class));
			
			methods_07.put(22, PZ7280EPacket.class.getMethod("set_sa", float.class));
			methods_07.put(23, PZ7280EPacket.class.getMethod("set_sb", float.class));
			methods_07.put(24, PZ7280EPacket.class.getMethod("set_sc", float.class));
			methods_07.put(25, PZ7280EPacket.class.getMethod("set_stotal", float.class));

			methods_07.put(26, PZ7280EPacket.class.getMethod("set_freq", float.class));

			methods_0A.put(1, PZ7280EPacket.class.getMethod("setEpi", long.class));
			methods_0A.put(3, PZ7280EPacket.class.getMethod("setEql", long.class));

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDeviceType() {
		return "PZ7280E";
	}

	@Override
	public PZ7280EConvert getConvert(PZ7280EPacket packet) {
		return new PZ7280EConvert(packet);
	}

	@Override
	protected PZ7280EPacket handleTotalQuery(int size, Map<Integer, ResultWrapper> ycData,
			Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData)
			throws PacketInCorrectException, UnRegistSupervisorException {
		PZ7280EPacket packet = fetchOrInitDeviceConvert().getOriginal();

		for (Entry<Integer, ResultWrapper> entry : ycData.entrySet()) {
			int index = entry.getKey();
			ResultWrapper result = ycData.get(index);
			if (result.illegal()) {
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
			if (result.illegal()) {
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

	protected PZ7280EConvert fetchOrInitDeviceConvert() {
		Map<Integer, Map<Integer, AbsJsonConvert<?>>> buffer = DataBuffer.getInstance().getBuffer();
		int logotype = SessionUtils.getLogoType(session);
		Map<Integer, AbsJsonConvert<?>> _temp = buffer.get(logotype);
		if (_temp == null) {
			buffer.put(logotype, new HashMap<Integer, AbsJsonConvert<?>>());
		}
		AbsJsonConvert<?> __temp = buffer.get(logotype).get(mDevice.vCpu);

		if (__temp == null) {
			__temp = new PZ7280EConvert(new PZ7280EPacket(mDevice.vCpu));

			buffer.get(logotype).put(mDevice.vCpu, __temp);
		}
		return (PZ7280EConvert) __temp;

	}

	@Override
	protected PZ7280EPacket handleActive(int size, Map<Integer, ResultWrapper> ycData, Map<Integer, ResultWrapper> yxData,
			Map<Integer, ResultWrapper> ymData) throws IgnorePackageException, PacketInCorrectException {
		PZ7280EPacket packet = fetchOrInitDeviceConvert().getOriginal();

		for (Entry<Integer, ResultWrapper> entry : ycData.entrySet()) {
			int index = entry.getKey();
			ResultWrapper result = ycData.get(index);
			if (result.illegal()) {
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
			if (result.illegal()) {
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
