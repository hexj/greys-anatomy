package com.github.ompc.greys.core.manager;

import java.util.Collection;

/**
 * 类加载数据源
 * Created by vlinux on 16/2/2.
 */
public interface ClassDataSource {

    /**
     * 获取所有可被感知的Class
     *
     * @return Class集合
     */
    Collection<Class<?>> allLoadedClasses();

}
