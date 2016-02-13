package com.github.ompc.greys.server;

import java.net.InetSocketAddress;

/**
 * 服务端启动配置文件
 * Created by vlinux on 16/2/12.
 */
public class GaServerConfigure {

    /**
     * 默认WEBSOCKET路径
     */
    public static final String DEFAULT_WEBSOCKET_PATH = "/greys";

    // 服务端绑定地址
    private InetSocketAddress address;

    // websocket路径
    private String webSocketPath = DEFAULT_WEBSOCKET_PATH;

    // worker线程数
    private int workerNum;

    // handler线程数
    private int handlerNum;

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public String getWebSocketPath() {
        return webSocketPath;
    }

    public void setWebSocketPath(String webSocketPath) {
        this.webSocketPath = webSocketPath;
    }

    public int getWorkerNum() {
        return workerNum;
    }

    public void setWorkerNum(int workerNum) {
        this.workerNum = workerNum;
    }

    public int getHandlerNum() {
        return handlerNum;
    }

    public void setHandlerNum(int handlerNum) {
        this.handlerNum = handlerNum;
    }
}
