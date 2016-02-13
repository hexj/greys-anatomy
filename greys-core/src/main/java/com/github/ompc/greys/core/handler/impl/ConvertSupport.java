package com.github.ompc.greys.core.handler.impl;

import com.github.ompc.greys.common.di.DiContainer;
import com.github.ompc.greys.common.util.GaReflectUtils;
import com.github.ompc.greys.core.manager.ReflectManager;
import com.github.ompc.greys.core.util.GaMethod;
import com.github.ompc.greys.core.util.matcher.ClassMatcher;
import com.github.ompc.greys.core.util.matcher.GaMethodMatcher;
import com.github.ompc.greys.core.util.matcher.Matcher;
import com.github.ompc.greys.core.util.matcher.PatternMatcher;
import com.github.ompc.greys.protocol.body.SearchClassReq;
import com.github.ompc.greys.protocol.body.info.*;
import com.github.ompc.greys.protocol.body.matching.ClassMatching;
import com.github.ompc.greys.protocol.body.matching.MethodMatching;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.CodeSource;
import java.util.*;

import static com.github.ompc.greys.common.util.GaReflectUtils.*;

/**
 * 转换支持
 * Created by vlinux on 15/11/2.
 */
public abstract class ConvertSupport extends CycleSupport {

    private final ReflectManager reflectManager = DiContainer.getInstance().get(ReflectManager.class);

    final protected Collection<Class<?>> matchingClasses(
            final ClassMatching classMatching, final boolean isIncludeSubClasses) {
        final Set<Class<?>> classSet = new LinkedHashSet<Class<?>>();
        if (null == classMatching) {
            return classSet;
        }

        // 搜索所有匹配器需求
        // 搜索当前匹配器所匹配的类
        final Collection<Class<?>> matchedClasses = reflectManager.searchClass(toMatcher(classMatching));
        classSet.addAll(matchedClasses);

        // 如果要求搜索子类，则需要继续添加
        if (isIncludeSubClasses) {
            for (final Class<?> matchedClass : matchedClasses) {
                classSet.addAll(reflectManager.searchSubClass(matchedClass));
            }
        }

        return classSet;
    }


    final protected Collection<GaMethod> matchingGaMethod(
            final ClassMatching classMatching,
            final boolean isIncludeSubClasses,
            final MethodMatching methodMatching) {


        final Matcher<GaMethod> gaMethodMatcher = toMatcher(methodMatching);
        final Set<GaMethod> matchedMethodSet = new LinkedHashSet<GaMethod>();

        final Collection<Class<?>> matchedClassSet = matchingClasses(classMatching, isIncludeSubClasses);
        for (Class<?> matchedClass : matchedClassSet) {
            matchedMethodSet.addAll(reflectManager.searchClassGaMethods(matchedClass, gaMethodMatcher));
        }

        return matchedMethodSet;
    }

    /**
     * 转换为模式匹配器
     *
     * @param pattern 模式匹配
     * @return 模式匹配器
     */
    private Matcher<String> toMatcher(final String pattern) {
        return new PatternMatcher(
                PatternMatcher.Strategy.REGEX,
                pattern
        );
    }

    /**
     * 转换为类匹配器
     *
     * @param classMatching 类匹配
     * @return 类匹配器
     */
    private Matcher<Class<?>> toMatcher(final ClassMatching classMatching) {
        return new ClassMatcher(
                classMatching.getModifier(),
                classMatching.getType(),
                toMatcher(classMatching.getPattern()),
                null
        );
    }

    private List<Matcher<Class<?>>> toMatchers(final Collection<ClassMatching> classMatchingList) {
        if (null == classMatchingList) {
            return null;
        }
        final List<Matcher<Class<?>>> classMatcherList = new ArrayList<Matcher<Class<?>>>();
        for (ClassMatching classMatching : classMatchingList) {
            classMatcherList.add(toMatcher(classMatching));
        }
        return classMatcherList;
    }

    /**
     * 转换为方法匹配器
     *
     * @param methodMatching 方法匹配
     * @return 方法匹配器
     */
    private Matcher<GaMethod> toMatcher(final MethodMatching methodMatching) {
        return new GaMethodMatcher(
                methodMatching.getModifier(),
                toMatcher(methodMatching.getPattern()),
                toMatchers(methodMatching.getParameters()),
                null
        );
    }

