package cn.loftown.wechat.app.code.base;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class BaseResponse {
    public BaseResponse(){
        this.code = 0;
        this.message = "操作成功";
    }
    public BaseResponse(String message){
        this.code = 500;
        this.message = message;
    }

    public BaseResponse(int code, String message){
        this.code = code;
        this.message = message;
    }

    private Integer code;
    private String message;

    public static Integer ok(){
        return 0;
    }
    public static Integer business_error(){
        return 100;
    }
    public static Integer system_error(){
        return 500;
    }

    public static BaseResponse getInstance(JSONObject jsonObject){
        return new BaseResponse(jsonObject.getInteger("errcode"),jsonObject.getString("errmsg"));
    }
}
