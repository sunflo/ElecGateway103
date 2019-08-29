package com.heshun.dsm.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.util.TextUtils;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.heshun.dsm.common.Config;
import com.heshun.dsm.common.Constants;
import com.heshun.dsm.entity.Device;
import com.heshun.dsm.handler.GateWayCodecFactory;
import com.heshun.dsm.handler.IoMessageHandler;
import com.heshun.dsm.ui.ControlPanel.OnStatusChangeListener;
import com.heshun.dsm.util.ELog;
import com.heshun.dsm.util.SessionUtils;

/**
 * 系统操作合计
 *
 * @author huangxz
 */
public class SystemHelper {
    // 网关的Tcp接收器，所有TCP报文，通过他来接收
    public static NioSocketAcceptor minaAcceptor = new NioSocketAcceptor();

    //    public static TotalQueryTask mQueryTask;
    public static TotalQueryEmitter mQueryTask;

    public static ScheduledExecutorService mHttpRequestThreadPool = Executors.newScheduledThreadPool(12);

    /**
     * 从配置文件读取管理机与采集装置的从属关系
     */
    @Deprecated
    public static void loadDeviceConfig() throws IOException {

        FileInputStream fis = null;
        try {

            if (Config.isDebug) {
                fis = new FileInputStream(new File("src/main/resource/" + Constants.CONFIG_PATH));
            } else {
                fis = new FileInputStream(
                        new File(SystemHelper.class.getResource("/" + Constants.CONFIG_PATH).getPath()));
            }
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String str = new String(buffer);
            String[] _managers = str.split("#");
            Map<String, HashMap<Integer, List<Device>>> devices = new HashMap<>();
            for (String m : _managers) {
                // 用双斜杠注释。。
                if (m.trim().startsWith("//"))
                    continue;
                // 172.31.59.44:1,83,eqa300,26;
                String[] _manager = m.split(":");
                String ip = _manager[0].trim();
                String _device = _manager[1].trim();

                String[] _cpu = _device.split(",");
                String cpu = _cpu[0].trim();
                // String address = _cpu[1].trim();
                String type = _cpu[1].trim();
                String length = _cpu[2].trim();

                HashMap<Integer, List<Device>> map = devices.get(ip);
                if (map == null) {
                    map = new HashMap<>();
                    devices.put(ip, map);
                }
                List<Device> _devices = devices.get(ip).get(Integer.valueOf(cpu));
                if (_devices == null) {
                    _devices = new ArrayList<>();
                    map.put(Integer.valueOf(cpu), _devices);
                }
                map.get(Integer.valueOf(cpu)).add(new Device(Integer.valueOf(cpu), type, Integer.valueOf(length)));
                devices.put(ip, map);
            }
        } finally {
            if (null != fis)
                fis.close();
        }
    }

    // 从配置文件加载配置，网关运行的一些必要参数

    public static void loadSystemConfig() throws IOException, com.alibaba.fastjson.JSONException {
        FileInputStream fis = null;

        try {
            if (Config.isDebug) {
                fis = new FileInputStream(new File("src/main/resource/config.cfg"));
            } else {
                fis = new FileInputStream(new File(SystemHelper.class.getResource("/config.cfg").getPath()));
            }

            byte[] buffer = new byte[fis.available()];

            fis.read(buffer);
            String str = new String(buffer);
            JSONObject _config = JSON.parseObject(str);
            String _eserver = _config.getString("elec_server");
            String _pdServer = _config.getString("pd_server");
            int _port = _config.getIntValue("port");
            int sesing_gap = _config.getIntValue("remote_sesing_gap_ms");
            int request_gap = _config.getIntValue("request_send_gap_ms");
            int feed_gap = _config.getIntValue("feed_back_delay_gap_s");
            String version = _config.getString("version");
            String logPath = _config.getString("log_path");

            ELog.getInstance().log(String.format("读取配置文件：\r\n%s \r\n 解析结果[%s,%s,%s,%s,%s,%s]", str, _eserver, _port,
                    sesing_gap, request_gap, feed_gap, logPath));
            Constants.ELEC_SERVER_PRFIX = _eserver;
            Constants.ENVIRO_SERVER_PRFIX = _pdServer;
            Constants.TCP_ACCEPTOR_PORT = _port;
            Constants.COMMAND_TIME_GAP_IN_SESSION = request_gap;
            Constants.REMOTE_SENSING_GAP = sesing_gap;
            Constants.FEED_BACK_DELAY = feed_gap;
            Constants.GATEWAY_VERSION = version;
            Constants.LOG_OUT_PATH = logPath;

        } finally {
            if (null != fis)
                fis.close();
        }

    }

