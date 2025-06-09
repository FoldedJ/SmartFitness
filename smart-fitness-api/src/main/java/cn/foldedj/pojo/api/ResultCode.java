package cn.foldedj.pojo.api;

import lombok.Getter;

/**
 * 响应状态码
 */
@Getter
public enum ResultCode {
    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 请求成功
     */
    REQUEST_SUCCESS(200, "请求成功"),
    
    /**
     * 请求错误
     */
    REQUEST_ERROR(400, "请求错误"),
    
    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),
    
    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),
    
    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),
    
    /**
     * 服务器错误
     */
    SERVER_ERROR(500, "服务器错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResultCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
