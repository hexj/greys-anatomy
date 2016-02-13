package com.github.ompc.greys.protocol.body;

import com.github.ompc.greys.protocol.*;
import com.github.ompc.greys.protocol.serializer.FrameFormatException;
import com.github.ompc.greys.protocol.serializer.FrameSerializerException;
import com.github.ompc.greys.protocol.serializer.GsonFrameSerializer;
import com.google.gson.Gson;
import junit.framework.Assert;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * BODY通用测试用例
 * Created by vlinux on 16/1/31.
 */
public class BodyTestCase<REQ extends Req, RESP extends Resp> extends GpFrameTestCase<REQ, RESP> {

    private static final AtomicInteger frameIndexRef = new AtomicInteger(1000);
    private final Gson gson = GsonFrameSerializer.newGson();
    private final TestItem testItem;
    private final Class<? extends Req> reqClass;
    private final Class<? extends Resp> respClass;

    public BodyTestCase(TestItem testItem) {
        this.testItem = testItem;
        final java.lang.reflect.Type mySuperClass = getClass().getGenericSuperclass();
        reqClass = (Class<? extends Req>) ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
        respClass = (Class<? extends Resp>) ((ParameterizedType) mySuperClass).getActualTypeArguments()[1];
    }


    // --- 序列化帧请求 ---

    @Test
    public void test_序列化正常请求_序列化成功() throws FrameSerializerException {

        if (testItem instanceof ReqSuccess) {
            final REQ req;
            try {
                req = (REQ) gson.fromJson(testItem.json, reqClass);
            } catch (Throwable t) {
                // 这一步不能挂,挂了就不用走这个测试用例了
                // 但不用担心,因为会在另外一个测试用例中拦下这种场景
                return;
            }
            frameSerializer.serializeFrameReq(toFrameReq(frameIndexRef.getAndIncrement(), req));
        }

    }

    @Test(expected = FrameSerializerException.class)
    public void test_序列化异常请求_序列化失败_序列化异常() throws FrameSerializerException {
        if (testItem instanceof ReqFrameSerializerException) {

            final REQ req;
            try {
                req = (REQ) gson.fromJson(testItem.json, reqClass);
            } catch (Throwable t) {
                // 这一步不能挂,挂了就不用走这个测试用例了
                // 但不用担心,因为会在另外一个测试用例中拦下这种场景
                throw newFrameSerializerException();
            }

            try {
                frameSerializer.serializeFrameReq(toFrameReq(frameIndexRef.getAndIncrement(), req));
            } catch (FrameSerializerException e) {
                Assert.assertEquals(e.getStatus(), ((ReqFrameSerializerException) testItem).status);
                throw e;
            }
        } else {
            throw newFrameSerializerException();
        }
    }


    // --- 反序列化帧请求 ---


    @Test
    public void test_反序列化正常请求_反序列化成功() throws FrameFormatException, FrameSerializerException {
        if (testItem instanceof ReqSuccess) {
            final String type = reqClass.getAnnotation(Type.class).value();
            final String frameReqJson = String.format("{id:%s,type:'%s',body:%s}", frameIndexRef.getAndIncrement(), type, testItem.json);
            final GpFrameReq frameReq = frameSerializer.deserializeFrameReq(frameReqJson);
            assertDeepEquals(gson.fromJson(testItem.json, reqClass), frameReq.getBody());
        }
    }

    @Test(expected = FrameSerializerException.class)
    public void test_反序列化异常请求_反序列化失败_序列化异常() throws FrameFormatException, FrameSerializerException {
        if (testItem instanceof ReqFrameSerializerException) {
            final String type = reqClass.getAnnotation(Type.class).value();
            final String frameReqJson = String.format("{id:%s,type:'%s',body:%s}", frameIndexRef.getAndIncrement(), type, testItem.json);
            try {
                frameSerializer.deserializeFrameReq(frameReqJson);
            } catch (FrameSerializerException e) {
                Assert.assertEquals(e.getStatus(), ((ReqFrameSerializerException) testItem).status);
                throw e;
            }
        } else {
            throw newFrameSerializerException();
        }
    }