    @Deprecated
    public static boolean loadServerAddress() {
        FileInputStream fis = null;
        String server;
        try {

            if (Config.isDebug) {
                fis = new FileInputStream(new File("src/main/resource/" + Constants.SERVER_PATH));
            } else {
                fis = new FileInputStream(
                        new File(SystemHelper.class.getResource("/" + Constants.SERVER_PATH).getPath()));
            }
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String _server = new String(buffer);
            server = _server.split("#")[0];
            int port = Integer.valueOf(_server.split("#")[1]);
            if (TextUtils.isEmpty(server))
                return false;
            Constants.ELEC_SERVER_PRFIX = server;
            Constants.TCP_ACCEPTOR_PORT = port;
            ELog.getInstance().log(String.format("server_url:%s  ////  tcp_port:%s", Constants.ELEC_SERVER_PRFIX,
                    Constants.TCP_ACCEPTOR_PORT));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != fis)
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }

    public static HashMap<Integer, Device> loadDevicesByLogoType(int logotype) {
        FileInputStream fis = null;
        try {

            if (Config.isDebug) {
                fis = new FileInputStream(new File(String.format("src/main/resource/station_%s.cfg", logotype)));
            } else {
                fis = new FileInputStream(
                        new File(SystemHelper.class.getResource(String.format("/station_%s.cfg", logotype)).getPath()));
            }
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String str = new String(buffer);
            // ELog.getInstance().log(String.format("%s号站，设备配置：%s", logotype,
            // str));
            String[] _devices = str.split("#");
            // List<Device> devices = new ArrayList<Device>();
            HashMap<Integer, Device> devices = new HashMap<>();
            for (String m : _devices) {
                // 用双斜杠注释。。
                if (m.trim().startsWith("//") || m.trim().length() == 0)
                    continue;
                // 16,eqa300,26#
                String[] _attrs = m.trim().split(",");
                Device _device = new Device(Integer.valueOf(_attrs[0]), _attrs[1], Integer.valueOf(_attrs[2]));
                devices.put(Integer.valueOf(_attrs[0]), _device);
            }
            ELog.getInstance().log(String.format("%s号站，设备总数：%s", logotype, devices.size()));
            return devices;
        } catch (NullPointerException e) {
            ELog.getInstance().err(Arrays.toString(e.getStackTrace()));
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取开关量模块的联动配置
     */
    public static void loadSwitchConfig(IoSession s, int cpu) {
        HashMap<String, byte[]> cache = SessionUtils.getSwitchConfig(s);
        if (null != cache) {
            return;
        }

        int logotype = SessionUtils.getLogoType(s);
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        HashMap<String, byte[]> result;
        String fileName = String.format("%s-%s.cfg", logotype, cpu);
        try {

            if (Config.isDebug) {
                fis = new FileInputStream(new File(String.format("src/main/resource/switch/%s", fileName)));
            } else {
                fis = new FileInputStream(
                        new File(SystemHelper.class.getResource(String.format("/switch/%s", fileName)).getPath()));
            }
            isr = new InputStreamReader(fis);
            reader = new BufferedReader(isr);
            result = new HashMap<>();
            for (; ; ) {
                String _line = reader.readLine();
                if (_line == null)
                    break;
                String[] temp = _line.split(",");
                String key = temp[0] + "-" + temp[1];
                byte[] v = new byte[]{Byte.valueOf(temp[2]), Byte.valueOf(temp[3]), Byte.valueOf(temp[4])};
                result.put(key, v);
            }
            if (!result.isEmpty())
                SessionUtils.cacheSwitchConfig(result, s);
        } catch (FileNotFoundException e) {
            ELog.getInstance().err(Arrays.toString(e.getStackTrace()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException ignored) {

                }
            }
            if (null != isr) {
                try {
                    isr.close();
                } catch (IOException ignored) {

                }
            }
            if (null != fis)
                try {
                    fis.close();
                } catch (IOException ignored) {

                }
        }
    }

    /**
     * 开启tcp端口的监听，在1048端口
     */
    public static void initMessageListener(OnStatusChangeListener listener) throws IOException {

        minaAcceptor.getSessionConfig().setMinReadBufferSize(Constants.MIN_READBUFFER_SIZE);

        minaAcceptor.getSessionConfig().setReadBufferSize(100 * 1024);

        minaAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new GateWayCodecFactory()));

        minaAcceptor.setHandler(new IoMessageHandler(listener));

        minaAcceptor.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, Constants.TCP_IDLE_TIME);

        minaAcceptor.bind(new InetSocketAddress(Constants.TCP_ACCEPTOR_PORT));

        // PropertyConfigurator.configure("log4j.properties");

    }

    /**
     * 向同一网段的从机广播IP地址
     */
    public static void start() {

        // mQueryTask.start();

    }

    /**
     * 开启遍历总查询
     *
     * @return
     */
    public static TotalQueryEmitter initTotalQuery(OnStatusChangeListener listener) {

//        mQueryTask = new TotalQueryTask();
        mQueryTask = new TotalQueryEmitter(listener);
        mQueryTask.start();
        // mQueryTask.start();
        return mQueryTask;
    }

}
