package com.heshun.dsm.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.session.IoSession;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.heshun.dsm.cmd.Command;
import com.heshun.dsm.common.Constants;
import com.heshun.dsm.entity.Device;
import com.heshun.dsm.util.ELog;
import com.heshun.dsm.util.SessionUtils;
import com.heshun.dsm.util.Utils;

/**
 * 定时主动对所有设备发起总查询
 * 
 * @author huangxz
 * 
 */
public class TotalQueryTask {

	private boolean notFirst = false;

	private Timer mTimer;

	private boolean isStart = false;

	// private QueryLoopFinishListener mTrigger;

	public TotalQueryTask() {
		mTimer = new Timer();
	}

	public void start() {
		if (!isStart) {
			ELog.getInstance().log("开始循环总查询");

			mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					ELog.getInstance().log(
							String.format("******************************开始时间%s***************************",
									Utils.getCurrentTime()));

					Map<Long, IoSession> sessionMap = SystemHelper.minaAcceptor.getManagedSessions();
					IoSession currSession = null;
					int currCpu = 0;
					for (Entry<Long, IoSession> entry : sessionMap.entrySet()) {
						currSession = entry.getValue();
						if (SessionUtils.isSessionIllegal(currSession)) {
							ELog.getInstance().err(String.format("异常连接，强制关闭:(%s<>)", currSession.getId()));

							SessionUtils.reset(currSession);
							currSession.closeNow();
							continue;
						}

						HashMap<Integer, Device> _devices = SessionUtils.getDevices(currSession);
						int _logoType = SessionUtils.getLogoType(currSession);

						for (Entry<Integer, Device> d : _devices.entrySet()) {
							try {
								Thread.sleep(Constants.COMMAND_TIME_GAP_IN_SESSION);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							currCpu = d.getKey();
							if (currSession.isActive() && currSession.isConnected()) {
								currSession.write(Command.getTotalQueryCommand(currCpu));
								ELog.getInstance().log(_logoType + " =======>>>总查询，端口：" + currCpu, _logoType);
							} else {
								ELog.getInstance().log(_logoType + "已经断开连接，忽略当前未发送报文", _logoType);
								SessionUtils.reset(currSession);
								currSession.closeNow();
								break;
							}

						}

					}

					ELog.getInstance().log(
							String.format("******************************周期结束时间%s***************************",
									Utils.getCurrentTime()));
					if (!notFirst) {
						// 开始发送线程
						startFeedBackLoop();
						notFirst = true;
					}
				}
			}, 500, Constants.REMOTE_SENSING_GAP);
			isStart = true;
		}
	}

	public void stop() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
		}
	}

	public interface QueryLoopFinishListener {
		void onFinishLoop();

	}

	public void startFeedBackLoop() {
		Scheduler scheduler;
		ELog.getInstance().log(
				String.format(">>>>>>>>>>>>>>>>>开始启动数据发送定时任务%s<<<<<<<<<<<<<<<<<", Utils.getCurrentTime()));
		Trigger t = TriggerBuilder.newTrigger().withIdentity("feed", "group1").startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(5).repeatForever()).build();

		JobDetail job = JobBuilder.newJob(DataFeedBackJob.class).withIdentity("job", "group1").build();
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.scheduleJob(job, t);

			// scheduler.start();
			scheduler.startDelayed(Constants.FEED_BACK_DELAY);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void flush() {
	}
}
