package com.github.ompc.greys.protocol.body.info;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * 方法信息
 * Created by oldmanpushcart@gmail.com on 15/11/3.
 */
public class MethodInfo {


    @NotNull
    private TypeInfo declaringClass;


    @NotNull
    private TypeInfo returnClass;


    @NotNull
    private Integer modifier;


    @NotBlank
    private String name;


    private Collection<TypeInfo> annotations;


    private Collection<TypeInfo> parameters;


    private Collection<TypeInfo> exceptions;

    public TypeInfo getDeclaringClass() {
        return declaringClass;
    }

    public void setDeclaringClass(TypeInfo declaringClass) {
        this.declaringClass = declaringClass;
    }

    public TypeInfo getReturnClass() {
        return returnClass;
    }

    public void setReturnClass(TypeInfo returnClass) {
        this.returnClass = returnClass;
    }

    public Integer getModifier() {
        return modifier;
    }

    public void setModifier(Integer modifier) {
        this.modifier = modifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<TypeInfo> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Collection<TypeInfo> annotations) {
        this.annotations = annotations;
    }

    public Collection<TypeInfo> getParameters() {
        return parameters;
    }

    public void setParameters(Collection<TypeInfo> parameters) {
        this.parameters = parameters;
    }

    public Collection<TypeInfo> getExceptions() {
        return exceptions;
    }

    public void setExceptions(Collection<TypeInfo> exceptions) {
        this.exceptions = exceptions;
    }
}
