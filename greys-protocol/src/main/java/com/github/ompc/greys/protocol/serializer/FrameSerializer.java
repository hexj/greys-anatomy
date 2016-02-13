package com.github.ompc.greys.protocol.serializer;

import com.github.ompc.greys.protocol.GpFrameReq;
import com.github.ompc.greys.protocol.GpFrameResp;

/**
 * 帧序列化
 * Created by oldmanpushcart@gmail.com on 15/11/8.
 */
public interface FrameSerializer {

    /**
     * 反序列化请求帧
     *
     * @param json JSON报文(FrameReq)
     * @return 请求帧对象
     * @throws FrameFormatException     JSON报文格式错误
     * @throws FrameSerializerException 反序列化失败
     */
    GpFrameReq deserializeFrameReq(String json) throws FrameFormatException, FrameSerializerException;

    /**
     * 反序列化应答帧
     *
     * @param json JSON报文(FrameResp)
     * @return 应答帧
     * @throws FrameFormatException     JSON报文格式错误
     * @throws FrameSerializerException 反序列化失败
     */
    GpFrameResp deserializeFrameResp(String json) throws FrameFormatException, FrameSerializerException;

    /**
     * 序列化请求帧
     *
     * @param frameReq 请求帧
     * @return 请求帧报文(JSON)
     * @throws FrameSerializerException 序列化失败
     */
    String serializeFrameReq(GpFrameReq frameReq) throws FrameSerializerException;

    /**
     * 序列化应答帧
     *
     * @param frameResp 应答帧
     * @return 应答帧报文(JSON)
     * @throws FrameSerializerException 序列化失败
     */
    String serializeFrameResp(GpFrameResp frameResp) throws FrameSerializerException;

}
