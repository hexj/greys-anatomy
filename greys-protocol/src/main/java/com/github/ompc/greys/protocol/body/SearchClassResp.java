package com.github.ompc.greys.protocol.body;

import com.github.ompc.greys.protocol.Resp;
import com.github.ompc.greys.protocol.body.info.ClassInfo;

import java.util.Collection;

/**
 * 搜索已加载类应答
 * Created by oldmanpushcart@gmail.com on 15/11/7.
 */
@Type("search-class")
public class SearchClassResp extends Resp {

    // 匹配类信息
    private Collection<ClassInfo> classInfos;

    public Collection<ClassInfo> getClassInfos() {
        return classInfos;
    }

    public void setClassInfos(Collection<ClassInfo> classInfos) {
        this.classInfos = classInfos;
    }

}