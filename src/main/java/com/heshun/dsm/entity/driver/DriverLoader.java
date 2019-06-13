package com.heshun.dsm.entity.driver;

import com.heshun.dsm.util.ELog;
import com.heshun.dsm.util.Utils;
import com.sun.istack.internal.NotNull;
import org.apache.http.util.TextUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class DriverLoader {

    private static ConcurrentHashMap<String, DeviceDriver> mDriverContainer = new ConcurrentHashMap<>();

    public static DeviceDriver load(@NotNull String _temp) {
        String key = _temp.toLowerCase();
        if (mDriverContainer.containsKey(key))
            return mDriverContainer.get(key);
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(Utils.getConfigFile(String.format("/dri/%s.dr", key)));
            br = new BufferedReader(fr);
            DeviceDriver driver = new DeviceDriver();
            driver.setName(_temp);
            for (; ; ) {
                String line = br.readLine();
                if (line == null)
                    break;
                if (line.startsWith("#") || line.startsWith("//") || TextUtils.isEmpty(line)) {
                    continue;
                    //注释，do nothing
                } else if (line.startsWith("<")) {
                    if (line.endsWith(">")) {
                        String mask = line.substring(1, line.length() - 1);
                        if (!TextUtils.isEmpty(mask))
                            driver.setMask(mask.trim());
                    }
                } else {
                    DriverItem item = new DriverItem(line);
                    driver.register(item);
                }
            }
            mDriverContainer.put(key, driver);
            return driver;
        } catch (FileNotFoundException e) {
            ELog.getInstance().err(String.format("未找到驱动文件[%s.dr]，尝试使用兼容解包策略", key));

        } catch (IOException e) {
            ELog.getInstance().err(String.format("加载驱动失败，请检查驱动文件[%s.dr]是否正常", key));

        } catch (IllegalStateException e) {
            ELog.getInstance().err(String.format("驱动文件[%s.dr]格式异常，请检查", key));

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
        }

        return null;
    }

    private String decrypt(String origin) {
        return origin;
    }

}
