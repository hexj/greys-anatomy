package com.github.ompc.greys.protocol.body;


import com.github.ompc.greys.protocol.Req;
import com.github.ompc.greys.protocol.body.matching.ClassMatching;
import com.github.ompc.greys.protocol.body.matching.MethodMatching;

/**
 * 搜索已加载类的方法请求
 * Created by oldmanpushcart@gmail.com on 15/11/7.
 */
@Type("search-method")
public class SearchMethodReq extends Req {

    // 类匹配
    private ClassMatching classMatching = new ClassMatching();

    // 是否包含子类
    private boolean isIncludeSubClasses;

    // 方法匹配
    private MethodMatching methodMatching = new MethodMatching();

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

    public MethodMatching getMethodMatching() {
        return methodMatching;
    }

    public void setMethodMatching(MethodMatching methodMatching) {
        this.methodMatching = methodMatching;
    }
}