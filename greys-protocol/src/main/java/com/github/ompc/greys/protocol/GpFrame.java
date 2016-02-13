package com.github.ompc.greys.protocol;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 帧数据结构
 * Created by oldmanpushcart@gmail.com on 15/11/8.
 */
public class GpFrame {

    // 帧序列
    private Integer id;

    // 请求类型

    @NotBlank
    private String type;

    public GpFrame() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GpFrame(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "frame[id=" + id + ";type=" + type + "]";
    }
}
