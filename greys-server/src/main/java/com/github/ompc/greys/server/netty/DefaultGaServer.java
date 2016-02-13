package com.github.ompc.greys.server.netty;

import com.github.ompc.greys.common.util.Initializer;
import com.github.ompc.greys.common.util.NamedThreadFactory;
import com.github.ompc.greys.core.util.LogUtil;
import com.github.ompc.greys.server.GaServer;
import com.github.ompc.greys.server.GaServerConfigure;
import com.github.ompc.greys.server.netty.handler.GpFrameReqHandler;
import com.github.ompc.greys.server.netty.handler.GpFrameServerCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * GreysServer默认实现
 * Created by vlinux on 16/2/12.
 */
public class DefaultGaServer implements GaServer {

    private final Logger logger = LogUtil.getLogger();
    private final Initializer initializer = new Initializer(true);

    private GaServerConfigure configure;
    private ExecutorService handlerExecutorService;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private Channel serverChannel;

    @Override
    public boolean isBind() {
        return initializer.isInitialized();
    }

    @Override
    public void bind(final GaServerConfigure configure, final Instrumentation inst) throws IOException {
        try {
            initializer.initProcess(new Initializer.Processor() {
                @Override
                public void process() throws Throwable {

                    // 启动处理器线程池
                    handlerExecutorService = Executors.newFixedThreadPool(
                            configure.getHandlerNum(),
                            new NamedThreadFactory("GREYS-HANDLER", true)
                    );

                    final ServerBootstrap serverBootstrap = new ServerBootstrap()
                            .group(
                                    bossGroup = new NioEventLoopGroup(
                                            1,
                                            new NamedThreadFactory("GREYS-NETTY-BOSS-GROUP")
                                    ),
                                    workGroup = new NioEventLoopGroup(
                                            configure.getWorkerNum(),
                                            new NamedThreadFactory("GREYS-NETTY-WORKER-GROUP")
                                    )
                            )
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    final ChannelPipeline pipeline = socketChannel.pipeline();
                                    pipeline
                                            .addLast(new HttpServerCodec())
                                            .addLast(new HttpObjectAggregator(64 * 1024))
                                            .addLast(new WebSocketServerProtocolHandler(configure.getWebSocketPath()))
                                            .addLast(new GpFrameServerCodec())
                                            .addLast(new GpFrameReqHandler(handlerExecutorService))
                                    ;
                                }
                            })
                            .option(ChannelOption.SO_KEEPALIVE, true)
                            .option(ChannelOption.TCP_NODELAY, true);

                    serverChannel = serverBootstrap.bind(configure.getAddress()).sync().channel();
                    logger.info("{} bind success. address={}", this, configure.getAddress());

                }
            });
        } catch (Throwable cause) {
            logger.warn("{} bind failed. address={}", this, configure.getAddress(), cause);
            throwIOException(cause);
        }
    }


    @Override
    public void unbind() throws IOException {
        try {
            initializer.destroyProcess(new Initializer.Processor() {
                @Override
                public void process() throws Throwable {

                    // shutdown handler-service
                    if (null != handlerExecutorService
                            && !handlerExecutorService.isShutdown()
                            && !handlerExecutorService.isTerminated()) {
                        handlerExecutorService.shutdown();
                        logger.debug("{} handler-executor-service was shutdown.", this);
                    }

                    try {
                        // shutdown ServerChannel
                        if (null != serverChannel) {
                            serverChannel.closeFuture().sync();
                        }
                        logger.debug("server-channel close success.");
                    } finally {
                        // shutdown group
                        if (null != bossGroup) {
                            bossGroup.shutdownGracefully();
                        }
                        if (null != workGroup) {
                            workGroup.shutdownGracefully();
                        }
                        logger.debug("groups(boss,work) shutdown success.");
                    }
                    logger.info("{} unbind success. address={}", this, configure.getAddress());

                }
            });
        } catch (Throwable cause) {
            logger.warn("{} unbind failed. address={}", this, configure.getAddress(), cause);
            throwIOException(cause);
        }
    }


    private void throwIOException(final Throwable cause) throws IOException {
        if (cause instanceof IOException) {
            throw (IOException) cause;
        } else {
            throw new IOException(cause);
        }
    }


    public static void main(String... args) throws IOException {

        final GaServerConfigure configure = new GaServerConfigure();
        configure.setAddress(new InetSocketAddress("127.0.0.1", 3658));
        configure.setWebSocketPath("/greys");
        final GaServer gaServer = new DefaultGaServer();
        gaServer.bind(configure, null);

        System.in.read();

    }

}
