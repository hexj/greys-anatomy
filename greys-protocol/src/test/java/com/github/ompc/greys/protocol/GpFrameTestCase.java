package com.github.ompc.greys.protocol;

import com.github.ompc.greys.protocol.body.Type;
import com.github.ompc.greys.protocol.serializer.FrameFormatException;
import com.github.ompc.greys.protocol.serializer.FrameSerializer;
import com.github.ompc.greys.protocol.serializer.FrameSerializerException;
import com.github.ompc.greys.protocol.serializer.GsonFrameSerializer;
import com.google.gson.Gson;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * 帧测试用例
 * Created by oldmanpushcart@gmail.com on 16/1/16.
 */
public class GpFrameTestCase<REQ extends Req, RESP extends Resp> {

    protected FrameSerializer frameSerializer = new GsonFrameSerializer();

    protected static String getJson(final String jsonPath) throws IOException {
        final InputStream is = Object.class.getResourceAsStream("/json/" + jsonPath);
        try {
            return IOUtils.toString(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    protected GpFrameReq toFrameReq(final int id, final REQ req) {
        Assert.assertNotNull(req);
        final Type typeAnno = req.getClass().getAnnotation(Type.class);
        Assert.assertNotNull(typeAnno);
        final String type = typeAnno.value();
        Assert.assertTrue(StringUtils.isNoneBlank(type));
        final GpFrameReq frameReq = new GpFrameReq();
        frameReq.setBody(req);
        frameReq.setType(type);
        frameReq.setId(id);
        return frameReq;
    }

    protected GpFrameResp toFrameResp(final int id, final boolean isFinish, final FrameStatusEnum status, final RESP resp) {
        Assert.assertNotNull(resp);
        final Type typeAnno = resp.getClass().getAnnotation(Type.class);
        Assert.assertNotNull(typeAnno);
        final String type = typeAnno.value();
        Assert.assertTrue(StringUtils.isNoneBlank(type));
        final GpFrameResp frameResp = new GpFrameResp();
        frameResp.setId(id);
        frameResp.setType(type);
        frameResp.setStatus(status);
        frameResp.setFinish(isFinish);
        frameResp.setBody(resp);
        return frameResp;
    }

//    protected void assertJsonSame(final String src, final String desc) {
//        Assert.assertTrue(StringUtils.isNotBlank(src));
//        Assert.assertTrue(StringUtils.isNotBlank(desc));
//        final JsonParser jsonParser = new JsonParser();
//        final JsonObject jsonObjectSrc = (JsonObject) jsonParser.parse(src);
//        final JsonObject jsonObjectDest = (JsonObject) jsonParser.parse(desc);
//        Assert.assertEquals(jsonObjectSrc, jsonObjectDest);
//    }

    protected void assertDeepEquals(final Object src, final Object desc) {
        Assert.assertNotNull(src);
        Assert.assertNotNull(desc);
        final Gson gson = new Gson();
        final String jsonSrc = gson.toJson(src);
        final String jsonDesc = gson.toJson(desc);
        Assert.assertEquals(jsonSrc, jsonDesc);
    }

    @Test
    public void test_获取帧序列化工具_帧序列化正常初始化() {
        final FrameSerializer frameSerializer = new GsonFrameSerializer();
        Assert.assertNotNull(frameSerializer);
    }

    @Test(expected = FrameFormatException.class)
    public void test_序列化错误帧报文_序列化报错() throws IOException, FrameFormatException, FrameSerializerException {

        final String json = getJson("FRAME_FORMAT_ERROR.json");
        frameSerializer.deserializeFrameReq(json);

    }

//    @Test(expected = FrameSerializerException.class)
//    public void test_序列化帧数据为空帧报文_序列化报错() throws IOException, FrameFormatException, FrameSerializerException {
//        final String json = getJson("FRAME_BODY_EMPTY_ERROR.json");
//        frameSerializer.deserializeFrameReq(json);
//    }

}
