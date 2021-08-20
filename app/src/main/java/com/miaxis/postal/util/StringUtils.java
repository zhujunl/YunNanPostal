package com.miaxis.postal.util;

import java.util.regex.Pattern;

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

    /**

     * 限制只能输入中文

     */

    public static boolean isChineseWord(String str){
        String pattern = "[\u4e00-\u9fa5]+";
        return Pattern.matches(pattern, str);
    }

}
