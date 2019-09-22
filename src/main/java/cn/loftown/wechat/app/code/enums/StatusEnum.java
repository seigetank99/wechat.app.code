package cn.loftown.wechat.app.code.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum StatusEnum {
    UNKNOWN(-1, "未知"),
    ENABLES(1, "启用"),
    DISABLES(2,"禁用");

    private Integer code;

    private String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    StatusEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public static StatusEnum parse(Integer code){
        return Arrays.stream(StatusEnum.values())
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
