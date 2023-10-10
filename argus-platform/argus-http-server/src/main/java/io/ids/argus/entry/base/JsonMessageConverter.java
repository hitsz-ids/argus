/*
 *
 */

package io.ids.argus.entry.base;

import io.ids.argus.core.base.json.Transformer;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

public class JsonMessageConverter extends AbstractGenericHttpMessageConverter<Object> {

    public JsonMessageConverter() {
        super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

    @Override
    protected void writeInternal(@Nonnull Object o, Type type, @Nonnull HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
    }

    @Override
    @Nonnull
    protected Object readInternal(Class<?> aClass, @Nonnull HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        return readJavaObject(aClass.getGenericSuperclass(), httpInputMessage);
    }

    @Override
    @Nonnull
    public Object read(@Nonnull Type type, Class<?> aClass, @Nonnull HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        return readJavaObject(type, httpInputMessage);
    }

    public Object readJavaObject(Type type, HttpInputMessage httpInputMessage) throws IOException {
        var data = Transformer.parseObject(new InputStreamReader(httpInputMessage.getBody()));
//        if (Objects.equals(type.getTypeName(), JSONData.class.getTypeName())) {
//            return data;
//        }
//        var object = JSONConverter.fromJson(data, type);
//        try {
//            List<Field> fieldList = new ArrayList<>();
//            Class<?> clazz = object.getClass();
//            while (clazz != null) {
//                fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
//                clazz = clazz.getSuperclass();
//            }
//            clazz = object.getClass();
//            List<Method> methodList = new ArrayList<>();
//            while (clazz != null) {
//                methodList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods())));
//                clazz = clazz.getSuperclass();
//            }
//            String fn;
//            for (Field field : fieldList) {
//                fn = field.getName();
//                if (Objects.equals(field.getType().getTypeName(), JSONData.class.getTypeName())) {
//                    for (Method method : methodList) {
//                        if(Objects.equals(("set" +
//                                        fn.substring(0, 1).toUpperCase() +
//                                        fn.substring(1)), method.getName())) {
//                            method.invoke(object, data.getJSONData(fn));
//                        }
//                    }
//                }
//            }
//        } catch (InvocationTargetException | IllegalAccessException e) {
//            throw new IOException("请求参数JSON解析错误");
//        }
        return data;
    }
}