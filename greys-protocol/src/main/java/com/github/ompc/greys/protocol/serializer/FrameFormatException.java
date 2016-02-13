package com.github.ompc.greys.protocol.serializer;

/**
 * 帧格式错误
 * Created by oldmanpushcart@gmail.com on 15/11/8.
 */
public class FrameFormatException extends Exception {

    public FrameFormatException(Throwable cause) {
        super(cause);
    }

    public FrameFormatException() {
    }
}
