package com.github.ompc.greys.protocol.body;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static com.github.ompc.greys.protocol.FrameStatusEnum.ILLEGAL_BODY_DATA;

/**
 * Terminate TestCase
 * Created by vlinux on 16/1/31.
 */
@RunWith(Parameterized.class)
public class TerminateTestCase extends BodyTestCase<TerminateReq,TerminateResp> {

    public TerminateTestCase(TestItem testItem) {
        super(testItem);
    }

    @Parameterized.Parameters
    public static Collection prepareData() {
        Object[][] objects = {
                {new ReqSuccess("{target-id:12345}")},
                {new RespSuccess("{}")},
                {new ReqFrameSerializerException("{targetId:12345}", ILLEGAL_BODY_DATA)},
                {new RespSuccess("{version:'1.7.4.3',words:'hello'}")},
        };
        return Arrays.asList(objects);
    }

}
