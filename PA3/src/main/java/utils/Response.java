package utils;

import java.io.Serializable;

/**
 * @description: A response entity indicating the state of one specific action.
 * @author: Xue Zhang
 * @date: 2022-02-02
 * @version: 1.0
 **/
public class Response implements Serializable {

    private static final long serialVersionID = 1L;

    private Integer code;
    private String message;
    private Object data;

    public Response() {
    }

    public Response(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
