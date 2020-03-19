package cn.loftown.wechat.app.code.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class AccountWechatDTO {
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
     * 1、普通订阅号2、认证订阅号3、普通服务号4、认证服务号/认证媒体/政府订阅号
     */
    private Integer level;
    /**
     * 公众号名称
     */
    private String name;
    /**
     * 公众号帐号
     */
    private String account;
    /**
     * 公众号原始ID
     */
    private String original;
    /**
     * 功能介绍
     */
    private String signature;
    /**
     * 国家
     */
    private String country;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 微信公众平台账户名
     */
    private String username;
    /**
     * 微信公众平台账户密码
     */
    private String password;
    /**
     * 更新时间
     */
    private Long lastupdate;
    /**
     * 微信公众号APPID
     */
    private String key;
    /**
     * 微信公众号APPSECRET
     */
    private String secret;
    /**
     * 风格ID
     */
    private Integer styleid;
    /**
     * 引导关注素材URL
     */
    private String subscribeurl;
    /**
     * 添加时间
     */
    private Date createtime;
    /**
     * 开放平台返回的auth_refresh_token
     */
    private String auth_refresh_token;
    /**
     * 微信公众号头像
     */
    private String head_img;
    /**
     * 微信公众号类型
     */
    private Integer service_type_info;
    /**
     * 微信公众号认证类型
     */
    private Integer verify_type_info;
    /**
     * 微信公众号所设置的微信号，可能为空
     */
    private String alias;
    /**
     * 微信公众号二维码图片的URL
     */
    private String qrcode_url;
    /**
     * 微信公众号的主体名称
     */
    private String principal_name;
    /**
     * 是否绑定到微信开放平台
     */
    private Integer bind_wechat_open;
    private String bind_wechat_open_key;
}
