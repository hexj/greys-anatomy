package com.github.ompc.greys.protocol.body.info;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * 类型信息<br/>
 * 任何Java类型都被抽象成了类型信息
 * Created by oldmanpushcart@gmail.com on 15/11/2.
 */
public class TypeInfo {


    @NotBlank
    private String name;


    private String codeSource;


    @NotNull
    private Integer modifier;


    @NotNull
    private Integer type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodeSource() {
        return codeSource;
    }

    public void setCodeSource(String codeSource) {
        this.codeSource = codeSource;
    }

    public Integer getModifier() {
        return modifier;
    }

    public void setModifier(Integer modifier) {
        this.modifier = modifier;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
