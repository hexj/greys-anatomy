package com.github.ompc.greys.core.manager;

import com.github.ompc.greys.common.di.Impl;
import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.manager.impl.DefaultHandlerMetaDataManager;
import com.github.ompc.greys.protocol.Req;
import com.github.ompc.greys.protocol.Resp;

/**
 * 处理器信息管理
 * Created by vlinux on 15/10/26.
 */
@Impl(DefaultHandlerMetaDataManager.class)
public interface HandlerMetaDataManager {

    /**
     * 处理器MetaData信息
     */
    class MetaData {

        private final Class<? extends Handler> handlerClass;
        private final Class<? extends Req> reqClass;
        private final Class<? extends Resp> respClass;

        public MetaData(
                final Class<? extends Handler> handlerClass,
                final Class<? extends Req> reqClass,
                final Class<? extends Resp> respClass) {
            this.handlerClass = handlerClass;
            this.reqClass = reqClass;
            this.respClass = respClass;
        }

        /**
         * 获取处理器类型
         *
         * @return 处理器类型
         */
        public Class<? extends Handler> getHandlerClass() {
            return handlerClass;
        }

        /**
         * 获取处理器请求类型
         *
         * @return 处理器请求类型
         */
        public Class<? extends Req> getReqClass() {
            return reqClass;
        }

        /**
         * 获取处理器返回类型
         *
         * @return 处理器返回类型
         */
        public Class<? extends Resp> getRespClass() {
            return respClass;
        }

    }


    /**
     * 映射处理器
     *
     * @param type 请求类型
     * @return Handler的Meta信息
     */
    MetaData mapping(String type);


    class Factory {
        private final static HandlerMetaDataManager instance = new DefaultHandlerMetaDataManager();

        public static HandlerMetaDataManager getInstance() {
            return instance;
        }
    }

}
