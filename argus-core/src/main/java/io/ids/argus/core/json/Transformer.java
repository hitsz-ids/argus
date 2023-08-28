package io.ids.argus.core.json;

import com.alibaba.fastjson2.JSON;
import io.ids.argus.core.exception.ArgusJsonParserException;

import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

public class Transformer {
    public static ArgusJson parseObject(String json) {
        return new ArgusJson(JSON.parseObject(json));
    }

    public static ArgusJson merge(ArgusJson src, ArgusJson dest) {
        src.json.keySet().forEach(key -> dest.add(key, src.json.get(key)));
        return dest;
    }

    public static ArgusJson parseObject(Object json) {
        return parseObject(JSON.toJSONString(json));
    }

    public static ArgusJson parseObject(Reader reader) {
        return new ArgusJson(JSON.parseObject(reader));
    }

    public static <T> T parseObject(Reader reader, Class<T> clazz) {
        return JSON.parseObject(reader, clazz);
    }

    public static <T> T parseObject(ArgusJson json, Class<T> clazz) {
        return JSON.parseObject(json.toJsonString(), clazz);
    }

    public static ArgusJsonArray parseArray(String json) {
        return new ArgusJsonArray(JSON.parseArray(json));
    }

    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static String toJsonString(Object src) {
        return JSON.toJSONString(src);
    }

    public static String toJsonString(ArgusJson src) {
        return JSON.toJSONString(src.json);
    }

    public static String toJsonString(ArgusJsonArray src) {

        return JSON.toJSONString(src.array);
    }

    public static int writeTo(OutputStream out, Object object) {
        return JSON.writeTo(out, object);
    }

    public static int writeTo(OutputStream out, ArgusJson json) {
        return JSON.writeTo(out, json.json);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) throws ArgusJsonParserException {
        return JSON.parseObject(json, classOfT);
    }

    public static <T> T fromJson(ArgusJson data, Class<T> classOfT) throws ArgusJsonParserException {
        return data.json.to(classOfT);
    }

    public static <T> T fromJson(ArgusJson data, Type type) throws ArgusJsonParserException {
        return data.json.to(type);
    }

    public static <T> T fromJson(String json, Type type) throws ArgusJsonParserException {
        return JSON.parseObject(json, type);
    }
}
