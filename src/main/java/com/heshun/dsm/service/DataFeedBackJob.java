package com.heshun.dsm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSONObject;
import com.heshun.dsm.common.Constants;
import com.heshun.dsm.common.http.HttpUtils;
import com.heshun.dsm.entity.convert.AbsJsonConvert;
import com.heshun.dsm.entity.global.DataBuffer;
import com.heshun.dsm.util.ELog;
import com.heshun.dsm.util.Utils;

public class DataFeedBackJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		final Map<Integer, Map<Integer, AbsJsonConvert<?>>> _copy = new HashMap<Integer, Map<Integer, AbsJsonConvert<?>>>();

		ELog.getInstance().log(String.format("&&&&&&&&&&&&&&&&&开始发送本周期数据%s&&&&&&&&&&&&&&&&&", Utils.getCurrentTime()));
		_copy.putAll(DataBuffer.getInstance().getBuffer());
		// DataBuffer.getInstance().getBuffer().clear();
		// mListener.onDataChanged();
		for (final Entry<Integer, Map<Integer, AbsJsonConvert<?>>> entry : _copy.entrySet()) {
			final int logotype = entry.getKey();
			SystemHelper.mHttpRequestThreadPool.schedule(new Runnable() {

				@Override
				public void run() {
					try {
						Map<Integer, AbsJsonConvert<?>> _datas = entry.getValue();
						List<Object> datas = new ArrayList<Object>();
						for (Entry<Integer, AbsJsonConvert<?>> entry : _datas.entrySet()) {
							datas.add(entry.getValue().toJsonObj(String.valueOf(logotype)));
						}
						ELog.getInstance().log(String.format("%s-周期结束，组包发送，共%s", logotype, datas.size()), logotype);
						final JSONObject jo = new JSONObject();
						jo.put("ip", logotype);
						jo.put("data", datas);
						jo.put("isAlarm", false);
						ELog.getInstance().log(String.format("%s周期组包数据====|\r\n %s", logotype, JSONObject.toJSONString(jo)),
								logotype);
						int _s = logotype / 10000;
						if (_s == 3 || logotype == 10047 || logotype == 10051 || logotype == 10052) {
							ELog.getInstance().log(HttpUtils.post(Constants.getEnviroUrl(), jo), logotype);
						} else {
							ELog.getInstance().log(HttpUtils.post(Constants.getBathUrl(), jo), logotype);
						}
					}catch (Exception e){
						e.printStackTrace();
					}
					 
				}
			}, 500, TimeUnit.MILLISECONDS);
		}
	}

}
