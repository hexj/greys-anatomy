package com.github.ompc.greys.protocol.serializer;

import com.github.ompc.greys.protocol.FrameStatusEnum;

/**
 * 帧序列化相关异常
 * Created by oldmanpushcart@gmail.com on 15/11/8.
 */
public class FrameSerializerException extends Exception {

    // 帧序号
    private final int id;

    // 帧类型
    private final String type;

    // 错误码
    private final FrameStatusEnum status;

    /**
     * 帧序列化异常
     *
     * @param id     帧ID
     * @param type   帧类型
     * @param status 错误码
     *               {@link com.github.ompc.greys.protocol.FrameStatusEnum}
     */
    public FrameSerializerException(int id, String type, FrameStatusEnum status) {
        this.id = id;
        this.type = type;
        this.status = status;
    }

    /**
     * 帧序列化异常
     *
     * @param id     帧ID
     * @param type   帧类型
     * @param status 错误码
     *               {@link com.github.ompc.greys.protocol.FrameStatusEnum}
     * @param cause  错误原因
     */
    public FrameSerializerException(int id, String type, FrameStatusEnum status, Throwable cause) {
        super(cause);
        this.id = id;
        this.type = type;
        this.status = status;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public FrameStatusEnum getStatus() {
        return status;
    }

    /**
     * 获取帧ID
     *
     * @return 帧ID
     */
    public int getId() {
        return id;
    }

    /**
     * 获取帧类型
     *
     * @return 帧类型
     */
    public String getType() {
        return type;
    }
}
