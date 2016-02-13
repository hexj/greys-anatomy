package com.github.ompc.greys.protocol.body.matching;

import com.github.ompc.greys.common.util.GaReflectUtils;
import com.github.ompc.greys.common.util.GaStringUtils;

/**
 * 类匹配
 * Created by oldmanpushcart@gmail.com on 15/11/2.
 */
public class ClassMatching {

    // 访问修饰符
    private int modifier = GaReflectUtils.DEFAULT_MOD;

    // 类类型
    private int type = GaReflectUtils.DEFAULT_TYPE;

    // 类名匹配
    private String pattern = GaStringUtils.REGEX_MATCH_ANY;

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
