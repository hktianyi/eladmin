package me.zhengjie.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
@Schema(description = "返回结果")
public class ResponseWrap<T> {
    @Schema(description = "业务处理结果，200成功")
    private int code;
    @Schema(description = "消息")
    private String message;
    @Schema
    private T data;

    public static <T> ResponseWrap<T> ok() {
        return ok(null);
    }

    public static <T> ResponseWrap<T> ok(T data) {
        return new ResponseWrap<T>().setCode(200).setData(data);
    }

    public static <T> ResponseWrap<T> error(String message) {
        return error(-1, message);
    }

    public static <T> ResponseWrap<T> error(int code, String message) {
        return new ResponseWrap<T>().setCode(-code).setMessage(message);
    }
}