    /**
     * 转换为TypeInfo
     *
     * @param clazz 目标类
     * @return TypeInfo
     */
    public final TypeInfo toTypeInfo(final Class<?> clazz) {
        final TypeInfo typeInfo = new TypeInfo();
        typeInfo.setName(clazz.getName());
        typeInfo.setCodeSource(getCodeSource(clazz));
        typeInfo.setModifier(clazz.getModifiers());
        typeInfo.setType(computeClassType(clazz));
        return typeInfo;
    }

    /**
     * 转换为ClassInfo
     *
     * @param clazz 目标类
     * @param req   搜索类请求
     * @return ClassInfo
     */
    public final ClassInfo toClassInfo(final Class<?> clazz, final SearchClassReq req) {
        return toClassInfo(
                clazz,
                req.isIncludeInterfaces(),
                req.isIncludeAnnotations(),
                req.isIncludeSuperClasses(),
                req.isIncludeClassLoaders(),
                req.isIncludeFields()
        );
    }

    /**
     * 转换为ClassInfo
     *
     * @param clazz                 目标类
     * @param isIncludeInterfaces   是否包含接口
     * @param isIncludeAnnotations  是否包含元注释
     * @param isIncludeSuperClasses 是否包含父类集合
     * @param isIncludeClassLoaders 是否包含ClassLoader集合
     * @param isIncludeFields       是否包含属性集合
     * @return ClassInfo
     */
    public final ClassInfo toClassInfo(
            final Class<?> clazz,
            final boolean isIncludeInterfaces,
            final boolean isIncludeAnnotations,
            final boolean isIncludeSuperClasses,
            final boolean isIncludeClassLoaders,
            final boolean isIncludeFields) {

        final ClassInfo classInfo = new ClassInfo();

        // process type-info
        classInfo.setName(clazz.getName());
        classInfo.setCodeSource(getCodeSource(clazz));
        classInfo.setModifier(clazz.getModifiers());
        classInfo.setType(computeClassType(clazz));

        classInfo.setInterfaces(isIncludeInterfaces
                ? toInterfaceTypeInfos(clazz)
                : null);

        classInfo.setAnnotations(isIncludeAnnotations
                ? toAnnotationTypeInfos(clazz)
                : null);

        classInfo.setSuperClasses(isIncludeSuperClasses
                ? toSuperClassTypeInfos(clazz)
                : null);

        classInfo.setClassLoaders(isIncludeClassLoaders
                ? toClassLoaderObjectInfos(clazz)
                : null);

        classInfo.setFieldInfos(isIncludeFields
                ? toFieldInfos(clazz)
                : null);

        return classInfo;
    }

    private String getCodeSource(final Class<?> clazz) {
        final CodeSource cs = clazz.getProtectionDomain().getCodeSource();
        if (null == cs
                || null == cs.getLocation()
                || null == cs.getLocation().getFile()) {
            return "";
        }
        return cs.getLocation().getFile();
    }

    private Collection<TypeInfo> toInterfaceTypeInfos(final Class<?> clazz) {
        final Class<?>[] interfaceArray = clazz.getInterfaces();
        if (null == interfaceArray) {
            return null;
        }
        final List<TypeInfo> interfaceTypeInfos = new ArrayList<TypeInfo>();
        for (Class<?> interfaceClass : interfaceArray) {
            interfaceTypeInfos.add(toTypeInfo(interfaceClass));
        }
        return interfaceTypeInfos;
    }

    private Collection<TypeInfo> toAnnotationTypeInfos(final Class<?> clazz) {
        return toAnnotationTypeInfos(clazz.getAnnotations());
    }

    private Collection<TypeInfo> toAnnotationTypeInfos(final Annotation[] annotationArray) {
        if (null == annotationArray) {
            return null;
        }
        final List<TypeInfo> annotationTypeInfos = new ArrayList<TypeInfo>();
        for (Annotation annotation : annotationArray) {
            annotationTypeInfos.add(toTypeInfo(annotation.getClass()));
        }
        return annotationTypeInfos;
    }

    private Collection<TypeInfo> toSuperClassTypeInfos(final Class<?> clazz) {
        final List<Class<?>> superClassList = recGetSuperClass(clazz);
        final List<TypeInfo> superClassTypeInfos = new ArrayList<TypeInfo>();
        for (Class<?> superClass : superClassList) {
            superClassTypeInfos.add(toTypeInfo(superClass));
        }
        return superClassTypeInfos;
    }

