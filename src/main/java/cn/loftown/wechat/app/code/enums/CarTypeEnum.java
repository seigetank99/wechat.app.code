package cn.loftown.wechat.app.code.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum CarTypeEnum {
    UNKNOWN(-1, "未知"),
    TEST(1, "试驾"),
    APPOINTMENT(2, "预约"),
    MODEL(2,"车型");

    private Integer code;

    private String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    CarTypeEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public static CarTypeEnum parse(Integer code){
        return Arrays.stream(CarTypeEnum.values())
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
