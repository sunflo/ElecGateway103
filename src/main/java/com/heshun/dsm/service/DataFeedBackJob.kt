package com.heshun.dsm.service

import com.alibaba.fastjson.JSONObject
import com.heshun.dsm.common.Constants
import com.heshun.dsm.common.http.HttpUtils
import com.heshun.dsm.entity.convert.AbsJsonConvert
import com.heshun.dsm.entity.global.DataBuffer
import com.heshun.dsm.util.Utils
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.util.*
import java.util.concurrent.TimeUnit
import com.heshun.dsm.util.ELog.getInstance as g


class DataFeedBackJob : Job {
    private val mHeterogeneous = arrayOf(10047, 10051, 10052)
    override fun execute(context: JobExecutionContext) {
        try {
            g().log("[开始发送本周期数据${Utils.getCurrentTime()}]")
            HashMap<Int, Map<Int, AbsJsonConvert<*>>>()
                    .apply {
                        putAll(DataBuffer.getInstance().buffer)
                    }
                    .forEach { logotype, convert ->
                        SystemHelper.mHttpRequestThreadPool.schedule({
                            JSONObject().let {
                                it["ip"] = logotype
                                it["isAlarm"] = false
                                it["data"] = mutableListOf<Any>().apply {
                                    convert.forEach { _, item ->
                                        add(item.toJsonObj(logotype.toString()))
                                    }
                                }
                                HttpUtils.post(url(logotype), it).let { resp ->
                                    g().log("[$logotype]组包数据上报，响应结果：\r\n $resp\r\n 上报内容：\r\n ${JSONObject.toJSONString(it)} \r\n", logotype)
                                }
                            }

                        }, 500, TimeUnit.MILLISECONDS)

                    }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun url(logotype: Int) = if (logotype / 10000 == 3 || mHeterogeneous.contains(logotype)) Constants.getEnviroUrl() else Constants.getBathUrl()


}
