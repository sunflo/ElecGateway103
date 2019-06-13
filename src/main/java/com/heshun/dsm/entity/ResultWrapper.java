package com.heshun.dsm.entity;

import java.util.Arrays;

public class ResultWrapper {

	//默认2个字节的无符号整形
	private int dataTyp = 0x0C;
	private byte[] originData;

	public ResultWrapper(byte[] origin) {
		this.originData = origin;
	}

	public ResultWrapper(int dt, byte[] o) {
		this.dataTyp = dt;
		this.originData = o;
	}

	public int getDataTyp() {
		return dataTyp;
	}

	public void setDataTyp(int dataTyp) {
		this.dataTyp = dataTyp;
	}

	public byte[] getOriginData() {
		return originData;
	}

	public void setOriginData(byte[] originData) {
		this.originData = originData;
	}
	
	public boolean illegal() {
		byte[] _temp =new byte[] {(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0x7f};
		return Arrays.equals(originData, _temp);
	}

}
