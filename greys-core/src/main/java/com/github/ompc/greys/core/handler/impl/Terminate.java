package com.github.ompc.greys.core.handler.impl;

import com.github.ompc.greys.core.PointCut;
import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.handler.Type;
import com.github.ompc.greys.core.listener.InvokeListener;
import com.github.ompc.greys.core.server.Session;
import com.github.ompc.greys.core.util.LogUtils;
import com.github.ompc.greys.protocol.body.TerminateReq;
import com.github.ompc.greys.protocol.body.TerminateResp;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 终结请求
 * Created by vlinux on 15/10/31.
 */
@Type("terminate")
public class Terminate extends CycleSupport implements Handler<TerminateReq, TerminateResp> {

    private final Logger logger = LogUtils.getLogger();
    private Session session;

    @Override
    public void init(int id, Session session) throws Throwable {
        this.session = session;
    }

    @Override
    public LinkedHashMap<InvokeListener, ArrayList<PointCut>> handle(TerminateReq req, Out<TerminateResp> out) throws Throwable {

        final int targetId = req.getTargetId();
        session.terminate(targetId);
        out.finish();

        logger.info("terminate targetId={}", targetId);

        return null;
    }

}
