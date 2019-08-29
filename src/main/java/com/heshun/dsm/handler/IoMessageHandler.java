package com.heshun.dsm.handler;

import java.util.HashMap;

import com.heshun.dsm.service.TotalQueryEmitter;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.heshun.dsm.cmd.Command;
import com.heshun.dsm.entity.Device;
import com.heshun.dsm.entity.convert.AbsJsonConvert;
import com.heshun.dsm.handler.strategy.def.DefaultUnpStrategy.DefaultConvert;
import com.heshun.dsm.handler.strategy.logotype.LogoTypeConvert;
import com.heshun.dsm.handler.transfer.BatchTransferHandlerRefactor;
import com.heshun.dsm.handler.transfer.ItfTransferHandler;
import com.heshun.dsm.service.SystemHelper;
import com.heshun.dsm.ui.ControlPanel.OnStatusChangeListener;
import com.heshun.dsm.util.ELog;
import com.heshun.dsm.util.SessionUtils;

/**
 * 事件处理handler
 *
 * @author huangxz
 */
public class IoMessageHandler extends IoHandlerAdapter {

    private boolean isFirstConnect = true;

    private OnStatusChangeListener mListener;

    private ItfTransferHandler mTransHandler;

    public IoMessageHandler(OnStatusChangeListener listener) {
        this.mListener = listener;
        mTransHandler = new BatchTransferHandlerRefactor(mListener);
        // mTransHandler = new BatchTransferHandler(mListener);

    }

    @Override
    public synchronized void messageReceived(IoSession session, Object message) throws Exception {
        if (!(message instanceof AbsJsonConvert))
            return;
        if (message instanceof LogoTypeConvert) {
            LogoTypeConvert pack = (LogoTypeConvert) message;
            // 绑定logotype
            SessionUtils.setLogoType(pack.logotype, session);
            if (SessionUtils.getDevices(session) == null) {

                HashMap<Integer, Device> deviceMap = SystemHelper.loadDevicesByLogoType(pack.logotype);
                if (null == deviceMap) {
                    ELog.getInstance().err(
                            String.format("未找到配置文件，强制关闭:([%s]  %s : %s)", pack.logotype,
                                    SessionUtils.getIpFromSession(session), SessionUtils.getPortFromSession(session)));
                    session.closeNow();
                } else
                    session.setAttribute("devices", deviceMap);
                ELog.getInstance().log(String.format("sessionId:%s && logotype:%s", session.getId(), pack.logotype));
            }

            if (isFirstConnect) {
                if (mTransHandler instanceof BatchTransferHandlerRefactor) {
                    SystemHelper.initTotalQuery(mListener);

                }
                isFirstConnect = false;
            }
        } else if (message instanceof DefaultConvert) {
            // do nothing
        } else
            mTransHandler.transfer(session, (AbsJsonConvert<?>) message);

    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        if (mListener != null) {
            mListener.onConnectChange();
        }
        super.sessionClosed(session);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        // ELog.getInstance().log("sessionCreated", session);
        super.sessionCreated(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        session.write(Command.getLogoTypeCommand());

        if (mListener != null) {
            mListener.onConnectChange();
        }
        super.sessionIdle(session, status);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        // ELog.getInstance().log(
        // "sessionOpened:(" + session.getId() + ")" +
        // Utils.getIpFromSession(session) + "--->"
        // + Utils.getPortFromSession(session));

        session.write(Command.getLogoTypeCommand());

        if (mListener != null) {
            mListener.onConnectChange();
        }

        super.sessionOpened(session);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        ELog.getInstance().log("exception:".concat(cause.toString()), session);
        super.exceptionCaught(session, cause);
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        super.inputClosed(session);
    }
}
