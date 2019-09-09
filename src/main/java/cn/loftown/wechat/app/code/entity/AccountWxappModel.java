package cn.loftown.wechat.app.code.entity;

import lombok.Data;

@Data
public class AccountWxappModel {
    /**
     * 主键
     */
    private Integer acid;
    /**
     * 接口权限级别:1.未认证2.已认证
     */
    private Integer level;
    /**
     * 微信小程序帐号
     */
    private String account;
    /**
     * 微信小程序原始ID
     */
    private String original;
    /**
     * 微信小程序APPID
     */
    private String appId;
    /**
     * 微信小程序名称
     */
    private String name;
    /**
     * 认证主体
     */
    private String principalName;
    /**
     * 头像地址
     */
    private String headImg;
    /**
     * 是否通过认证
     */
    private Boolean hasAuth;
}
