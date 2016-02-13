package com.github.ompc.greys.protocol.body;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * CloseSession TestCase
 * Created by vlinux on 16/1/31.
 */
@RunWith(Parameterized.class)
public class CloseSessionTestCase extends BodyTestCase<CloseSessionReq, CloseSessionResp> {

    public CloseSessionTestCase(TestItem testItem) {
        super(testItem);
    }

    @Parameterized.Parameters
    public static Collection prepareData() {
        Object[][] objects = {
                {new ReqSuccess("{}")},
                {new RespSuccess("{}")},
                {new RespSuccess("{words:'hello'}")},
        };
        return Arrays.asList(objects);
    }

}
