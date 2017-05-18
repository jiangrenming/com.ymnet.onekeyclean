package com.ymnet.onekeyclean.cleanmore.framewrok.http;


import java.lang.reflect.Type;

/**
 * 返回转换器
 */
public interface Converter {
    <T> T convert(ResponseBody response, Type type);
}
