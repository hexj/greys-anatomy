package com.github.ompc.greys.protocol.body;

import com.github.ompc.greys.protocol.Req;

import javax.validation.constraints.NotNull;

/**
 * 终结请求
 * Created by oldmanpushcart@gmail.com on 15/11/7.
 */
@Type("terminate")
public class TerminateReq extends Req {

    // 目标帧序列ID
    @NotNull
    private Integer targetId;

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

}