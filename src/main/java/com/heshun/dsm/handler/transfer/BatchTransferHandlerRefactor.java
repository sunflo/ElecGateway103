package com.heshun.dsm.handler.transfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSONObject;
import com.heshun.dsm.common.Constants;
import com.heshun.dsm.common.http.HttpUtils;
import com.heshun.dsm.entity.convert.AbsJsonConvert;
import com.heshun.dsm.entity.global.DataBuffer;
import com.heshun.dsm.service.SystemHelper;
import com.heshun.dsm.ui.ControlPanel.OnStatusChangeListener;
import com.heshun.dsm.util.ELog;
import com.heshun.dsm.util.SessionUtils;

/**
 * 批量传输
 * 
 * @author huangxz
 * 
 */
public class BatchTransferHandlerRefactor implements ItfTransferHandler {

	private OnStatusChangeListener mListener;

	public BatchTransferHandlerRefactor(OnStatusChangeListener mListener) {
		this.mListener = mListener;
	}

	private int count;

	@Override
	public void transfer(IoSession session, AbsJsonConvert<?> message) {

		final int logotype = SessionUtils.getLogoType(session);

		Map<Integer, AbsJsonConvert<?>> datas = DataBuffer.getInstance().getBuffer().get(logotype);
		if (datas == null) {
			datas = new HashMap<Integer, AbsJsonConvert<?>>();
			DataBuffer.getInstance().getBuffer().put(logotype, datas);
		}

		datas.put(message.getOriginal().address, message);
		// 扰动值需要立即上送
		if (message.notify) {
			notifyChange(logotype, message);
		}
		count = (count % 3) + 1;
		if (count == 1)
			mListener.onDataChanged();

	}

	private void notifyChange(final int logotype, final AbsJsonConvert<?> message) {
		SystemHelper.mHttpRequestThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				final JSONObject jo = new JSONObject();

				ELog.getInstance().log(String.format("%s-发生突变，需要立即上送", logotype), logotype);
				List<Object> data = new ArrayList<Object>();
				data.add(message.toJsonObj(String.valueOf(logotype)));
				jo.put("ip", logotype);
				jo.put("data", data);
				jo.put("isAlarm", true);
				ELog.getInstance().log(String.format("突变上送|\r\n %s \r\n %s", logotype, JSONObject.toJSONString(jo)),
						logotype);
				ELog.getInstance().log(HttpUtils.post(Constants.getEvFullUrl(), jo), logotype);
			}
		});
	}
}
