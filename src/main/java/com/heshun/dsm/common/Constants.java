package com.heshun.dsm.common;

public class Constants {

	/**
	 * 广播地址
	 */
	public static String BROADCAST_ADDR = "255.255.255.255";
	/**
	 * 广播收取心跳的时间频率
	 */
	public static int BORADCAST_TIME_GAP = 30 * 1000;
	/**
	 * 发起遥测频率
	 */
	public static int REMOTE_SENSING_GAP = 5 * 60 * 1000;
	/**
	 * 远端接收广播的端口号，向这个端口循环发送广播
	 */
	public static int BROADCAST_PORT = 1032;
	/**
	 * Tcp监听端口，收取客户端传送的报文
	 */
	public static int TCP_ACCEPTOR_PORT = 9114;
	/**
	 * 进入空闲状态的时间间隔，单位s
	 */
	public static int TCP_IDLE_TIME = 30;
	/**
	 * 遥测数据头
	 */
	public final static byte HEAD_CODE_YC = 7;
	/**
	 * 遥信数据头
	 */
	public final static byte HEAD_CODE_YX = 8;
	/**
	 * 配置文件地址
	 */
	public final static String SERVER_PATH = "server.cfg";
	/**
	 * 配置文件地址
	 */
	public final static String CONFIG_PATH = "config.cfg";

	public final static String LOG_PATH = "log4j.properties";

	public static String LOG_OUT_PATH = "D:\\heshundsm\\managerlog\\";
	/**
	 * socket缓冲最小缓冲大小
	 */
	public final static int MIN_READBUFFER_SIZE = 20 * 2048;

	/**
	 * 需求侧后台domain
	 */
	public static String ELEC_SERVER_PRFIX = "http://dsm.gate.jsclp.cn/";
	/**
	 * 需求侧后台接口action
	 */
	private static String ELEC_ACTION = "dsm/api/front/insertdata";
	/**
	 * 需求侧后台接口action(批量上报接口)
	 */

	// http://dsm.gate.jsclp.cn/dsm/api/front/insertDataBach
	private static String BATCH_ACTION = "dsm/api/front/insertDataBach";
	/**
	 * 配电，环境监控后台domain
	 */
	public static String ENVIRO_SERVER_PRFIX = "http://pd.jsclp.cn/";
	/**
	 * 配电，环境监控后台接口action
	 */
	private static String ENVIRO_SERVER_ACTION = "pd/api/front/insertEnvironmentData";

	private static String TEMP_ACTION = "dsm/api/front/insertTempedata";
	/**
	 * 单通道内命令发送间隔
	 */
	public static int COMMAND_TIME_GAP_IN_SESSION = 200;
	/**
	 * 完成第一次查询，上报服务器的延长间隔,单位 s
	 */
	public static int FEED_BACK_DELAY = 10;
	/**
	 * 用于pannel显示的版本号
	 */
	public static String GATEWAY_VERSION = "v2.0.1";

	public static String getBathUrl() {
		return ELEC_SERVER_PRFIX + BATCH_ACTION;
	}

	public static String getTempUrl() {
		return ELEC_SERVER_PRFIX + TEMP_ACTION;
	}

	public static String getElecUrl() {

		return ELEC_SERVER_PRFIX + ELEC_ACTION;
	}

	public static String getEnviroUrl() {

		return ENVIRO_SERVER_PRFIX + ENVIRO_SERVER_ACTION;
	}

	public static String getEvFullUrl() {
		return ENVIRO_SERVER_PRFIX;
	}

	public static String getElecFullUrl() {
		return ELEC_SERVER_PRFIX;
	}

}
