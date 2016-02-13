package com.github.ompc.greys.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 帧状态枚举
 * Created by oldmanpushcart@gmail.com on 16/1/16.
 */
public enum FrameStatusEnum {

    /**
     * 处理成功
     */
    SUCCESS(200),

    /**
     * 未知错误
     */
    UN_KNOW_ERROR(500),

    /**
     * 非法帧类型
     */
    ILLEGAL_FRAME_TYPE(502),

    /**
     * 非法数据格式<br/>
     * 遇到这个状态码意味着帧的格式没有问题,但帧所携带的数据(BODY字段)格式出了问题
     */
    ILLEGAL_BODY_FORMAT(503),

    /**
     * 服务器内部处理失败<br/>
     * GREYS服务器内部处理时发生了错误,需要结合服务器日志错误信息查看
     */
    HANDLER_ERROR(504),

    /**
     * 非法参数<br/>
     * 请求包含了非法的参数
     */
    ILLEGAL_PARAMETERS(505),

    /**
     * 请求已在处理中<br/>
     * 这个错误提示主要用于幂等操作,如果客户端重复将一个帧ID请求到服务端来,服务端判断当前帧是否已经在处理队列中,若在则返回该状态码
     */
    HANDING(506),

    /**
     * 非法帧格式<br/>
     * 通常遇到这个状态码就意味着你的整个帧请求格式有问题
     */
    ILLEGAL_FRAME_FORMAT(507),

    /**
     * 不支持的版本
     */
    UN_SUPPORT_VERSION(508),

    /**
     * 非法数据(BODY)<br/>
     * 遇到这个状态码意味着BODY的格式没有问题,但BODY数据不合法
     */
    ILLEGAL_BODY_DATA(509),

    /**
     * 非法数据(FRAME)<br/>
     * 遇到这个状态码意味着帧格式没有问题,但帧的数据不合法
     */
    ILLEGAL_FRAME_DATA(510);

    private final int status;

    FrameStatusEnum(int status) {
        this.status = status;
    }

    /**
     * 获取状态编码
     *
     * @return 状态编码
     */
    public int getStatus() {
        return status;
    }

    // 类型值到枚举的映射
    private final static Map<Integer, FrameStatusEnum> mapping = new HashMap<Integer, FrameStatusEnum>();

    static {
        for (FrameStatusEnum fse : FrameStatusEnum.values()) {
            mapping.put(fse.status, fse);
        }
    }

    /**
     * 根据状态码映射到具体枚举类型
     *
     * @param status 状态码
     * @return 状态枚举
     * @throws IllegalArgumentException 非法状态码
     */
    public static FrameStatusEnum mapping(int status) throws IllegalArgumentException {
        final FrameStatusEnum fse = mapping.get(status);
        if (null == fse) {
            throw new IllegalArgumentException("un know status=" + status);
        }
        return fse;
    }

}
