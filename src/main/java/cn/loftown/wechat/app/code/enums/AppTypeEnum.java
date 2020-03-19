package cn.loftown.wechat.app.code.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum AppTypeEnum {
    /**
     * 未知
     */
    UNKNOWN(-1, "未知"),
    WECHATSERVICE(1, "微信公众号"),
    WECHATMINIAPP(2,"微信小程序");

    private Integer code;

    private String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    AppTypeEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public static AppTypeEnum parse(Integer code){
        return Arrays.stream(AppTypeEnum.values())
                .filter(item -> item.getCode().equals(code))
                .findFirst()
                .orElse(UNKNOWN);
    }

    @JsonValue
    public Map<Integer,String> toJson(){
        Map map = new HashMap<Integer,String>();
        map.put("code",getCode());
        map.put("name",getName());
        return map;
    }
}
