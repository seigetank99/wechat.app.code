package cn.loftown.wechat.app.code.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountWxappDTO implements Serializable {
    /**
     * 主键
     */
    private Integer acid;
    /**
     * 所属账号uniacid
     */
    private Integer uniacid;
    /**
     * 接入的token值
     */
    private String token;
    /**
     * 接入的encodingaeskey值
     */
    private String encodingaeskey;
    /**
     * 接口权限级别:1.未认证2.已认证
     */
    private Integer level;
    private Integer statusId;
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
    private String key;
    /**
     * 微信小程序APPSECRET
     */
    private String secret;
    /**
     * 微信小程序名称
     */
    private String name;
    /**
     * 微信小程序访问域名
     */
    private String appdomain;
    /**
     * 开放平台返回的auth_refresh_token
     */
    private String auth_refresh_token;
    /**
     * 认证主体
     */
    private String principal_name;
    /**
     * 头像地址
     */
    private String head_img;
    /**
     * 是否绑定到微信开放平台
     */
    private Integer bind_wechat_open;
    private String bind_wechat_open_key;
}
