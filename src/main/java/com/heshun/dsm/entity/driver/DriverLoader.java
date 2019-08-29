package com.heshun.dsm.entity.driver;

import com.heshun.dsm.common.Config;
import com.heshun.dsm.util.ELog;
import com.heshun.dsm.util.Utils;
import org.apache.http.util.TextUtils;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentHashMap;

import static com.heshun.dsm.common.Config.DK;

public class DriverLoader {

    private static ConcurrentHashMap<String, DeviceDriver> mDriverContainer = new ConcurrentHashMap<>();

    //卸载驱动
    public static boolean unload(String name) {
        if (mDriverContainer.containsKey(name)) {
            mDriverContainer.remove(name);
            return true;
        }
        return false;
    }


    public static DeviceDriver load(String _temp) {
        String fileName = _temp.toLowerCase();
        if (mDriverContainer.containsKey(fileName))
            return mDriverContainer.get(fileName);


        FileReader fr = null;
        BufferedReader br = null;
        File configFile = null;
        try {
            decrypt(fileName);
            configFile = Utils.getConfigFile("dri", String.format("%s.tmp", fileName));
            fr = new FileReader(configFile);
            br = new BufferedReader(fr);
            DeviceDriver driver = new DeviceDriver();
            driver.setName(_temp);
            for (; ; ) {
                String line = br.readLine();
                if (line == null)
                    break;
                else
                    line = line.trim();
                if (line.startsWith("#") || line.startsWith("//") || TextUtils.isEmpty(line)) {
                    continue;
                    //注释，do nothing
                } else if (line.startsWith("[")) {
                    if (line.endsWith("]")) {
                        String mask = line.substring(1, line.length() - 1);
                        if (!TextUtils.isEmpty(mask))
                            driver.setMask(mask.trim());
                    }
                } else {
                    DriverItem item = new DriverItem(line);
                    driver.register(item);
                }
            }
            if (driver.size() > 0) {
                mDriverContainer.put(fileName, driver);
                return driver;
            }
        } catch (FileNotFoundException e) {
            ELog.getInstance().err(String.format("未找到驱动文件[%s.dr]，尝试使用兼容解包策略", fileName));

        } catch (IOException e) {
            ELog.getInstance().err(String.format("加载驱动失败，请检查驱动文件[%s.dr]是否正常", fileName));

        } catch (IllegalStateException e) {
            ELog.getInstance().err(String.format("驱动文件[%s.dr]格式异常，请检查", fileName));

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (configFile != null && configFile.exists() && !Config.isDebug)
                configFile.delete();
        }

        return null;
    }

    private static void decrypt(String oFileName) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        OutputStreamWriter fileWriter = null;

        try {
            File configFile = Utils.getConfigFile("dri", String.format("%s.dr", oFileName));

            fis = new FileInputStream(configFile);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String origin = new String(buffer);

            String code = URLDecoder.decode(decoder(origin.substring(DK[2])), "UTF-8");
            if (code.length() <= 8)
                throw new IllegalStateException();
            String result = decoder(code.substring(DK[0], code.length() - DK[1] - DK[3]).concat(code.substring(code.length() - DK[3])));
            File target = Utils.getConfigFile("dri", String.format("%s.tmp", oFileName));

            fos = new FileOutputStream(target);

            fileWriter = new OutputStreamWriter(fos);
            fileWriter.write(URLDecoder.decode(result, "UTF-8"));
            fileWriter.flush();

        } finally {
            if (fis != null)
                fis.close();

            if (fos != null)
                fos.close();
            if (fileWriter != null)
                fileWriter.close();
        }

    }

    private static String decoder(String origin) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return new String(decoder.decodeBuffer(origin));
    }


}
