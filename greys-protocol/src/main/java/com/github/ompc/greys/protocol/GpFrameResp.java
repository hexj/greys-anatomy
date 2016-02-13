package com.github.ompc.greys.protocol;

import javax.validation.constraints.NotNull;

/**
 * 帧应答
 * Created by oldmanpushcart@gmail.com on 15/11/7.
 */
public final class GpFrameResp extends GpFrame {

    // 应答状态

    @NotNull
    private FrameStatusEnum status;

    // 是否完结

    @NotNull
    private Boolean finish;

    // 应答体
    private Resp body;

    public GpFrameResp() {

    }

    public GpFrameResp(int id, String type, FrameStatusEnum status, boolean finish) {
        super(id, type);
        this.status = status;
        this.finish = finish;
    }

    public FrameStatusEnum getStatus() {
        return status;
    }

    public void setStatus(FrameStatusEnum status) {
        this.status = status;
    }

    public Boolean getFinish() {
        return finish;
    }

    public void setFinish(Boolean finish) {
        this.finish = finish;
    }

    public Resp getBody() {
        return body;
    }

    public void setBody(Resp body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "frame-resp[id=" + getId() + ";type=" + getType() + "]";
    }

}
