package com.github.ompc.greys.protocol.body;

import com.github.ompc.greys.protocol.Resp;
import com.github.ompc.greys.protocol.body.info.MethodInfo;

import java.util.Collection;

/**
 * 搜索已加载类的方法应答
 * Created by oldmanpushcart@gmail.com on 15/11/7.
 */
@Type("search-method")
public class SearchMethodResp extends Resp {

    private Collection<MethodInfo> methodInfos;

    public Collection<MethodInfo> getMethodInfos() {
        return methodInfos;
    }

    public void setMethodInfos(Collection<MethodInfo> methodInfos) {
        this.methodInfos = methodInfos;
    }

}