    private Collection<ObjectInfo> toClassLoaderObjectInfos(final Class<?> clazz) {
        final List<ClassLoader> classLoaderList = recGetClassLoader(clazz);
        final List<ObjectInfo> classLoaderObjectInfos = new ArrayList<ObjectInfo>();
        for (ClassLoader classLoader : classLoaderList) {
            classLoaderObjectInfos.add(toObjectInfo(classLoader));
        }
        return classLoaderObjectInfos;
    }

    private Collection<FieldInfo> toFieldInfos(final Class<?> clazz) {
        final Field[] fieldArray = clazz.getDeclaredFields();
        if (null == fieldArray) {
            return null;
        }
        final List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
        for (Field field : fieldArray) {
            fieldInfos.add(toFieldInfo(field));
        }
        return fieldInfos;
    }


    /**
     * 转换为ObjectInfo
     *
     * @param obj 目标对象
     * @return ObjectInfo
     */
    public final ObjectInfo toObjectInfo(final Object obj) {
        final ObjectInfo objectInfo = new ObjectInfo();
        objectInfo.setToString(null == obj ? "null" : obj.toString());
        return objectInfo;
    }

    /**
     * 转换为FieldInfo
     *
     * @param field 目标Field
     * @return FieldInfo
     */
    public final FieldInfo toFieldInfo(final Field field) {
        final FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setModifier(GaReflectUtils.computeModifier(field));
        fieldInfo.setName(field.getName());
        fieldInfo.setReturnType(toTypeInfo(field.getType()));
        fieldInfo.setAnnotations(toAnnotationTypeInfo(field));
        fieldInfo.setValue(toValueObjectInfo(field));
        return fieldInfo;
    }

    private ObjectInfo toValueObjectInfo(final Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            try {
                return toObjectInfo(field.get(null));
            } catch (Throwable t) {
                return null;
            }
        } else {
            return null;
        }
    }

    private TypeInfo[] toAnnotationTypeInfo(final Field field) {
        final Annotation[] annotationArray = field.getAnnotations();
        if (null == annotationArray) {
            return null;
        }
        final TypeInfo[] annotationTypeInfoArray = new TypeInfo[annotationArray.length];
        for (int index = 0; index < annotationArray.length; index++) {
            annotationTypeInfoArray[index] = toTypeInfo(annotationArray[index].getClass());
        }
        return annotationTypeInfoArray;
    }

    /**
     * 转换为MethodInfo
     *
     * @param gaMethod 目标类
     * @return MethodInfo
     */
    public final MethodInfo toMethodInfo(final GaMethod gaMethod) {
        final MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDeclaringClass(toTypeInfo(gaMethod.getDeclaringClass()));
        methodInfo.setReturnClass(toTypeInfo(gaMethod.getReturnType()));
        methodInfo.setModifier(gaMethod.getModifiers());
        methodInfo.setName(gaMethod.getName());
        methodInfo.setAnnotations(toAnnotationTypeInfos(gaMethod));
        methodInfo.setParameters(toParameterTypeInfos(gaMethod));
        methodInfo.setExceptions(toExceptionTypeInfos(gaMethod));
        return methodInfo;
    }

    private Collection<TypeInfo> toAnnotationTypeInfos(final GaMethod gaMethod) {
        return toAnnotationTypeInfos(gaMethod.getAnnotations());
    }

    private Collection<TypeInfo> toParameterTypeInfos(final GaMethod gaMethod) {
        final Class<?>[] parameterClassArray = gaMethod.getParameterTypes();
        if (null == parameterClassArray) {
            return null;
        }
        final List<TypeInfo> parameterTypeInfos = new ArrayList<TypeInfo>();
        for (Class<?> parameterClass : parameterClassArray) {
            parameterTypeInfos.add(toTypeInfo(parameterClass));
        }
        return parameterTypeInfos;
    }

    private Collection<TypeInfo> toExceptionTypeInfos(final GaMethod gaMethod) {
        final Class<?>[] exceptionClassArray = gaMethod.getExceptionTypes();
        if (null == exceptionClassArray) {
            return null;
        }
        final List<TypeInfo> exceptionTypeInfos = new ArrayList<TypeInfo>();
        for (Class<?> exceptionClass : exceptionClassArray) {
            exceptionTypeInfos.add(toTypeInfo(exceptionClass));
        }
        return exceptionTypeInfos;
    }

}
