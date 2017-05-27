package utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by jrm on 2017-4-21.
 * 參數的转变
 */

public class ConvertParamsUtils {

    private static  Map<String ,String> params ;

    private static ConvertParamsUtils convertParams;

    public static ConvertParamsUtils getInstatnce(){
        if (convertParams == null){
            convertParams = new ConvertParamsUtils();
        }
        return convertParams;
    }

    public ConvertParamsUtils(){
        params = new HashMap<>();
    }

    /**
     * 传入一对键值对的参数
     * @param key
     * @param value
     * @return
     */
    public Map<String,String> getParamsOne(String key,String value){
        params.put(key,value);
        return  getSignParams(params);
    }

    /**
     * 传入两个键值对的参数
     * @param key1
     * @param value1
     * @param key2
     * @param value2
     * @return
     */
    public Map<String,String> getParamsTwo(String key1,String value1,String key2,String value2){
        params.put(key1,value1);
        params.put(key2,value2);
        return  getSignParams(params);
    }

    /**
     *
     * @param key1
     * @param value1
     * @param key2
     * @param value2
     * @param key3
     * @param value3
     * @return
     */
    public Map<String,String> getParamsThree(String key1,String value1,String key2,String value2,String key3,String value3){
        params.put(key1,value1);
        params.put(key2,value2);
        params.put(key3,value3);
        return  getSignParams(params);
    }

    public Map<String,String> getParamsFour(String key1,String value1,String key2,String value2,String key3,String value3,String key4,String value4){
        params.put(key1,value1);
        params.put(key2,value2);
        params.put(key3,value3);
        params.put(key4,value4);
        return  getSignParams(params);
    }


    public Map<String,String> getParamsFive(String key1,String value1,String key2,String value2,String key3,String value3,String key4,String value4,String key5,String value5){
        params.put(key1,value1);
        params.put(key2,value2);
        params.put(key3,value3);
        params.put(key4,value4);
        params.put(key5,value5);
        return  getSignParams(params);
    }

    public Map<String,String> getParamsSix(String key1,String value1,String key2,String value2,String key3,String value3,String key4,String value4,String key6,String value6,String key5,String value5){
        params.put(key1,value1);
        params.put(key2,value2);
        params.put(key3,value3);
        params.put(key4,value4);
        params.put(key5,value5);
        params.put(key6,value6);
        return  getSignParams(params);
    }

    public Map<String,String> getParamsSeven(String key1,String value1,String key2,String value2,String key3,String value3,String key4,
                                             String value4,String key6,String value6,String key5,String value5,String key7,String value7){
        params.put(key1,value1);
        params.put(key2,value2);
        params.put(key3,value3);
        params.put(key4,value4);
        params.put(key5,value5);
        params.put(key6,value6);
        params.put(key7,value7);
        return  getSignParams(params);
    }
    public Map<String,String> getParamsEight(String key1,String value1,String key2,String value2,String key3,String value3,String key4,
                                             String value4,String key6,String value6,String key5,String value5,String key7,String value7
                                             ,String key8,String value8){
        params.put(key1,value1);
        params.put(key2,value2);
        params.put(key3,value3);
        params.put(key4,value4);
        params.put(key5,value5);
        params.put(key6,value6);
        params.put(key7,value7);
        params.put(key8,value8);
        return  getSignParams(params);
    }

    public Map<String,String> getParamsNine(String key1,String value1,String key2,String value2,String key3,String value3,String key4,
                                             String value4,String key6,String value6,String key5,String value5,String key7,String value7
                                                ,String key8,String value8 ,String key9,String value9){
        params.put(key1,value1);
        params.put(key2,value2);
        params.put(key3,value3);
        params.put(key4,value4);
        params.put(key5,value5);
        params.put(key6,value6);
        params.put(key7,value7);
        params.put(key8,value8);
        params.put(key9,value9);
        return  getSignParams(params);
    }

    public Map<String,String> getParamsTen(String key1,String value1,String key2,String value2,String key3,String value3,String key4,
                                            String value4,String key6,String value6,String key5,String value5,String key7,String value7
                                        ,String key8,String value8 ,String key9,String value9,String key10,String value10){
        params.put(key1,value1);
        params.put(key2,value2);
        params.put(key3,value3);
        params.put(key4,value4);
        params.put(key5,value5);
        params.put(key6,value6);
        params.put(key7,value7);
        params.put(key8,value8);
        params.put(key9,value9);
        params.put(key10,value10);
        return  getSignParams(params);
    }

    /**
     * 对所给的参数进行加密处理
     * @param params
     */
    public  Map<String,String> getSignParams(Map<String ,String > params){
        Map<String ,String> map = new HashMap<>();
        ArrayList<String> pair = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if (params == null || params.size() == 0){
            map.put("sign_parm", "sign");
            sb.append(map.get("sign_parm"));
        }else {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                if (params.get(key) == null) continue;
                String value = params.get(key).toString();
                if (TextUtils.isEmpty(value)) {
                    continue;
                }
                map.put(key,value);
                pair.add(key + "=" + value);
            }
            Collections.sort(pair);
            for (String temp : pair) {
                sb.append(temp);
            }
        }
        String sign = sb.toString();
        String code = Md5Utils.strCodeRt(sign);
        map.put("sign",code);
        sb = null;
        return  map;
    }
}
