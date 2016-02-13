package com.github.ompc.greys.protocol.body.info;

import java.util.Collection;

/**
 * 类信息
 * Created by oldmanpushcart@gmail.com on 15/11/2.
 */
public class ClassInfo extends TypeInfo {


    private Collection<TypeInfo> interfaces;


    private Collection<TypeInfo> annotations;


    private Collection<TypeInfo> superClasses;


    private Collection<ObjectInfo> classLoaders;


    private Collection<FieldInfo> fieldInfos;

    public Collection<TypeInfo> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Collection<TypeInfo> interfaces) {
        this.interfaces = interfaces;
    }

    public Collection<TypeInfo> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Collection<TypeInfo> annotations) {
        this.annotations = annotations;
    }

    public Collection<TypeInfo> getSuperClasses() {
        return superClasses;
    }

    public void setSuperClasses(Collection<TypeInfo> superClasses) {
        this.superClasses = superClasses;
    }

    public Collection<ObjectInfo> getClassLoaders() {
        return classLoaders;
    }

    public void setClassLoaders(Collection<ObjectInfo> classLoaders) {
        this.classLoaders = classLoaders;
    }

    public Collection<FieldInfo> getFieldInfos() {
        return fieldInfos;
    }

    public void setFieldInfos(Collection<FieldInfo> fieldInfos) {
        this.fieldInfos = fieldInfos;
    }
}
