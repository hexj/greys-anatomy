package com.github.ompc.greys.common.di;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实现标记
 * Created by vlinux on 16/2/2.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Impl {

    /**
     * 目标实现类类型
     *
     * @return 实现类类型
     */
    Class<?> value();

}
