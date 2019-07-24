package com.heshun.dsm.util;

import com.heshun.dsm.common.Config;
import com.heshun.dsm.service.SystemHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static byte[] getBytes(short s) {
        int count = 2;
        byte[] arr = new byte[count];
        for (int i = 0; i < count; i++) {
            arr[i] = (byte) ((int) (s >> i * 8 & 0xFF));
        }
        return arr;
    }

    public static byte[] getBytes(int s) {
        int count = 4;
        byte[] arr = new byte[count];
        for (int i = 0; i < count; i++) {
            arr[i] = (byte) ((int) (s >> i * 8 & 0xFF));
        }
        return arr;
    }

    public static void printBitString(short s) {
    }

    /*
     * 字节转二进制字符串
     */
    public static String getBitString(byte b) {
        int z = b;
        z |= 256;
        String str = Integer.toBinaryString(z);
        int len = str.length();
        return str.substring(len - 8, len);
    }

    public static float byte2float(byte[] b, boolean r) {
        byte[] reverse = new byte[4];
        if (!r) {
            reverse[0] = b[3];
            reverse[1] = b[2];
            reverse[2] = b[1];
            reverse[3] = b[0];
        } else
            reverse = b;
        return Float.intBitsToFloat(byteArrayToInt(reverse, 0));
    }

    public static float byte2float(byte[] b) {
        byte[] reverse = new byte[4];
        reverse[0] = b[3];
        reverse[1] = b[2];
        reverse[2] = b[1];
        reverse[3] = b[0];
        return Float.intBitsToFloat(byteArrayToInt(reverse, 0));
    }

    public static int byte2Int(byte[] b, boolean r) {
        if (r) {
            byte[] reverse = new byte[4];
            reverse[0] = b[3];
            reverse[1] = b[2];
            reverse[2] = b[1];
            reverse[3] = b[0];
            return byteArrayToInt(reverse, 0);
        }
        return byteArrayToInt(b, 0);
    }

    /**
     * 字节数组转int
     */
    public static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    /*
     * 将二进制字符串转换回字节
     */
    public static byte bit2byte(String bString) {
        byte result = 0;
        for (int i = bString.length() - 1, j = 0; i >= 0; i--, j++) {
            result += (Byte.parseByte(bString.charAt(i) + "") * Math.pow(2, j));
        }
        return result;
    }

    /*
     * char转byte数组
     */
    public static byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }

    /**
     * short转二进制字符串
     */
    public static String short2bitString(short s) {
        byte[] bytes = getBytes(s);
        return bytes[0] + "" + bytes[1];

    }

    public static short bytes2Short(byte[] b) {
        return (short) (((b[1] << 8) | b[0] & 0xff));
    }

    public static short bytes2Short(byte high, byte low) {
        return (short) (((low << 8) | high & 0xff));
    }

    public static long bytes2Long(byte[] high, byte[] low) {

        byte[] bytes = new byte[8];
        bytes[4] = high[1];
        bytes[5] = high[0];
        bytes[6] = low[1];
        bytes[7] = low[0];

        return ByteUtils.byte8ToLong(bytes) & 0xFFFFFFFFl;
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    /**
     * /dri/%s.dr
     */
    public static File getConfigFile(String path, String fileName) throws FileNotFoundException {
        try {
            if (Config.isDebug)
                return new File("src/main/resource/" + path + "/" + fileName);
            else {
                File dir = new File(SystemHelper.class.getResource("/").getPath());
                File dri = new File(dir, path);
                File file = new File(dri, fileName);
                ELog.getInstance().log("\n\n\n\n\n\n" + file.getAbsolutePath() + "\n\n\n\n\n\n");
                return file;
            }
        } catch (NullPointerException e) {
            throw new FileNotFoundException(path);
        }
    }


}
