package cn.loftown.wechat.app.code.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum OrderCancelTypeEnum {
    UNKNOWN(-1, "未知"),
    CANCEL_PLAY(1, "取消保养计划"),
    INFO_ERROR(2, "预约信息填写有误"),
    TIME_REASON(3, "时间与其它事情冲突"),
    OTHER(4, "其它原因"),
    OUT_TIME(5,"超时取消");

    private Integer code;

    private String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    OrderCancelTypeEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public static OrderCancelTypeEnum parse(Integer code){
        return Arrays.stream(OrderCancelTypeEnum.values())
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
