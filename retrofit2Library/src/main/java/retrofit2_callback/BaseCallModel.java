package retrofit2_callback;

/**
 * Created by jrm on 2017-4-21.
 * 返回json数据的统一基础格式模型
 */

public class BaseCallModel <T>{
    public  int code;
    public String msg;
    public T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
