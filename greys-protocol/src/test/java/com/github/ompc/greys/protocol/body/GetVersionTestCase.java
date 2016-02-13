package com.github.ompc.greys.protocol.body;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static com.github.ompc.greys.protocol.FrameStatusEnum.ILLEGAL_BODY_DATA;

/**
 * 获取版本命令测试用例
 * Created by oldmanpushcart@gmail.com on 16/1/16.
 */
@RunWith(Parameterized.class)
public class GetVersionTestCase extends BodyTestCase<GetVersionReq, GetVersionResp> {


    public GetVersionTestCase(TestItem testItem) {
        super(testItem);
    }


    @Parameterized.Parameters
    public static Collection prepareData() {
        Object[][] objects = {
                {new ReqSuccess("{}")},
                {new RespSuccess("{version:'1.7.4.3'}")},
                {new RespFrameSerializerException("{_version:'1.7.4.3'}", ILLEGAL_BODY_DATA)},
                {new RespFrameSerializerException("{}", ILLEGAL_BODY_DATA)},
                {new RespSuccess("{version:'1.7.4.3',words:'hello'}")},
        };
        return Arrays.asList(objects);
    }

}
