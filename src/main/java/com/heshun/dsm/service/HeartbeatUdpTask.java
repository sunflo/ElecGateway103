package com.heshun.dsm.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.heshun.dsm.cmd.Command;
import com.heshun.dsm.common.Constants;
import com.heshun.dsm.util.ELog;

/**
 * 心跳定时发送线程
 * 
 * @author huangxz
 *
 */
public class HeartbeatUdpTask {

	private DatagramSocket mSocket = null;
	private DatagramPacket mPacket = null;

	private Timer mTimer;

	public HeartbeatUdpTask() {
		try {
			mSocket = new DatagramSocket();
			mSocket.setBroadcast(true);
			byte[] bag = Command.getHeartBreakCommand();
			mPacket = new DatagramPacket(bag, bag.length, InetAddress.getByName(Constants.BROADCAST_ADDR),
					Constants.BROADCAST_PORT);

			mTimer = new Timer();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void start() {

		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					mSocket.send(mPacket);
					ELog.getInstance().log(String.format("发送广播  <%s>", new Date().toString()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 3000, Constants.BORADCAST_TIME_GAP);

	}

	public void stop() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
		}
	}
}
