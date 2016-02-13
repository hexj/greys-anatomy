package com.github.ompc.greys.protocol.body.matching;

import com.github.ompc.greys.common.util.GaReflectUtils;
import com.github.ompc.greys.common.util.GaStringUtils;

import java.util.Collection;

/**
 * 方法匹配
 * Created by oldmanpushcart@gmail.com on 15/11/2.
 */
public class MethodMatching {

    // 访问修饰符
    private int modifier = GaReflectUtils.DEFAULT_MOD;

    // 方法名匹配
    private String pattern = GaStringUtils.REGEX_MATCH_ANY;

    // 参数数组类型匹配
    private Collection<ClassMatching> parameters;

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Collection<ClassMatching> getParameters() {
        return parameters;
    }

    public void setParameters(Collection<ClassMatching> parameters) {
        this.parameters = parameters;
    }
}
