package com.ymnet.onekeyclean.cleanmore.framewrok.http;

import java.lang.reflect.Type;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Converter;

/**
 * Created with IntelliJ IDEA.
 * Date: 4/6/16
 * Author: zhangcm
 */
public interface Request {

    /**
     * 目前只使用到POST、GET
     */
    class Method {
        public static final int POST = 0;
        public static final int GET = 1;
    }

    /**
     * 设置请求url
     *
     * @param url 请求地址
     * @return this
     */
    Request url(String url);

    /**
     * 设置请求方式,默认使用POST方式请求
     *
     * @param method 请求方式
     * @return this
     * @see Method#POST
     * @see Method#GET
     */
    Request method(int method);

    /**
     * 设置加密方式,默认使用SALT_BASE64加密
     *
     * @param algorithm 加密方式
     * @return this
     * @see com.market2345.framework.security.SC#SALT_BASE64
     * @see com.market2345.framework.security.SC#SALT_MD5
     * @see com.market2345.framework.security.SC#NO_ENCRYPT
     */
    Request encrypt(int algorithm);

    /**
     * 设置请求参数
     *
     * @param key   参数的key
     * @param value 参数的value
     * @return this
     */
    Request param(String key, String value);

    /**
     * 设置请求参数
     *
     * @param params 请求参数集合
     * @return this
     */
    Request paramMap(Map<String, String> params);


    /**
     * 设置转换器，默认是GsonCoverter
     *
     * @param converter 转换器
     * @return this
     * @see GsonConverter
     */
    Request converter(Converter converter);

    /**
     * 设置返回对象的Type
     *
     * @param returnType 返回Type
     * @return this
     */
    Request returnType(Type returnType);


    Request returnClass(Class<?> clazz);


    /**
     * 构建Call对象,该对象管理请求
     */
    Call<?> build();

    String getUrl();

    int getMethod();

    int getAlgorithm();

    Map<String, String> getParams();

    Converter getConverter();

    Type getReturnType();

    Class<?> getReturnClazz();
}
