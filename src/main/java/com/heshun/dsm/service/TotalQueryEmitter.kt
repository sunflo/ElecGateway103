package com.heshun.dsm.service

import com.heshun.dsm.cmd.Command
import com.heshun.dsm.common.Constants
import com.heshun.dsm.ui.ControlPanel
import com.heshun.dsm.util.ELog
import com.heshun.dsm.util.SessionUtils
import com.heshun.dsm.util.Utils
import org.apache.mina.core.session.IoSession
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import java.util.*
import com.heshun.dsm.common.Constants.COMMAND_TIME_GAP_IN_SESSION as timeGap

class TotalQueryEmitter(private val mProgressListener: ControlPanel.OnStatusChangeListener) {

    private var notFirst = false

    private val mTimer = Timer()

    private var isStart = false

    fun start() {
        if (isStart) {
            ELog.getInstance().log("查询任务已经开始。。。。。")
            return
        }
        mTimer.schedule(object : TimerTask() {
            override fun run() {
                while (true) {
                    ELog.getInstance().log("[ 周期开始时间:${Utils.getCurrentTime()} ]")

                    isStart = true
                    flatRequestPackQueue().apply {
                        forEachIndexed { index, it ->
                            it.emit()
                            mProgressListener.onProgressChange(index + 1, size)
                        }
                    }
                    if (!notFirst) {
                        // 开始发送线程
                        startFeedBackLoop()
                        notFirst = true
                    }
                    ELog.getInstance().log("[ 周期结束:${Utils.getCurrentTime()},等待${Constants.REMOTE_SENSING_GAP} ms后开始下一周期 ]")

                    try {
                        Thread.sleep(Constants.REMOTE_SENSING_GAP.toLong())
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
            }
        }, 1500L)
    }

    private fun flatRequestPackQueue(): List<CommandPack> {
        var maxSize = 0
        return with(mutableListOf<List<CommandPack>>().apply {
            SystemHelper.minaAcceptor.managedSessions?.forEach {
                it.value?.let { session ->
                    SessionUtils.getDevices(session)?.keys?.let { ids ->
                        //取设备最多的session的deviceSize作为第二次循环的times
                        maxSize = if (ids.size > maxSize) ids.size else maxSize
                        this.add(ids.map { currId ->
                            CommandPack(session, Command.getTotalQueryCommand(currId))
                        })
                    }
                }
            }
        }) {
            mutableListOf<CommandPack>().also { result ->
                for (i in 0 until maxSize) {
                    this.forEach {
                        if (it.size > i) {
                            result.add(it[i])
                        }
                    }
                }
            }
        }
    }

    private fun startFeedBackLoop() {
        StdSchedulerFactory.getDefaultScheduler()?.apply {
            ELog.getInstance().log("[ 开始启动数据发送定时任务,延迟${Constants.FEED_BACK_DELAY} 秒 ]")
            val feedbackJob = JobBuilder.newJob(DataFeedBackJob::class.java)
                    .withIdentity("job", "group1")
                    .build()
            val trigger = TriggerBuilder.newTrigger()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInMinutes(5)
                            .repeatForever())
                    .withIdentity("feed", "group1")
                    .startNow()
                    .build()
            scheduleJob(feedbackJob, trigger)
        }?.startDelayed(Constants.FEED_BACK_DELAY)
    }


    internal inner class CommandPack(val session: IoSession, private val command: ByteArray) {
        fun emit() {
            val lastTime = SessionUtils.getLastWriteTime(session)
            val currTime = System.currentTimeMillis()
            // 距离上次发送间隔少于安全时间间隔
            if (currTime - lastTime < timeGap) {
                val safeTime = timeGap - (currTime - lastTime)
                ELog.getInstance().log(String.format("发送频率超过安全频率，等待[%s] ms", safeTime), session)
                try {
                    Thread.sleep(safeTime)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            ELog.getInstance().log("发送报文：" + Arrays.toString(command), session)
            session.write(command)
            SessionUtils.setLastWriteTime(session)
        }
    }
}