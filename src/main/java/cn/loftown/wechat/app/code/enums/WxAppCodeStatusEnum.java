package cn.loftown.wechat.app.code.enums;

import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum WxAppCodeStatusEnum {
    /**
     * 未知
     */
    UNKNOWN(-1, "未知"),
    /**
     * 已作废
     */
    DELETE(1, "已作废"),
    /**
     * 代码提交后未提交审核
     */
    TEMPLATE(2, "体验中"),
    /**
     * 代码提交审核
     */
    SUBMIT(3,"审核中"),
    /**
     * 审核未通过
     */
    FEEDBACK(4,"被驳回"),
    /**
     * 审核通过
     */
    AUDIT(5,"审核通过"),
    /**
     * 生产发布
     */
    RELEASE(6,"生产发布"),
    /**
     * 生产禁用
     */
    INVISIBLE(7,"生产禁用"),
    /**
     * 生产回退
     */
    REVERT(8,"生产回退");

    private Integer code;

    private String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    WxAppCodeStatusEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public static WxAppCodeStatusEnum parse(Integer code){
        return Arrays.stream(WxAppCodeStatusEnum.values())
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
