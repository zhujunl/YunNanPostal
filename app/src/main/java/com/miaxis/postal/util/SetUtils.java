package com.miaxis.postal.util;

import java.util.Set;

/**
 * @author Tank
 * @date 2021/8/6 7:36 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class SetUtils {

    public static boolean isNull(Set<?> set) {
        return set == null;
    }

    public static boolean isNullOrEmpty(Set<?> set) {
        return set == null || set.isEmpty();
    }


}