    // --- 序列化帧应答 ---

    @Test
    public void test_序列化正常应答_序列化成功() throws FrameSerializerException {
        if (testItem instanceof RespSuccess) {
            final RESP resp;
            try {
                resp = (RESP) gson.fromJson(testItem.json, respClass);
            } catch (Throwable t) {
                // 这一步不能挂,挂了就不用走这个测试用例了
                // 但不用担心,因为会在另外一个测试用例中拦下这种场景
                return;
            }
            frameSerializer.serializeFrameResp(toFrameResp(frameIndexRef.getAndIncrement(), true, FrameStatusEnum.SUCCESS, resp));
        }
    }

    @Test(expected = FrameSerializerException.class)
    public void test_序列化异常应答_序列化失败_序列化异常() throws FrameSerializerException {
        if (testItem instanceof RespFrameSerializerException) {

            final RESP resp;
            try {
                resp = (RESP) gson.fromJson(testItem.json, respClass);
            } catch (Throwable t) {
                // 这一步不能挂,挂了就不用走这个测试用例了
                // 但不用担心,因为会在另外一个测试用例中拦下这种场景
                throw newFrameSerializerException();
            }

            try {
                frameSerializer.serializeFrameResp(toFrameResp(frameIndexRef.getAndIncrement(), true, FrameStatusEnum.SUCCESS, resp));
            } catch (FrameSerializerException e) {
                Assert.assertEquals(e.getStatus(), ((RespFrameSerializerException) testItem).status);
                throw e;
            }
        } else {
            throw newFrameSerializerException();
        }
    }

    // --- 反序列化帧应答 ---

    @Test
    public void test_反序列化正常应答_反序列化成功() throws FrameFormatException, FrameSerializerException {
        if (testItem instanceof RespSuccess) {
            final String type = reqClass.getAnnotation(Type.class).value();
            final String frameRespJson = String.format("{id:%s,type:'%s',status:200,finish:true,body:%s}", frameIndexRef.getAndIncrement(), type, testItem.json);
            final GpFrameResp frameResp = frameSerializer.deserializeFrameResp(frameRespJson);
            assertDeepEquals(gson.fromJson(testItem.json, respClass), frameResp.getBody());
        }
    }

    @Test(expected = FrameSerializerException.class)
    public void test_反序列化异常应答_反序列化失败_序列化异常() throws FrameFormatException, FrameSerializerException {
        if (testItem instanceof RespFrameSerializerException) {
            final String type = reqClass.getAnnotation(Type.class).value();
            final String frameRespJson = String.format("{id:%s,type:'%s',status:200,finish:true,body:%s}", frameIndexRef.getAndIncrement(), type, testItem.json);
            try {
                frameSerializer.deserializeFrameResp(frameRespJson);
            } catch (FrameSerializerException e) {
                Assert.assertEquals(e.getStatus(), ((RespFrameSerializerException) testItem).status);
                throw e;
            }
        } else {
            throw newFrameSerializerException();
        }
    }


    private FrameSerializerException newFrameSerializerException() {
        return new FrameSerializerException(0, null, null);
    }

    /**
     * 测试条目
     */
    protected static class TestItem {

        private final String json;

        public TestItem(String json) {
            this.json = json;
        }

    }

    protected static class ReqSuccess extends TestItem {
        public ReqSuccess(String json) {
            super(json);
        }
    }

    protected static class ReqFrameSerializerException extends TestItem {
        private final FrameStatusEnum status;

        public ReqFrameSerializerException(String json, FrameStatusEnum status) {
            super(json);
            this.status = status;
        }
    }

    protected static class RespSuccess extends TestItem {
        public RespSuccess(String json) {
            super(json);
        }
    }

    protected static class RespFrameSerializerException extends TestItem {
        private final FrameStatusEnum status;

        public RespFrameSerializerException(String json, FrameStatusEnum status) {
            super(json);
            this.status = status;
        }
    }

}
