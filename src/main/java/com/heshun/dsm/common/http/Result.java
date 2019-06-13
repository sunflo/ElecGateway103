package com.heshun.dsm.common.http;

/**
 * 响应结果信息类
 *
 * @author: xu.jin
 */
public class Result {

	public static final boolean SUCC = true;
	public static final boolean FAILURE = false;

	private boolean succ = SUCC;
	private int statusCode = 200;
	private String msg;
	private Object data;
	private long time = System.currentTimeMillis();

	public Result() {
	}

	public Result(String msg) {
		this.msg = msg;
	}

	public Result(boolean succ) {
		this.succ = succ;
	}

	public Result(boolean succ, String msg) {
		this.succ = succ;
		this.msg = msg;
	}

	public Result(boolean succ, Object data) {
		this(succ, succ ? 200 : 400, data);
	}

	public Result(boolean succ, Object data, String msg) {
		this(succ, msg);
		this.data = data;
	}

	public Result(int statusCode, Object data) {
		this(200 == statusCode ? SUCC : FAILURE, statusCode, data);
	}

	public Result(boolean succ, int statusCode, Object data) {
		this.succ = succ;
		this.statusCode = statusCode;
		this.data = data;
	}

	public Result(Object data) {
		this.data = data;
	}

	public boolean isSucc() {
		return succ;
	}

	public void setSucc(boolean succ) {
		this.succ = succ;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
