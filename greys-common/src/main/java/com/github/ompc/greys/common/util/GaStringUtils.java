package com.github.ompc.greys.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Greys内部的字符串工具类
 * Created by vlinux on 16/2/2.
 */
public class GaStringUtils {

    /**
     * 正则表达式_匹配全字符串
     */
    public static final String REGEX_MATCH_ANY = ".*";

    /**
     * 翻译类名称<br/>
     * 将 java/lang/String 的名称翻译成 java.lang.String
     *
     * @param className 类名称 java/lang/String
     * @return 翻译后名称 java.lang.String
     */
    public static String tranClassName(String className) {
        return StringUtils.replace(className, "/", ".");
    }

    /**
     * 获取异常的原因描述
     *
     * @param t 异常
     * @return 异常原因
     */
    public static String getCauseMessage(Throwable t) {
        if (null != t.getCause()) {
            return getCauseMessage(t.getCause());
        }
        return t.getMessage();
    }

}
