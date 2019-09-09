package cn.loftown.wechat.app.code.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信公众号类型
 */
public enum WechatServiceTypeEnum {

    /**
     * 未知
     */
    UNKNOWN(-1, "未知"),
    /**
     * 订阅号
     */
    SUBSCRIBE(0, "订阅号"),

    /**
     * 代表由历史老帐号升级后的订阅号
     */
    OLDSUBSCRIBE(1,"老订阅号"),

    /**
     * 服务号
     */
    SERVICE(2,"服务号");

    private Integer code;

    private String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    WechatServiceTypeEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public static WechatServiceTypeEnum parse(Integer code){
        return Arrays.stream(WechatServiceTypeEnum.values())
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
