package com.github.ompc.greys.server.netty.handler;

import com.github.ompc.greys.core.util.LogUtil;
import com.github.ompc.greys.protocol.GpFrame;
import com.github.ompc.greys.protocol.GpFrameResp;
import com.github.ompc.greys.protocol.serializer.FrameSerializer;
import com.github.ompc.greys.protocol.serializer.FrameSerializerException;
import com.github.ompc.greys.protocol.serializer.GsonFrameSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.List;

/**
 * Gp帧解码器(服务端)
 * Created by vlinux on 16/2/13.
 */
public class GpFrameServerCodec extends MessageToMessageCodec<TextWebSocketFrame, GpFrame> {

    private final Logger logger = LogUtil.getLogger();
    private final FrameSerializer frameSerializer = new GsonFrameSerializer();

    @Override
    protected void encode(ChannelHandlerContext ctx, GpFrame gpFrame, List<Object> out) throws Exception {

        if (!(gpFrame instanceof GpFrameResp)) {
            logger.warn("channel={} ignore encode, because {} not a frame-resp.", ctx.channel(), gpFrame);
            return;
        }

        final String frameRespJson = frameSerializer.serializeFrameResp((GpFrameResp) gpFrame);
        out.add(new TextWebSocketFrame(frameRespJson));
        logger.debug("channel={} encode {}->{} success.", ctx.channel(), gpFrame, frameRespJson);

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame, List<Object> out) throws Exception {

        final String json = textWebSocketFrame.text();

        if (StringUtils.isBlank(json)) {
            logger.debug("channel={} decode fail, because receive a blank ws-frame, ignore this.", ctx.channel());
            return;
        }

        final GpFrame gpFrame = frameSerializer.deserializeFrameReq(json);
        out.add(gpFrame);
        logger.debug("channel={} decode {}->{} success.", ctx.channel(), json, gpFrame);

    }

    /*
     * 应答FrameSerializerException异常<br/>
     * 应答的方式是发送一个错误应答帧给客户端
     */
    private void responseFrameSerializerException(final ChannelHandlerContext ctx, final FrameSerializerException fse) {
        final GpFrameResp frameResp = new GpFrameResp(fse.getId(), fse.getType(), fse.getStatus(), true);
        try {
            ctx.channel().write(new TextWebSocketFrame(frameSerializer.serializeFrameResp(frameResp)));
        } catch (FrameSerializerException cause) {
            // 如果到了这一步还序列化失败,只能打日志了
            logger.warn("channel={} serialize {} fse failed.", ctx.channel(), frameResp, cause);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        if (cause instanceof FrameSerializerException) {
            final FrameSerializerException fse = (FrameSerializerException) cause;
            responseFrameSerializerException(ctx, fse);
            logger.warn("channel={} frame-server-codec failed, id={};type={};status={};",
                    ctx.channel(), fse.getId(), fse.getType(), fse.getStatus(), fse);
        } else {
            logger.warn("channel={} frame-server-codec failed.", ctx.channel(), cause);
        }

    }
}
