package com.github.ompc.greys.core.manager;

import com.github.ompc.greys.common.di.Impl;
import com.github.ompc.greys.core.manager.impl.DefaultReflectManager;
import com.github.ompc.greys.core.util.GaMethod;
import com.github.ompc.greys.core.util.matcher.Matcher;

import java.util.Collection;

/**
 * 反射操作管理类
 * Created by oldmanpushcart@gmail.com on 15/11/1.
 */
@Impl(DefaultReflectManager.class)
public interface ReflectManager {

    /**
     * 搜索类
     *
     * @param classMatcher 类匹配
     * @return 返回匹配的类集合
     */
    Collection<Class<?>> searchClass(Matcher<Class<?>> classMatcher);

    /**
     * 搜索目标类的所有子类
     *
     * @param targetClass 目标类
     * @return 返回匹配的类集合
     */
    Collection<Class<?>> searchSubClass(Class<?> targetClass);

    /**
     * 搜索类极其子类
     *
     * @param classMatcher 类匹配
     * @return 返回匹配的类集合
     */
    Collection<Class<?>> searchClassWithSubClass(Matcher<Class<?>> classMatcher);

    /**
     * 搜索目标类的所有可见匹配方法
     *
     * @param targetClass     目标类
     * @param gaMethodMatcher 方法匹配
     * @return 返回匹配的方法集合
     */
    Collection<GaMethod> searchClassGaMethods(Class<?> targetClass, Matcher<GaMethod> gaMethodMatcher);

}
