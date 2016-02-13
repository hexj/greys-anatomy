package com.github.ompc.greys.protocol.serializer;

import com.github.ompc.greys.protocol.*;
import com.github.ompc.greys.protocol.body.Type;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;

import static com.github.ompc.greys.common.util.GaClassUtils.*;
import static com.github.ompc.greys.protocol.FrameStatusEnum.*;
import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_DASHES;

/**
 * 帧状态枚举序列化
 */
class FrameStatusEnumSerializer implements JsonSerializer<FrameStatusEnum>,
        JsonDeserializer<FrameStatusEnum> {

    @Override
    public FrameStatusEnum deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return FrameStatusEnum.mapping(json.getAsInt());
    }

    @Override
    public JsonElement serialize(FrameStatusEnum src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getStatus());
    }
}

/**
 * Gson实现的FrameSerializer
 * Created by oldmanpushcart@gmail.com on 15/11/8.
 */
public class GsonFrameSerializer implements FrameSerializer {

    private final static Logger logger = LoggerFactory.getLogger("greys-anatomy");

    // Gson引用,多线程情况下并发需要,确保线程安全
    private ThreadLocal<Gson> gsonRef = new ThreadLocal<Gson>() {

        @Override
        protected Gson initialValue() {
            return newGson();
        }

    };

