package cn.loftown.wechat.app.code.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信公众号认证类型
 */
public enum WechatVerifyTypeEnum {
    /**
     * 未知
     */
    UNKNOWN(-2, "未知"),
    /**
     * 未认证
     */
    NOAUTH(-1, "未认证"),
    /**
     * 微信认证
     */
    WECHAT(0, "微信认证"),

    /**
     * 新浪微博认证
     */
    XINLANG(1,"新浪微博认证"),

    /**
     * 腾讯微博认证
     */
    TENGXUN(2,"腾讯微博认证"),

    /**
     * 已资质认证通过但还未通过名称认证
     */
    QUAL(3,"资质认证"),

    /**
     * 代表已资质认证通过、还未通过名称认证，但通过了新浪微博认证
     */
    QUALXINLANG(4,"资质和新浪微博认证"),

    /**
     * 代表已资质认证通过、还未通过名称认证，但通过了腾讯微博认证
     */
    QUALTENFXUN(5,"资质和腾讯微博认证");

    private Integer code;

    private String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    WechatVerifyTypeEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public static WechatVerifyTypeEnum parse(Integer code){
        return Arrays.stream(WechatVerifyTypeEnum.values())
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
