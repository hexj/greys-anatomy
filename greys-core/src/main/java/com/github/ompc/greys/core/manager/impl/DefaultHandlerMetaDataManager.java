package com.github.ompc.greys.core.manager.impl;

import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.handler.Type;
import com.github.ompc.greys.core.manager.HandlerMetaDataManager;
import com.github.ompc.greys.core.util.LogUtil;
import com.github.ompc.greys.protocol.Req;
import com.github.ompc.greys.protocol.Resp;
import org.slf4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import static com.github.ompc.greys.common.util.GaClassUtils.*;

/**
 * 默认处理器信息管理
 * Created by vlinux on 15/10/26.
 */
public class DefaultHandlerMetaDataManager implements HandlerMetaDataManager {

    private final Logger logger = LogUtil.getLogger();
    private final Map<String/*TYPE*/, MetaData> metaDataMapping = new HashMap<String, MetaData>();

    public DefaultHandlerMetaDataManager() {

        for (Class<?> clazz :
                filterByAnnotation(
                        filterByParentClass(
                                scanPackage(
                                        getClass().getClassLoader(),
                                        "com.github.ompc.greys.core.handler.impl"
                                ),
                                Handler.class
                        ),
                        Type.class
                )) {

            final Class<? extends Handler> handlerClass = (Class<? extends Handler>) clazz;
            final ParameterizedType pType = getHandlerType(handlerClass);

            if (null == pType) {
                continue;
            }

            final java.lang.reflect.Type[] params = getHandlerType(handlerClass).getActualTypeArguments();
            final Class<? extends Req> reqClass = (Class<? extends Req>) params[0];
            final Class<? extends Resp> respClass = (Class<? extends Resp>) params[1];
            final String type = handlerClass.getAnnotation(Type.class).value();
            final MetaData metaData = new MetaData(handlerClass, reqClass, respClass);

            metaDataMapping.put(type, metaData);
        }

        logger.info("load handler : {}", metaDataMapping);

    }

    private ParameterizedType getHandlerType(final Class<? extends Handler> handlerClass) {
        final java.lang.reflect.Type[] gTypeArray = handlerClass.getGenericInterfaces();
        if (null != gTypeArray) {
            for (java.lang.reflect.Type gType : gTypeArray) {
                if (gType instanceof ParameterizedType) {
                    final ParameterizedType pType = (ParameterizedType) gType;
                    final java.lang.reflect.Type rType = pType.getRawType();
                    if (null != rType
                            && rType.equals(Handler.class)) {
                        return pType;
                    }
                }
            }
        }
        return null;
    }


    @Override
    public MetaData mapping(String type) {
        return metaDataMapping.get(type);
    }

}
