package com.github.ompc.greys.core.handler.impl;

import com.github.ompc.greys.core.util.GaMethod;
import com.github.ompc.greys.core.PointCut;
import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.handler.Type;
import com.github.ompc.greys.core.listener.InvokeListener;
import com.github.ompc.greys.protocol.body.SearchMethodReq;
import com.github.ompc.greys.protocol.body.SearchMethodResp;
import com.github.ompc.greys.protocol.body.info.MethodInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * 搜索已加载类的方法
 * Created by vlinux on 15/11/2.
 */
@Type("search-method")
public class SearchMethod extends ConvertSupport implements Handler<SearchMethodReq, SearchMethodResp> {

    @Override
    public LinkedHashMap<InvokeListener, ArrayList<PointCut>> handle(SearchMethodReq req, Out<SearchMethodResp> out) throws Throwable {

        final Collection<GaMethod> matchedGaMethods = matchingGaMethod(
                req.getClassMatching(),
                req.isIncludeSubClasses(),
                req.getMethodMatching()
        );

        final Collection<MethodInfo> methodInfos = new ArrayList<MethodInfo>();
        for( GaMethod gaMethod : matchedGaMethods ) {
            methodInfos.add(toMethodInfo(gaMethod));
        }

        final SearchMethodResp resp = new SearchMethodResp();
        resp.setMethodInfos(methodInfos);
        out.finish(resp);

        return null;
    }

}
