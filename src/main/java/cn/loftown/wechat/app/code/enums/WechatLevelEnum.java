package cn.loftown.wechat.app.code.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum WechatLevelEnum {
    /**
     * 未知
     */
    UNKNOWN(-1, "未知"),
    /**
     * 普通订阅号
     */
    SUBSCRIBE(1, "普通订阅号"),

    /**
     * 认证订阅号
     */
    AUTHSUBSCRIBE(2,"认证订阅号"),

    /**
     * 普通服务号
     */
    SERVICE(3,"普通服务号"),

    /**
     * 认证服务号/认证媒体/政府订阅号
     */
    AUTHSERVICE(4,"认证服务号/认证媒体/政府订阅号");

    private Integer code;

    private String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    WechatLevelEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public static WechatLevelEnum parse(Integer code){
        return Arrays.stream(WechatLevelEnum.values())
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
