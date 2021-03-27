package com.kongqw.serialportlibrary.utils;

/**
 * @author Tank
 * @date 2021/3/27 15:17
 * @des
 * @updateAuthor
 * @updateDes
 */
public class ByteUtils {


    public static String bytes2hex(byte[] hex) {
        StringBuilder sb = new StringBuilder();
        if (hex != null) {
            for (byte b : hex) {
                sb.append(String.format("%02x ", b).toUpperCase());
            }
        }
        return sb.toString();
    }


}
