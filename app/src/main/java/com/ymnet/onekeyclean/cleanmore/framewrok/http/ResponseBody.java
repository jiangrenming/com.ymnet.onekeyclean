package com.ymnet.onekeyclean.cleanmore.framewrok.http;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created with IntelliJ IDEA.
 * Date: 5/5/16
 * Author: zhangcm
 */
public interface ResponseBody {


    String string() throws IOException;

    InputStream byteStream();

    void close();


}
