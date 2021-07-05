package com.miaxis.postal.util;

/**
 * @author Tank
 * @date 2021/7/5 1:10 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class StringUtils {


    public static boolean isEquals(String content1, String content2) {
        if (content1 == null && content2 == null) {
            return true;
        }
        if (content1 == null || content2 == null) {
            return false;
        }
        return content1.equals(content2);
    }


}
