package com.github.ompc.greys.server;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

/**
 * Greys服务端接口定义
 * Created by vlinux on 16/2/12.
 */
public interface GaServer {

    /**
     * 判断服务器是否已经绑定端口
     *
     * @return 服务器是否已经绑定端口
     */
    boolean isBind();

    /**
     * 服务器绑定端口
     *
     * @param configure 配置信息
     * @param inst      inst
     * @throws IOException 绑定失败
     */
    void bind(GaServerConfigure configure, Instrumentation inst) throws IOException;


    /**
     * 服务器解除端口绑定
     *
     * @throws IOException 解除绑定失败
     */
    void unbind() throws IOException;

}
