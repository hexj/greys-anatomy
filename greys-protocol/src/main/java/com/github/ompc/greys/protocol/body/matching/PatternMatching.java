package com.github.ompc.greys.protocol.body.matching;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 模式匹配
 * Created by oldmanpushcart@gmail.com on 15/11/2.
 */
public class PatternMatching {

    // 模式字符串
    @NotBlank
    private String pattern;

    // 模式类型
    private int type;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
