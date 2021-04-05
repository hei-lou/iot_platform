package com.heilou.common.utils;

import cn.hutool.system.OsInfo;

/**
 * @author heilou
 * @version 1.0
 * @description:
 * @date 2021/4/1 17:34
 */
public class SystemUtil {

    private static OsInfo osInfo = new OsInfo();


    public static boolean isLinux(){
        return osInfo.isLinux();
    }

}
