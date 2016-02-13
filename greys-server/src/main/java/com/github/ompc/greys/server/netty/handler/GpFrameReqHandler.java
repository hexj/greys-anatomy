package com.github.ompc.greys.server.netty.handler;

import com.github.ompc.greys.common.di.DiContainer;
import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.listener.InvokeListener;
import com.github.ompc.greys.core.manager.HandlerMetaDataManager;
import com.github.ompc.greys.core.server.Session;
import com.github.ompc.greys.core.util.LogUtil;
import com.github.ompc.greys.core.util.PointCut;
import com.github.ompc.greys.protocol.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Gp协议处理器
 * Created by vlinux on 16/2/12.
 */
public class GpFrameReqHandler extends SimpleChannelInboundHandler<GpFrameReq> implements Session {

    private final Logger logger = LogUtil.getLogger();
    private final ExecutorService handlerExecutorService;
    private final HandlerMetaDataManager handlerMetaDataManager = DiContainer.getInstance().get(HandlerMetaDataManager.class);
    private final BlockingQueue<GpFrameResp> asyncWritingQueue = new LinkedBlockingQueue<GpFrameResp>();

    // 帧请求集合
    private final ConcurrentHashMap<Integer, CountDownLatch> requests = new ConcurrentHashMap<Integer, CountDownLatch>();

    private volatile Channel channel;
    private volatile boolean isFlushing = true;

    public GpFrameReqHandler(ExecutorService handlerExecutorService) {
        this.handlerExecutorService = handlerExecutorService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();

        // 启动异步刷新线程
        final Thread fThread = new Thread(new Flushing(), "GREYS-HANDLER-ASYNC-FLUSHER");
        fThread.setDaemon(true);
        fThread.start();

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        isFlushing = false;
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GpFrameReq gpFrameReq) throws Exception {

        // 找到对应的帧处理器
        final HandlerMetaDataManager.MetaData metaData = handlerMetaDataManager.mapping(gpFrameReq.getType());
        if (null == metaData) {
            // 如果当前服务端没有实现这个请求，则认为是无效的帧类型
            throw new HandlerException(gpFrameReq.getId(), gpFrameReq.getType(), FrameStatusEnum.ILLEGAL_FRAME_TYPE);
        }

        // 调用帧处理
        onGpFrameReq(gpFrameReq, metaData.getHandlerClass());

    }

    private void writeGpFrameResp(final GpFrameResp gpFrameResp) {
        asyncWritingQueue.offer(gpFrameResp);
    }

