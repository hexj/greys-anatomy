package com.github.ompc.greys.common.di;

import com.github.ompc.greys.common.util.GaReflectUtils;
import com.github.ompc.greys.common.util.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.ompc.greys.common.util.GaClassUtils.filterByAnnotation;
import static com.github.ompc.greys.common.util.GaClassUtils.scanPackage;

/**
 * DI容器
 * Created by vlinux on 16/2/2.
 */
public class DiContainer {

    private final Logger logger = LoggerFactory.getLogger(DiContainer.class);
    private final Map<Class<?>, Object> objectMap = new ConcurrentHashMap<Class<?>, Object>();
    private final Initializer initializer = new Initializer();

    private DiContainer() {
    }

    /**
     * 初始化所有实现类
     */
    private void initImpl() {
        for (Class<?> clazz :
                filterByAnnotation(
                        scanPackage(
                                DiContainer.class.getClassLoader(),
                                "com.github.ompc.greys"
                        ),
                        Impl.class
                )) {

            if (!clazz.isInterface()) {
                continue;
            }
            final Class<?> implClass = clazz.getAnnotation(Impl.class).value();

            try {
                final Object implObject = implClass.newInstance();
                objectMap.put(clazz, implObject);
                logger.info("DI success, interface:{} found implClass:{};impl:{}", clazz, implClass, implObject);
            } catch (Throwable t) {
                logger.warn("DI failed, interface:{} found impl:{}", clazz, implClass, t);
            }

        }
    }

    /**
     * 注入依赖
     */
    private void autoInject() {
        for (Map.Entry<Class<?>, Object> entry : objectMap.entrySet()) {
            final Object implObject = entry.getValue();
            final Class<?> implClazz = implObject.getClass();
            final Field[] fieldArray = implObject.getClass().getDeclaredFields();
            if (null == fieldArray) {
                continue;
            }
            for (Field field : fieldArray) {
                if (null != field.getAnnotation(Inject.class)) {
                    final Object injectTargetObject = objectMap.get(field.getType());
                    if (null == injectTargetObject) {
                        logger.warn("DI inject failed, inject impl not found. implClass:{};impl:{};field:{};",
                                implClazz,
                                implObject,
                                field.getName()
                        );
                        continue;
                    }
                    try {
                        GaReflectUtils.set(field, injectTargetObject, implObject);
                        logger.info("DI inject success, implClass:{};impl:{};field:{};injectType:{};injectObject:{}",
                                implClazz,
                                implObject,
                                field.getName(),
                                field.getType(),
                                injectTargetObject
                        );
                    } catch (IllegalAccessException e) {
                        logger.info("DI inject failed, implClass:{};impl:{};field:{};injectType:{};injectObject:{}",
                                implClazz,
                                implObject,
                                field.getName(),
                                field.getType(),
                                injectTargetObject,
                                e
                        );
                    }
                }
            }
        }

    }

    /**
     * 初始化DI容器
     *
     * @throws DiException
     */
    public void init() throws DiException {
        try {
            initializer.initProcess(new Initializer.Processor() {
                @Override
                public void process() throws Throwable {
                    initImpl();
                    autoInject();
                    logger.info("DI finished. object.count={}", objectMap.size());
                }
            });
        } catch (Throwable t) {
            logger.warn("DI container init failed.", t);
        }
    }

    /**
     * 判断DI容器是否已经被初始化
     *
     * @return DI容器是否已经被初始化
     */
    public boolean isInit() {
        return initializer.isInitialized();
    }

    /**
     * 获取托管对象
     *
     * @param interfaceClass 目标接口
     * @param <T>            类型
     * @return 托管对象
     */
    public <T> T get(Class<?> interfaceClass) {
        // DI容器必须先启动才能从容器中获取数据
        if (!initializer.isInitialized()) {
            throw new IllegalStateException("DI container was not init yet.");
        }
        return (T) objectMap.get(interfaceClass);
    }

    /**
     * 定义托管对象
     *
     * @param interfaceClass 目标接口
     * @param impl           托管对象
     * @return this
     */
    public DiContainer define(Class<?> interfaceClass, Object impl) {
        // 如果容器已经启动,则不允许启动后的容器定义类实现了
        // 只允许启动之前定义
        if (!initializer.isNew()) {
            throw new IllegalStateException("DI container was init already.");
        }
        objectMap.put(interfaceClass, impl);
        return this;
    }


    private final static DiContainer container = new DiContainer();

    public static DiContainer getInstance() {
        return container;
    }

}
