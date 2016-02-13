package com.github.ompc.greys.core.handler;

import com.github.ompc.greys.core.util.PointCut;
import com.github.ompc.greys.core.listener.InvokeListener;
import com.github.ompc.greys.core.server.Session;
import com.github.ompc.greys.protocol.Req;
import com.github.ompc.greys.protocol.Resp;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 处理器接口
 *
 * @param <REQ>  请求类型
 * @param <RESP> 应答类型
 */
public interface Handler<REQ extends Req, RESP extends Resp> {

    /**
     * 返回报文输出器
     */
    interface Out<RESP extends Resp> {

        /**
         * 输出返回报文
         *
         * @param resp 应答报文
         * @return this
         */
        Out out(RESP resp);

        /**
         * 输出结束报文
         *
         * @param resp 结束应答报文
         */
        void finish(RESP resp);

        /**
         * 结束返回报文
         */
        void finish();

    }


    /**
     * 初始化
     *
     * @param id      请求序号
     * @param session 会话
     * @throws Throwable 初始化出错
     */
    void init(int id, Session session) throws Throwable;

    /**
     * 处理
     *
     * @param req 请求报文
     * @param out 应答报文输出
     * @return 切入点
     * @throws Throwable 处理出错
     */
    LinkedHashMap<InvokeListener, ArrayList<PointCut>> handle(REQ req, Out<RESP> out) throws Throwable;

    /**
     * 销毁
     *
     * @throws Throwable 销毁出错
     */
    void destroy();

}
