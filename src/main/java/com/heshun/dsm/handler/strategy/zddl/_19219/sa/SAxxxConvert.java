package com.heshun.dsm.handler.strategy.zddl._19219.sa;

import com.alibaba.fastjson.JSONObject;
import com.heshun.dsm.entity.convert.AbsJsonConvert;

public class SAxxxConvert extends AbsJsonConvert<SAxxxPacket> {

	public SAxxxConvert(SAxxxPacket packet) {
		super(packet);
	}

	/**
	 * 获取虚拟cpu
	 */
	public int getAddress() {
		return mPacket.address;
	}

	@Override
	public JSONObject toJsonObj(String ip) {

		JSONObject _json = super.toJsonObj(ip);

		_json.put("pid", 0);
		_json.put("control", 1);
		_json.put("version", 0);
		_json.put("ua", mPacket._ua);
		_json.put("ub", mPacket._ub);
		_json.put("uc", mPacket._uc);
		_json.put("uab", mPacket._uab);
		_json.put("ubc", mPacket._ubc);
		_json.put("uca", mPacket._uca);
		_json.put("ia", mPacket._ia);
		_json.put("ib", mPacket._ib);
		_json.put("ic", mPacket._ic);
		// ADD
		_json.put("freq", mPacket._freq);

		_json.put("pa", mPacket._pa*1000);
		_json.put("pb", mPacket._pb*1000);
		_json.put("pc", mPacket._pc*1000);
		_json.put("pt", mPacket._ptotal*1000);
		// ADD
		_json.put("qa", mPacket._qa*1000);
		_json.put("qb", mPacket._qb*1000);
		_json.put("qc", mPacket._qc*1000);
		_json.put("qt", mPacket._qtotal*1000);
		// ADD
		_json.put("sa", mPacket._sa*1000);
		_json.put("sb", mPacket._sb*1000);
		_json.put("sc", mPacket._sc*1000);
		_json.put("st", mPacket._stotal*1000);
		// ADD
		_json.put("pfa", mPacket._pfa);
		_json.put("pfb", mPacket._pfb);
		_json.put("pfc", mPacket._pfc);
		_json.put("pft", mPacket._pftotal);
		_json.put("epi", mPacket.epi*1000);
		_json.put("eql", mPacket.eql*1000);
		return _json;
	}

	@Override
	public String getType() {
		return "eqa300";
	}

}