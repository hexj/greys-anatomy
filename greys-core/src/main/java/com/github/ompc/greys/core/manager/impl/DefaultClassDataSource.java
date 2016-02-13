package com.github.ompc.greys.core.manager.impl;

import com.github.ompc.greys.core.manager.ClassDataSource;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * 类加载数据源实现
 * Created by vlinux on 16/2/2.
 */
public class DefaultClassDataSource implements ClassDataSource {

    private final Instrumentation inst;

    public DefaultClassDataSource(Instrumentation inst) {
        this.inst = inst;
    }

    @Override
    public Collection<Class<?>> allLoadedClasses() {
        final Class<?>[] classArray = inst.getAllLoadedClasses();
        return null == classArray
                ? new ArrayList<Class<?>>()
                : Arrays.asList(classArray);
    }

}
