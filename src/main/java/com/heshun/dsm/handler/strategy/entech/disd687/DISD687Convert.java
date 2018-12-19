package com.heshun.dsm.handler.strategy.entech.disd687;

import com.alibaba.fastjson.JSONObject;
import com.heshun.dsm.entity.convert.AbsJsonConvert;

public class DISD687Convert extends AbsJsonConvert<DISD687Packet> {

	public DISD687Convert(DISD687Packet packet) {
		super(packet);
	}

	public int getPid() {
		return mPacket._pid;
	}

	/**
	 * 获取虚拟cpu
	 */
	public int getAddress() {
		return mPacket.address;
	}

	/**
	 * 获取波特率
	 */
	public int getPort() {
		return mPacket._port;
	}

	/**
	 * 获取接线方式(控制字)
	 */
	public int getWireType() {
		return mPacket._wireType;
	}

	/**
	 * 获取软件版本号
	 */
	public String getVersion() {
		return String.valueOf(mPacket._version);
	}

	/**
	 * A相电压
	 */
	public float getUa() {
		// 三相三线接法没有Uabc值
		if (mPacket._wireType == 1) {
			return 0;
		} else {
			return (float) ((double) mPacket._ua / 10000 * Math.pow(10, mPacket._dpt));
		}
	}

	/**
	 * B相电压
	 */
	public float getUb() {
		// 三相三线接法没有Uabc值
		if (mPacket._wireType == 1) {
			return 0;
		} else {
			return (float) ((double) mPacket._ub / 10000 * Math.pow(10, mPacket._dpt));
		}
	}

	/**
	 * C相电压
	 */
	public float getUc() {
		// 三相三线接法没有Uabc值
		if (mPacket._wireType == 1) {
			return 0;
		} else {
			return (float) ((double) mPacket._uc / 10000 * Math.pow(10, mPacket._dpt));
		}
	}

	/**
	 * ab相电压
	 */
	public float getUab() {
		return (float) ((double) mPacket._uab / 10000 * Math.pow(10, mPacket._dpt));
	}

	/**
	 * bc相电压
	 */
	public float getUbc() {
		return (float) ((double) mPacket._ubc / 10000 * Math.pow(10, mPacket._dpt));
	}

	/**
	 * ca相电压
	 */
	public float getUca() {
		return (float) ((double) mPacket._uca / 10000 * Math.pow(10, mPacket._dpt));
	}

	/**
	 * a相电流
	 */
	public float getIa() {
		return (float) ((double) mPacket._ia / 10000 * Math.pow(10, mPacket._dct));
	}

	/**
	 * b相电流
	 */
	public float getIb() {
		return (float) ((double) mPacket._ib / 10000 * Math.pow(10, mPacket._dct));
	}

	/**
	 * c相电流
	 */
	public float getIc() {
		return (float) ((double) mPacket._ic / 10000 * Math.pow(10, mPacket._dct));
	}

	// ADD
	/**
	 * 频率
	 */
	public float getFreq() {
		return (float) (double) mPacket._freq / 100;
	}

	/**
	 * A相有功功率
	 */
	public float getPa() {
		return (float) (((double) mPacket._pa / 10000) * Math.pow(10, mPacket._dqt));
	}

	/**
	 * B相有功功率
	 */
	public float getPb() {
		return (float) ((double) mPacket._pb / 10000 * Math.pow(10, mPacket._dqt));
	}

	/**
	 * C相有功功率
	 */
	public float getPc() {
		return (float) ((double) mPacket._pc / 10000 * Math.pow(10, mPacket._dqt));
	}

	/**
	 * 三相有功功率
	 */
	public float getPTotal() {
		return (float) ((double) mPacket._ptotal / 10000 * Math.pow(10, mPacket._dqt));
	}

	// ADD
	/**
	 * A相无功功率
	 */
	public float getQa() {
		return (float) (((double) mPacket._qa / 10000) * Math.pow(10, mPacket._dqt));
	}

	/**
	 * B相无功功率
	 */
	public float getQb() {
		return (float) ((double) mPacket._qb / 10000 * Math.pow(10, mPacket._dqt));
	}

	/**
	 * C相无功功率
	 */
	public float getQc() {
		return (float) ((double) mPacket._qc / 10000 * Math.pow(10, mPacket._dqt));
	}

	/**
	 * 三相无功功率
	 */
	public float getQTotal() {
		return (float) ((double) mPacket._qtotal / 10000 * Math.pow(10, mPacket._dqt));
	}

	// ADD
	/**
	 * A相视在功率
	 */
	public float getSa() {
		return (float) (((double) mPacket._sa / 10000) * Math.pow(10, mPacket._dqt));
	}

	/**
	 * B相视在功率
	 */
	public float getSb() {
		return (float) ((double) mPacket._sb / 10000 * Math.pow(10, mPacket._dqt));
	}

	/**
	 * C相视在功率
	 */
	public float getSc() {
		return (float) ((double) mPacket._sc / 10000 * Math.pow(10, mPacket._dqt));
	}

	/**
	 * 三相视在功率
	 */
	public float getSTotal() {
		return (float) ((double) mPacket._stotal / 10000 * Math.pow(10, mPacket._dqt));
	}

	//
	/**
	 * A相功率因数
	 */
	public float getPFA() {
		return (float) ((double) mPacket._pfa / 1000);
	}

	/**
	 * B相功率因数
	 */
	public float getPFB() {
		return (float) ((double) mPacket._pfb / 1000);
	}

	/**
	 * C相功率因数
	 */
	public float getPFC() {
		return (float) ((double) mPacket._pfc / 1000);
	}

	private long getDL(long r) {
		return (long) (((double) r) / 2.5) * 1000;
	}

	/**
	 * 三相功率因数
	 */
	public float getPFTotal() {
		return (float) ((double) mPacket._pftotal / 1000);
	}

	@Override
	public JSONObject toJsonObj(String ip) {

		JSONObject _json = super.toJsonObj(ip);

		_json.put("pid", getPid());
		_json.put("control", getWireType());
		_json.put("version", getVersion());
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

		_json.put("pa", mPacket._pa * 1000);
		_json.put("pb", mPacket._pb * 1000);
		_json.put("pc", mPacket._pc * 1000);
		_json.put("pt", mPacket._ptotal * 1000);
		// ADD
		_json.put("qa", mPacket._qa * 1000);
		_json.put("qb", mPacket._qb * 1000);
		_json.put("qc", mPacket._qc * 1000);
		_json.put("qt", mPacket._qtotal * 1000);
		// ADD
		_json.put("sa", mPacket._pa * 1000);
		_json.put("sb", mPacket._pb * 1000);
		_json.put("sc", mPacket._pc * 1000);
		_json.put("st", mPacket._ptotal * 1000);
		// ADD
		_json.put("pfa", mPacket._pfa);
		_json.put("pfb", mPacket._pfb);
		_json.put("pfc", mPacket._pfc);
		_json.put("pft", mPacket._pftotal);
		_json.put("epi", getDL(mPacket.epi));
		_json.put("eql", getDL(mPacket.eql));
		return _json;
	}

	@Override
	public String getType() {
		return "eqa300";
	}

}