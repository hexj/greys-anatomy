package com.github.ompc.greys.protocol.body;

import com.github.ompc.greys.protocol.Req;
import com.github.ompc.greys.protocol.body.matching.ClassMatching;

import javax.validation.constraints.NotNull;

/**
 * 搜索已加载类请求
 * Created by oldmanpushcart@gmail.com on 15/11/7.
 */
@Type("search-class")
public class SearchClassReq extends Req {

    // 类匹配集合
    @NotNull
    private ClassMatching classMatching = new ClassMatching();

    // 是否包含子类
    private boolean isIncludeSubClasses;

    // 是否包含属性
    private boolean isIncludeFields;

    // 是否包含ClassLoader
    private boolean isIncludeClassLoaders;

    // 是否包含接口
    private boolean isIncludeInterfaces;

    // 是否包含父类
    private boolean isIncludeSuperClasses;

    // 是否包含Annotation
    private boolean isIncludeAnnotations;

    public ClassMatching getClassMatching() {
        return classMatching;
    }

    public void setClassMatching(ClassMatching classMatching) {
        this.classMatching = classMatching;
    }

    public boolean isIncludeSubClasses() {
        return isIncludeSubClasses;
    }

    public void setIsIncludeSubClasses(boolean isIncludeSubClasses) {
        this.isIncludeSubClasses = isIncludeSubClasses;
    }

    public boolean isIncludeFields() {
        return isIncludeFields;
    }

    public void setIsIncludeFields(boolean isIncludeFields) {
        this.isIncludeFields = isIncludeFields;
    }

    public boolean isIncludeClassLoaders() {
        return isIncludeClassLoaders;
    }

    public void setIsIncludeClassLoaders(boolean isIncludeClassLoaders) {
        this.isIncludeClassLoaders = isIncludeClassLoaders;
    }

    public boolean isIncludeInterfaces() {
        return isIncludeInterfaces;
    }

    public void setIsIncludeInterfaces(boolean isIncludeInterfaces) {
        this.isIncludeInterfaces = isIncludeInterfaces;
    }

    public boolean isIncludeSuperClasses() {
        return isIncludeSuperClasses;
    }

    public void setIsIncludeSuperClasses(boolean isIncludeSuperClasses) {
        this.isIncludeSuperClasses = isIncludeSuperClasses;
    }

    public boolean isIncludeAnnotations() {
        return isIncludeAnnotations;
    }

    public void setIsIncludeAnnotations(boolean isIncludeAnnotations) {
        this.isIncludeAnnotations = isIncludeAnnotations;
    }
}