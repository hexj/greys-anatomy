package com.github.ompc.greys.core.handler.impl;

import com.github.ompc.greys.core.PointCut;
import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.handler.Type;
import com.github.ompc.greys.core.listener.InvokeListener;
import com.github.ompc.greys.protocol.body.GetVersionReq;
import com.github.ompc.greys.protocol.body.GetVersionResp;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 获取版本号
 * Created by vlinux on 15/10/25.
 */
@Type("get-version")
public class GetVersion extends CycleSupport implements Handler<GetVersionReq, GetVersionResp> {

    @Override
    public LinkedHashMap<InvokeListener, ArrayList<PointCut>> handle(GetVersionReq req, Out<GetVersionResp> out) throws Throwable {
        final GetVersionResp resp = new GetVersionResp();
        resp.setVersion(version());
        out.finish(resp);
        return null;
    }

    /**
     * 返回Greys当前版本
     *
     * @return Greys当前版本
     * @throws IOException 获取版本号出错(一般不会发生)
     */
    public static String version() throws IOException {
        final InputStream is = GetVersion.class.getResourceAsStream("/com/github/ompc/greys/core/res/version");
        try {
            return IOUtils.toString(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

}
