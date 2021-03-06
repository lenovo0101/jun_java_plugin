package com.jun.plugin.task.quartz.util;

import com.jun.plugin.task.quartz.job.base.BaseJob;

/**
 * <p>
 * 定时任务反射工具类
 * </p>
 *
 * @package: com.xkcoding.task.quartz.util
 * @description: 定时任务反射工具类
 * @author: yangkai.shen
 * @date: Created in 2018-11-26 13:33
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
public class JobUtil {
    /**
     * 根据全类名获取Job实例
     *
     * @param classname Job全类名
     * @return {@link BaseJob} 实例
     * @throws Exception 泛型获取异常
     */
    public static BaseJob getClass(String classname) throws Exception {
        Class<?> clazz = Class.forName(classname);
        return (BaseJob) clazz.newInstance();
    }
}
