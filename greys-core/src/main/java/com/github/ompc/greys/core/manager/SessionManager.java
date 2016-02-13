package com.github.ompc.greys.core.manager;

/**
 * 会话管理
 * Created by oldmanpushcart@gmail.com on 15/5/2.
 */
public interface SessionManager {

    /**
     * 服务端会话
     * Created by oldmanpushcart@gmail.com on 15/5/2.
     */
    interface Session {

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

    /**
     * 创建一个会话
     *
     * @return 创建的会话
     */
    Session newSession(final Session session);

    /**
     * 获取一个会话
     *
     * @param sessionId 会话ID
     * @return 返回会话
     */
    Session getSession(int sessionId);

    /**
     * 关闭所有会话
     */
    void clean();

    /**
     * 是否已经被销毁
     *
     * @return true/false
     */
    boolean isDestroy();

    /**
     * 销毁会话管理器所管理的所有会话
     */
    void destroy();

}
