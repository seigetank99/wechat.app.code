package cn.loftown.wechat.app.code.entity;

import cn.loftown.wechat.app.code.enums.WechatLevelEnum;
import cn.loftown.wechat.app.code.enums.WechatServiceTypeEnum;
import cn.loftown.wechat.app.code.enums.WechatVerifyTypeEnum;
import lombok.Data;

@Data
public class AccountWechatModel {
    private Integer acid;
    /**
     * 公众号名称
     */
    private String name;
    private WechatLevelEnum levelEnum;
    /**
     * 公众号帐号
     */
    private String account;
    private String appId;
    /**
     * 微信公众号原始ID
     */
    private String original;
    private String headImg;
    /**
     * 微信公众号认证类型
     */
    private WechatServiceTypeEnum serviceTypeEnum;
    /**
     * 微信公众号类型
     */
    private WechatVerifyTypeEnum verifyTypeEnum;
    /**
     * 微信公众号所设置的微信号，可能为空
     */
    private String alias;
    /**
     * 微信公众号二维码图片的URL
     */
    private String qrcodeUrl;
    /**
     * 微信公众号的主体名称
     */
    private String principalName;
    /**
     * 是否通过认证
     */
    private Boolean hasAuth;
}