    /**
     * 为了满足可测性,这里将Gson的构造方式开放出来
     *
     * @return gson
     */
    public static Gson newGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(LOWER_CASE_WITH_DASHES)
                //.excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(FrameStatusEnum.class, new FrameStatusEnumSerializer())
                .create();
    }

    // 校验器引用,多线程情况下并发需要,确保线程安全
    private ThreadLocal<Validator> validatorRef = new ThreadLocal<Validator>() {

        @Override
        protected Validator initialValue() {
            return Validation.buildDefaultValidatorFactory().getValidator();
        }
    };

    // Json解析器引用,多线程情况下并发需要,确保线程安全
    private ThreadLocal<JsonParser> jsonParserRef = new ThreadLocal<JsonParser>() {

        @Override
        protected JsonParser initialValue() {
            return new JsonParser();
        }
    };

    // TYPE :: REQ 映射
    private final static Map<String/*TYPE*/, Class<? extends Req>> typeReqMapping = new HashMap<String, Class<? extends Req>>();

    // TYPE :: RESP 映射
    private final static Map<String/*TYPE*/, Class<? extends Resp>> typeRespMapping = new HashMap<String, Class<? extends Resp>>();

    /*
     * 映射类型TYPE::REQ
     */
    private static void mappingTypeReq() {
        for (Class<?> clazz :
                filterByAnnotation(
                        filterByParentClass(
                                scanPackage(
                                        GsonFrameSerializer.class.getClassLoader(),
                                        "com.github.ompc.greys.protocol.body"
                                ),
                                Req.class
                        ),
                        Type.class
                )) {

            final String type = clazz.getAnnotation(Type.class).value();
            typeReqMapping.put(type, (Class<? extends Req>) clazz);
            logger.info("mapping-type-req {}:{}", type, clazz);
        }
    }

    /*
     * 映射类型TYPE::RESP
     */
    private static void mappingTypeResp() {
        for (Class<?> clazz :
                filterByAnnotation(
                        filterByParentClass(
                                scanPackage(
                                        GsonFrameSerializer.class.getClassLoader(),
                                        "com.github.ompc.greys.protocol.body"
                                ),
                                Resp.class
                        ),
                        Type.class
                )) {

            final String type = clazz.getAnnotation(Type.class).value();
            typeRespMapping.put(type, (Class<? extends Resp>) clazz);
            logger.info("mapping-type-resp {}:{}", type, clazz);
        }
    }


    // 当类加载的时候,扫描所有Jar包下的指定路径,加载Protocol协议类
    static {
        mappingTypeReq();
        mappingTypeResp();
    }

    @Override
    public GpFrameReq deserializeFrameReq(String json) throws FrameFormatException, FrameSerializerException {

        final Gson gson = gsonRef.get();

        // 序列化帧头
        final JsonElement jsonElement;
        final GpFrameReq frameReq;
        try {
            jsonElement = jsonParserRef.get().parse(json);
            frameReq = gson.fromJson(jsonElement, GpFrameReq.class);
        } catch (JsonSyntaxException e) {
            throw new FrameFormatException(e);
        }

        // 验证帧格式是否合法
        if (null == frameReq
                || !validatorRef.get().validate(frameReq).isEmpty()) {
            throw new FrameFormatException();
        }

        // 填充FrameReq
        frameReq.setBody(deserializeFrameReqBody(gson, jsonElement, frameReq));

        return frameReq;
    }

    /*
     * 解析BODY::REQ
     */
    private Req deserializeFrameReqBody(final Gson gson, final JsonElement jsonElement, final GpFrameReq frameReq) throws FrameSerializerException {

        final String type = frameReq.getType();
        final Class<? extends Req> reqClass = typeReqMapping.get(type);
        if (null == reqClass) {
            // meta-data中如果没有注册到对应的type，则认为是无效的frame-type
            throw new FrameSerializerException(frameReq.getId(), frameReq.getType(), ILLEGAL_FRAME_TYPE);
        }

        final Req req;
        try {
            req = gson.fromJson(jsonElement.getAsJsonObject().getAsJsonObject("body"), reqClass);
        } catch (JsonSyntaxException e) {
            // 解析body的时候出错，则需要抛出帧请求数据错误
            throw new FrameSerializerException(frameReq.getId(), frameReq.getType(), ILLEGAL_BODY_FORMAT, e);
        }

        // 验证请求参数是否合法
        if (null != req
                && !validatorRef.get().validate(req).isEmpty()) {
            throw new FrameSerializerException(frameReq.getId(), frameReq.getType(), ILLEGAL_BODY_DATA);
        }

        return req;
    }

    @Override
    public GpFrameResp deserializeFrameResp(String json) throws FrameFormatException, FrameSerializerException {
        final Gson gson = gsonRef.get();

        // 序列化帧头
        final JsonElement jsonElement;
        final GpFrameResp frameResp;
        try {
            jsonElement = jsonParserRef.get().parse(json);
            frameResp = gson.fromJson(jsonElement, GpFrameResp.class);
        } catch (JsonSyntaxException e) {
            throw new FrameFormatException(e);
        }

        final String type = frameResp.getType();
        final Class<? extends Resp> respClass = typeRespMapping.get(type);
        if (null == respClass) {
            // meta-data中如果没有注册到对应的type，则认为是无效的frame-type
            throw new FrameSerializerException(frameResp.getId(), frameResp.getType(), ILLEGAL_FRAME_TYPE);
        }

        final Resp resp;
        try {
            resp = gson.fromJson(jsonElement.getAsJsonObject().getAsJsonObject("body"), respClass);
        } catch (JsonSyntaxException e) {
            // 解析body的时候出错，则需要抛出帧请求数据错误
            throw new FrameSerializerException(frameResp.getId(), frameResp.getType(), ILLEGAL_BODY_FORMAT, e);
        }

        // 验证请求参数是否合法
        if (null != resp
                && !validatorRef.get().validate(resp).isEmpty()) {
            throw new FrameSerializerException(frameResp.getId(), frameResp.getType(), ILLEGAL_BODY_DATA);
        }

        // 填充FrameResp
        frameResp.setBody(resp);

        return frameResp;
    }

    @Override
    public String serializeFrameReq(GpFrameReq frameReq) throws FrameSerializerException {

        // 校验待序列化对象是否合法
        if (null != frameReq.getBody()
                && !validatorRef.get().validate(frameReq.getBody()).isEmpty()) {
            throw new FrameSerializerException(frameReq.getId(), frameReq.getType(), ILLEGAL_BODY_DATA);
        }

        if (!validatorRef.get().validate(frameReq).isEmpty()) {
            throw new FrameSerializerException(frameReq.getId(), frameReq.getType(), ILLEGAL_FRAME_DATA);
        }

        // 序列化对象
        try {
            return gsonRef.get().toJson(frameReq);
        } catch (Throwable cause) {
            throw new FrameSerializerException(frameReq.getId(), frameReq.getType(), ILLEGAL_FRAME_FORMAT, cause);
        }
    }

    @Override
    public String serializeFrameResp(GpFrameResp frameResp) throws FrameSerializerException {

        // 校验待序列化对象是否合法
        if (null != frameResp.getBody()
                && !validatorRef.get().validate(frameResp.getBody()).isEmpty()) {
            throw new FrameSerializerException(frameResp.getId(), frameResp.getType(), ILLEGAL_BODY_DATA);
        }

        if (!validatorRef.get().validate(frameResp).isEmpty()) {
            throw new FrameSerializerException(frameResp.getId(), frameResp.getType(), ILLEGAL_FRAME_DATA);
        }

        // 序列化对象
        try {
            return gsonRef.get().toJson(frameResp);
        } catch (Throwable cause) {
            throw new FrameSerializerException(frameResp.getId(), frameResp.getType(), ILLEGAL_FRAME_FORMAT, cause);
        }
    }
}
