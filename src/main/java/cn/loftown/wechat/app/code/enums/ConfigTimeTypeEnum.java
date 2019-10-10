package cn.loftown.wechat.app.code.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ConfigTimeTypeEnum {
    UNKNOWN(-1, "未知"),
    HOUR(1, "小时"),
    DAY(2,"天"),
    WEEK(3,"周"),
    MONTH(4,"月"),
    YEAR(5,"年");

    private Integer code;

    private String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    ConfigTimeTypeEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public static ConfigTimeTypeEnum parse(Integer code){
        return Arrays.stream(ConfigTimeTypeEnum.values())
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
