package com.github.ompc.greys.protocol;

/**
 * 帧请求
 * Created by oldmanpushcart@gmail.com on 15/11/7.
 */
public final class GpFrameReq extends GpFrame {

    // 请求数据
    private Req body;

    public Req getBody() {
        return body;
    }

    public void setBody(Req body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "frame-req[id=" + getId() + ";type=" + getType() + "]";
    }

}