    /*
     * 等待命令执行完成
     * 命令执行完成的标记是调用了Out.finish方法，或被强制关闭
     */
    private void waitingForFinish(final int id, final String type, final CountDownLatch countDown, final boolean hasFinish) {
        try {
            countDown.await();

            // 检查是否之前已经发过结束帧，如果尚未发则由此处代劳
            // 发一个空数据的结束帧，强制通知客户端当前处理已经结束
            if (!hasFinish) {
                writeGpFrameResp(new GpFrameResp(id, type, FrameStatusEnum.SUCCESS, true));
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void onGpFrameReq(final GpFrameReq gpFrameReq, final Class<? extends Handler> handlerClass) throws HandlerException {

        handlerExecutorService.execute(new Runnable() {
            @Override
            public void run() {

                final int id = gpFrameReq.getId();
                final String type = gpFrameReq.getType();

                // 检查帧序号是否已经存在于请求列表中
                // 如果已经存在则返回正在处理中
                if (requests.containsKey(id)) {
                    writeGpFrameResp(new GpFrameResp(id, type, FrameStatusEnum.HANDING, true));
                    return;
                }

                // 构造CountDown
                final CountDownLatch countDown;
                final CountDownLatch oCountDown = new CountDownLatch(1);
                if (null != requests.putIfAbsent(id, oCountDown)) {
                    // 这里可能存在并发写入同一个帧序号
                    // 所以需要利用putIfAbsent再次进行校验
                    // 如果请求已经在处理，则直接打日志走掉
                    logger.info("frame-req={} already handing, ignore this request.", gpFrameReq);
                    writeGpFrameResp(new GpFrameResp(id, type, FrameStatusEnum.HANDING, true));
                    return;
                } else {
                    countDown = oCountDown;
                }

                // 是否已发送过结束帧
                // 某些主动结束的处理器会发出结束帧，但有些处理器是需要强制结束的，这部分处理器的结束帧由GpFrameReqHandler代劳
                // 所以需要一个标志变量来区分这两种情况
                final AtomicBoolean hasFinishRef = new AtomicBoolean(false);

                final Req req = gpFrameReq.getBody();
                try {

                    // 输出器
                    final Handler.Out out = new Handler.Out() {

                        @Override
                        public Handler.Out out(Resp resp) {

                            // 发送帧报文
                            _out(resp, false);
                            return this;
                        }

                        @Override
                        public void finish(Resp resp) {

                            // 发送结束帧报文
                            _out(resp, true);

                            // 标记结束帧已经发过了
                            hasFinishRef.set(true);

                            finish();
                        }

                        private void _out(Resp resp, boolean isFinish) {
                            final GpFrameResp frameResp = new GpFrameResp(id, type, FrameStatusEnum.SUCCESS, isFinish);
                            frameResp.setBody(resp);
                            writeGpFrameResp(frameResp);
                        }

                        @Override
                        public void finish() {
                            countDown.countDown();
                        }

                    };

                    // 实例化处理器类
                    final Handler handler = handlerClass.newInstance();

                    // 处理器初始化
                    handler.init(id, GpFrameReqHandler.this);
                    logger.debug("frame-req[id={};type={};]'s handler process.", id, type);

                    try {

                        // 处理器处理请求
                        final LinkedHashMap<InvokeListener, ArrayList<PointCut>> listenerMap = handler.handle(req, out);
                        logger.debug("frame-req[id={};type={};]'s handler handle.", id, type);

                        // 等待命令完成
                        waitingForFinish(id, type, countDown, hasFinishRef.get());

                    } finally {

                        // 处理器销毁
                        // 如论如何处理器的销毁在初始化成功之后是已定要调用
                        handler.destroy();
                        logger.debug("frame-req[id={};type={};]'s handler destroyProcess.", id, type);

                    }

                } catch (Throwable t) {
                    logger.warn("frame-req[id={};type={};] handle failed.", id, type, t);
                    writeGpFrameResp(new GpFrameResp(id, type, FrameStatusEnum.HANDLER_ERROR, true));
                    return;
                } finally {
                    // 命令处理完之后要记得擦屁股
                    requests.remove(id);
                }

                logger.debug("frame-req[id={};type={};] handle finished. ", id, type);

            }
        });

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof HandlerException) {
            final HandlerException he = (HandlerException) cause;
            ctx.channel().write(new GpFrameResp(he.getId(), he.getType(), he.getStatus(), true));
            logger.warn("channel={} handing frame[id={};type={};] fail, status={};",
                    ctx.channel(), he.getId(), he.getType(), he.getStatus());
        } else {
            logger.warn("channel={} handing failed.", ctx.channel(), cause);
        }

    }

    @Override
    public void terminate(int targetId) {
        final CountDownLatch countDown = requests.remove(targetId);
        if (null != countDown) {
            countDown.countDown();
            logger.info("wakeup handler for terminate. target_id={};", targetId);
        }
    }

    @Override
    public void closeSession() {
        try {
            channel.close().sync();
        } catch (InterruptedException e) {
            logger.warn("{} close failed.", channel, e);
        }
    }

    /**
     * 处理异常
     */
    class HandlerException extends Exception {

        private final int id;
        private final String type;
        private final FrameStatusEnum status;

        /**
         * 构造帧异常
         *
         * @param id     帧ID
         * @param type   帧类型
         * @param status 应答状态
         * @param cause  异常原因
         */
        public HandlerException(int id, String type, FrameStatusEnum status, Throwable cause) {
            super(cause);
            this.id = id;
            this.type = type;
            this.status = status;
        }

        /**
         * 构造帧异常
         *
         * @param id     帧ID
         * @param type   帧类型
         * @param status 应答状态
         */
        public HandlerException(int id, String type, FrameStatusEnum status) {
            this.id = id;
            this.type = type;
            this.status = status;
        }

        public int getId() {
            return id;
        }

        public FrameStatusEnum getStatus() {
            return status;
        }

        public String getType() {
            return type;
        }

    }


    /**
     * 异步刷新
     */
    class Flushing implements Runnable {

        @Override
        public void run() {

            while (isFlushing) {
                // 等待获取等着刷新
                try {

                    // 从队列获取待发送报文
                    final GpFrameResp gpFrameResp = asyncWritingQueue.poll(500L, TimeUnit.MILLISECONDS);

                    if (null == gpFrameResp) {
                        // 超时 & 推空保护
                        continue;
                    }

                    if (!channel.isWritable()) {
                        logger.debug("{} was not writable, drop {}", channel, gpFrameResp);
                        continue;
                    }

                    channel.write(gpFrameResp);

                    // 当获取到队列最后一个报文后强制刷新
                    if (asyncWritingQueue.isEmpty()) {
                        channel.flush();
                    }

                } catch (InterruptedException e) {
                    // ignore
                }

            }//while

        }

    }

}
