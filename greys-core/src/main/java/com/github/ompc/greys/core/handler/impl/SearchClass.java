package com.github.ompc.greys.core.handler.impl;

import com.github.ompc.greys.core.util.PointCut;
import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.handler.Type;
import com.github.ompc.greys.core.listener.InvokeListener;
import com.github.ompc.greys.protocol.body.SearchClassReq;
import com.github.ompc.greys.protocol.body.SearchClassResp;
import com.github.ompc.greys.protocol.body.info.ClassInfo;

import java.util.*;

/**
 * 搜索已加载类
 * Created by vlinux on 15/11/2.
 */
@Type("search-class")
public class SearchClass extends ConvertSupport implements Handler<SearchClassReq, SearchClassResp> {

    @Override
    public LinkedHashMap<InvokeListener, ArrayList<PointCut>> handle(SearchClassReq req, Out<SearchClassResp> out) throws Throwable {

        final SearchClassResp resp = new SearchClassResp();
        resp.setClassInfos(toClassInfos(matchingClasses(req.getClassMatching(), req.isIncludeSubClasses()), req));
        out.finish(resp);

        return null;
    }

    private Collection<ClassInfo> toClassInfos(final Collection<Class<?>> classSet, final SearchClassReq req) {

        final Set<ClassInfo> classInfoSet = new LinkedHashSet<ClassInfo>();
        for (Class<?> clazz : classSet) {
            classInfoSet.add(toClassInfo(clazz,req));
        }

        return classInfoSet;
    }

}
