package com.heshun.dsm.handler.strategy;

import java.nio.BufferUnderflowException;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.heshun.dsm.entity.Device;
import com.heshun.dsm.entity.ResultWrapper;
import com.heshun.dsm.entity.convert.AbsJsonConvert;
import com.heshun.dsm.entity.pack.DefaultDevicePacket;
import com.heshun.dsm.handler.helper.BufferTransferIncompleteException;
import com.heshun.dsm.handler.helper.IgnorePackageException;
import com.heshun.dsm.handler.helper.PacketInCorrectException;
import com.heshun.dsm.handler.helper.UnRegistSupervisorException;

/**
 * 设备传感器策略统一抽象类
 *
 * @author huangxz
 */
public abstract class AbsDeviceUnpackStrategy<T extends AbsJsonConvert<V>, V extends DefaultDevicePacket> extends
        Abs103Unpacker<T, V> {

    protected boolean dealChange = false;

    protected boolean dealActive = false;

    public AbsDeviceUnpackStrategy(IoSession session, IoBuffer in, Device d) {
        super(session, in, d);
    }

    protected final V unpack() throws BufferTransferIncompleteException, PacketInCorrectException,
            IgnorePackageException, UnRegistSupervisorException {
        try {
            int dataSize;
            byte[] _head = new byte[3];

            in.mark();
            // 获取头部，判断报文类型
            in.get(_head);
            in.reset();
            // 再次标记，断包情况下reset
            in.mark();
            byte msgType = _head[2];

            Map<Integer, ResultWrapper> ycData = new HashMap<>();
            Map<Integer, ResultWrapper> yxData = new HashMap<>();
            Map<Integer, ResultWrapper> ymData = new HashMap<>();
            byte[] buffer = new byte[8];
            in.get(buffer);
            //
            dataSize = buffer[7];
            int count = 0;

            for (; ; ) {
                if (++count > dataSize)
                    break;
                byte packType = in.get();
                byte index = in.get();
                // 偏移无用数据
                in.get();
                // 数据类型,已知0x07 float ;0x0c unsigned short
                byte dataType = in.get();
                byte dataLength = in.get();
                byte[] data = new byte[dataLength];
                in.get();
                in.get(data);
                switch (packType) {
                    case 0x07:
                        ycData.put((int) index, new ResultWrapper(dataType, data));
                        break;
                    case 0x08:
                        yxData.put((int) index, new ResultWrapper(dataType, data));
                        break;
                    case 0x0A:
                        ymData.put((int) index, new ResultWrapper(dataType, data));
                        break;
                    default:
                        break;
                }
            }
            switch (msgType) {
                case (byte) 0x09:
                    // 总查询
                    return handle(dataSize, ycData, yxData, ymData, PackageType.TOTAL_QUERY);
                case (byte) 0x01:
                    // 处理扰动数据
                    return handle(dataSize, ycData, yxData, ymData, PackageType.CHANGE_NOTIF);
                case (byte) 0x02:
                    // 处理主动上送数据
                    return handle(dataSize, ycData, yxData, ymData, PackageType.ACTIVE_NOTIF);
                case (byte) 0x28:
                    // 遥控结果，暂时忽略
                    return handle(dataSize, ycData, yxData, ymData, PackageType.REMOTE_CONTROL);
                default:
                    return null;
            }

        } catch (BufferUnderflowException e) {
            in.reset();
            throw new BufferTransferIncompleteException(e);
        } catch (Exception e) {
            if (e instanceof IgnorePackageException || e instanceof UnRegistSupervisorException)
                throw e;
            in.reset();
            throw new PacketInCorrectException(e);
        }
    }

    private V handle(int size, Map<Integer, ResultWrapper> ycData, Map<Integer, ResultWrapper> yxData,
                     Map<Integer, ResultWrapper> ymData, PackageType type) throws PacketInCorrectException,
            IgnorePackageException, UnRegistSupervisorException {
        switch (type) {
            default:
            case TOTAL_QUERY:
                return handleTotalQuery(size, ycData, yxData, ymData);
            case ACTIVE_NOTIF:
                if (dealActive) {
                    return handleActive(size, ycData, yxData, ymData);
                }
                throw new IgnorePackageException(type);
            case CHANGE_NOTIF:
                if (dealChange) {
                    return handleChange(size, ycData, yxData, ymData);
                }
                throw new IgnorePackageException(type);
            case REMOTE_CONTROL:
                throw new IgnorePackageException(type);
        }
    }

    /**
     * 处理总查询报文，默认必须实现
     *
     * @param size   >>>>数据总条数(遥测，遥信，遥脉总数)
     * @param ycData >>>>遥测数据列表
     * @param yxData >>>>遥信数据列表
     * @param ymData >>>>遥脉数据列表
     */
    protected abstract V handleTotalQuery(int size, Map<Integer, ResultWrapper> ycData,
                                          Map<Integer, ResultWrapper> yxData, Map<Integer, ResultWrapper> ymData) throws PacketInCorrectException,
            UnRegistSupervisorException;

    /**
     * 处理扰动
     */
    protected V handleChange(int size, Map<Integer, ResultWrapper> ycData, Map<Integer, ResultWrapper> yxData,
                             Map<Integer, ResultWrapper> ymData) throws IgnorePackageException, PacketInCorrectException {
        throw new IgnorePackageException(PackageType.CHANGE_NOTIF);
    }

    /**
     * 处理主动上送
     */
    protected V handleActive(int size, Map<Integer, ResultWrapper> ycData, Map<Integer, ResultWrapper> yxData,
                             Map<Integer, ResultWrapper> ymData) throws IgnorePackageException, PacketInCorrectException {

        throw new IgnorePackageException(PackageType.ACTIVE_NOTIF);
    }

}
