package cn.loftown.wechat.app.code.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum OrderStatusEnum {
    UNKNOWN(-1, "未知"),
    DRAFT(1, "草稿"),
    PROCESS(2, "待确认"),
    CONFIRM(3, "已确认"),
    FINISH(4, "已完成"),
    CANCEL(5,"已取消");

    private Integer code;

    private String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    OrderStatusEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public static OrderStatusEnum parse(Integer code){
        return Arrays.stream(OrderStatusEnum.values())
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
