package com.github.ompc.greys.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * 反射工具类
 * Created by vlinux on 15/11/2.
 */
public class GaReflectUtils {


    /**
     * 递归获取目标类的ClassLoader<br/>
     * 因为JVM的ClassLoader采用双亲委派，所以按层次排序
     *
     * @param targetClass 目标类
     * @return ClassLoader层次列表(按层次排序，从近到远)
     */
    public static ArrayList<ClassLoader> recGetClassLoader(final Class<?> targetClass) {
        final ArrayList<ClassLoader> classLoaderList = new ArrayList<ClassLoader>();
        ClassLoader loader = targetClass.getClassLoader();
        if (null != loader) {
            classLoaderList.add(loader);
            while (true) {
                loader = loader.getParent();
                if (null == loader) {
                    break;
                }
                classLoaderList.add(loader);
            }
        }
        return classLoaderList;
    }


    /**
     * 递归获取目标类的父类<br/>
     * 因为Java的类继承关系是单父类的，所以按照层次排序
     *
     * @param targetClass 目标类
     * @return SuperClass层次列表(按层次排序，从近到远)
     */
    public static ArrayList<Class<?>> recGetSuperClass(final Class<?> targetClass) {
        final ArrayList<Class<?>> superClassList = new ArrayList<Class<?>>();
        Class<?> superClass = targetClass.getSuperclass();
        if (null != superClass) {
            superClassList.add(superClass);
            while (true) {
                superClass = superClass.getSuperclass();
                if (null == superClass) {
                    break;
                }
                superClassList.add(superClass);
            }//while
        }
        return superClassList;
    }

    /**
     * 计算ClassType
     *
     * @param targetClass 目标类
     * @return 计算出的ClassType
     */
    public static int computeClassType(Class<?> targetClass) {
        int type = 0;
        if (targetClass.isAnnotation())
            type |= TYPE_ANNOTATION;
        if (targetClass.isAnonymousClass())
            type |= TYPE_ANONYMOUS;
        if (targetClass.isArray())
            type |= TYPE_ARRAY;
        if (targetClass.isEnum())
            type |= TYPE_ENUM;
        if (targetClass.isInterface())
            type |= TYPE_INTERFACE;
        if (targetClass.isLocalClass())
            type |= TYPE_LOCAL;
        if (targetClass.isMemberClass())
            type |= TYPE_MEMBER;
        if (targetClass.isPrimitive())
            type |= TYPE_PRIMITIVE;
        if (targetClass.isSynthetic())
            type |= TYPE_SYNTHETIC;
        return type;
    }


    public static final int TYPE_ANNOTATION = 1 << 0;
    public static final int TYPE_ANONYMOUS = 1 << 1;
    public static final int TYPE_ARRAY = 1 << 2;
    public static final int TYPE_ENUM = 1 << 3;
    public static final int TYPE_INTERFACE = 1 << 4;
    public static final int TYPE_LOCAL = 1 << 5;
    public static final int TYPE_MEMBER = 1 << 6;
    public static final int TYPE_PRIMITIVE = 1 << 7;
    public static final int TYPE_SYNTHETIC = 1 << 8;

    /**
     * 默认类型(全匹配)
     */
    public static final int DEFAULT_TYPE =
            TYPE_ANNOTATION
                    | TYPE_ANONYMOUS | TYPE_ARRAY | TYPE_ENUM
                    | TYPE_INTERFACE | TYPE_LOCAL | TYPE_MEMBER
                    | TYPE_PRIMITIVE | TYPE_SYNTHETIC;


    /**
     * 计算类修饰符
     *
     * @param targetClass 目标类
     * @return 类修饰符
     */
    public static int computeModifier(final Class<?> targetClass) {
        return targetClass.getModifiers();
    }

    public static int computeModifier(final Method targetMethod) {
        return targetMethod.getModifiers();
    }

    public static int computeModifier(final Constructor<?> targetConstructor) {
        return targetConstructor.getModifiers();
    }

    public static int computeModifier(final Field targetField) {
        return targetField.getModifiers();
    }

    public static final int MOD_PUBLIC = Modifier.PUBLIC;
    public static final int MOD_PRIVATE = Modifier.PRIVATE;
    public static final int MOD_PROTECTED = Modifier.PROTECTED;
    public static final int MOD_STATIC = Modifier.STATIC;
    public static final int MOD_FINAL = Modifier.FINAL;
    public static final int MOD_SYNCHRONIZED = Modifier.SYNCHRONIZED;
    public static final int MOD_VOLATILE = Modifier.VOLATILE;
    public static final int MOD_TRANSIENT = Modifier.TRANSIENT;
    public static final int MOD_NATIVE = Modifier.NATIVE;
    public static final int MOD_ABSTRACT = Modifier.ABSTRACT;
    public static final int MOD_STRICT = Modifier.STRICT;

    /**
     * 默认匹配修饰符(全匹配)
     */
    public static final int DEFAULT_MOD =
            MOD_FINAL
                    | MOD_PROTECTED | MOD_VOLATILE | MOD_STATIC | MOD_PUBLIC | MOD_SYNCHRONIZED
                    | MOD_TRANSIENT | MOD_ABSTRACT | MOD_NATIVE | MOD_STRICT | MOD_PRIVATE;


    /**
     * 设置对象某个成员的值
     *
     * @param field  属性对象
     * @param value  属性值
     * @param target 目标对象
     * @throws IllegalArgumentException 非法参数
     * @throws IllegalAccessException   非法进入
     */
    public static void set(Field field, Object value, Object target) throws IllegalArgumentException, IllegalAccessException {
        final boolean isAccessible = field.isAccessible();
        try {
            field.setAccessible(true);
            field.set(target, value);
        } finally {
            field.setAccessible(isAccessible);
        }
    }

}
