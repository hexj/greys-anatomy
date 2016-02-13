package com.github.ompc.greys.protocol.body;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static com.github.ompc.greys.protocol.FrameStatusEnum.ILLEGAL_BODY_FORMAT;

/**
 * SearchClass TestCase
 * Created by vlinux on 16/1/31.
 */
@RunWith(Parameterized.class)
public class SearchClassTestCase extends BodyTestCase<SearchClassReq,SearchClassResp> {

    public SearchClassTestCase(TestItem testItem) {
        super(testItem);
    }

    @Parameterized.Parameters
    public static Collection prepareData() throws IOException {
        Object[][] objects = {
                {new ReqSuccess("{}")},
                {new RespSuccess("{}")},
                {new ReqSuccess(getJson("BODY_SEARCH_CLASS_REQ_SUCCESS.json"))},
                {new ReqFrameSerializerException("{class-matching:true}", ILLEGAL_BODY_FORMAT)},
        };
        return Arrays.asList(objects);
    }

}
