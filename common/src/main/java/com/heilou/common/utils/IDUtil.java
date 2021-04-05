package com.heilou.common.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * @author heilou
 * @version 1.0
 * @description:
 * @date 2021/3/30 11:51
 */
public class IDUtil {
    private static final Snowflake snowflake = IdUtil.createSnowflake(1, 1);

    public static String getSnowFlakeId(){
        return snowflake.nextIdStr();
    }


}
