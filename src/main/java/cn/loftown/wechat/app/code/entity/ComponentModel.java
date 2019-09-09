package cn.loftown.wechat.app.code.entity;

import lombok.Data;

@Data
public class ComponentModel {
    private String token;
    private String encodingaesKey;
    private String appId;
    private String appsecret;
    /**
     * 认证状态
     */
    private Integer authstate;
}
