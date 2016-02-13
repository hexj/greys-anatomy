package com.github.ompc.greys.core.server;

/**
 * 服务端会话
 * Created by oldmanpushcart@gmail.com on 15/5/2.
 */
public interface Session {

    /**
     * 终结当前会话中的任意一个请求
     *
     * @param targetId 目标请求ID
     */
    void terminate(final int targetId);

    /**
     * 关闭会话
     */
    void closeSession();

}