package com.heshun.dsm.handler;

import java.util.HashMap;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.heshun.dsm.entity.Device;
import com.heshun.dsm.handler.helper.BufferTransferIncompleteException;
import com.heshun.dsm.handler.helper.IgnorePackageException;
import com.heshun.dsm.handler.helper.PacketInCorrectException;
import com.heshun.dsm.handler.helper.UnRegistSupervisorException;
import com.heshun.dsm.handler.strategy.Abs103Unpacker;
import com.heshun.dsm.handler.strategy.UnpackerFactory;
import com.heshun.dsm.util.ELog;
import com.heshun.dsm.util.SessionUtils;

/**
 * 报文解析器
 * 
 * @author huangxz
 * 
 */
public class GateWayDecoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
		Abs103Unpacker<?, ?> unPacker = null;
		in.mark();
		// 获取头
		byte flag = in.get();

		in.reset();

		switch (flag) {
		// 扰动值
		case 0x01:
			break;
		// 标识报文
		case 0x05:
			// 创造一个虚拟的设备去解析标识报文
			unPacker = UnpackerFactory.fetchUnPacker(session, in, new Device("logotype", 1));
			break;
		// 总查询
		case 0x0A:
			in.mark();
			byte[] _head = new byte[8];
			// 0A 81 09 01 FE F1 04 80
			// 0A 81 01......
			// 0A 81 02......
			in.get(_head);
			byte cpu = _head[3];

			in.reset();
			HashMap<Integer, Device> _devices = SessionUtils.getDevices(session);
			Device currDevice = null;
			if (null != _devices) {
				currDevice = _devices.get(cpu & 0xff);
			}
			unPacker = UnpackerFactory.fetchUnPacker(session, in, currDevice);
			break;
		default:
			if (in.hasRemaining()) {
				in.position(in.limit());
				ELog.getInstance().err("报文异常，无效的报文头，丢弃掉所有未处理报文++++++++++++++++++", session);
			}
			return true;
		}

		try {
			assert unPacker != null;
			out.write(unPacker.doUnPack());
			return true;
		} catch (BufferTransferIncompleteException e) {
			ELog.getInstance().log("------------报文传输不完整，等待下个报文到达--------------", session);
			return false;
		} catch (PacketInCorrectException e) {
			// ELog.getInstance().log("------------设备读取异常，数据长度错误--------------",
			// session);
			if (in.hasRemaining()) {
				in.position(in.limit());
				ELog.getInstance().err("-------------" + e.getMessage(), session);
			}
			return true;
		} catch (IgnorePackageException e) {
			// ELog.getInstance().log(String.format("------------忽略此类报文的处理 [%s]--------------",
			// e.getMessage()), session);
			return true;
		} catch (UnRegistSupervisorException e) {
			ELog.getInstance().log("xxxxxxxxxxxxx未注册的管理机xxxxxxxxxxxxxxx", session);
			return true;
		}

	}
}
