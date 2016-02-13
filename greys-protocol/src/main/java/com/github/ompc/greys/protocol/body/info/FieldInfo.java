package com.github.ompc.greys.protocol.body.info;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * 属性信息
 * Created by oldmanpushcart@gmail.com on 15/11/2.
 */
public class FieldInfo {


    @NotNull
    private Integer modifier;


    @NotBlank
    private String name;


    @NotNull
    private TypeInfo returnType;


    private TypeInfo[] annotations;


    private ObjectInfo value;

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeInfo getReturnType() {
        return returnType;
    }

    public void setReturnType(TypeInfo returnType) {
        this.returnType = returnType;
    }

    public TypeInfo[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(TypeInfo[] annotations) {
        this.annotations = annotations;
    }

    public ObjectInfo getValue() {
        return value;
    }

    public void setValue(ObjectInfo value) {
        this.value = value;
    }
}
