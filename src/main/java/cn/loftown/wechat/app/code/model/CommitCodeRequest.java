package cn.loftown.wechat.app.code.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class CommitCodeRequest {
    /**
     * 小程序acid
     */
    private Integer wxAppAcid;
    /**
     * 小程序名称
     */
    private String wxAppName;
    /**
     * 代码库中的代码模版ID
     */
    private Integer templateId;
    /**
     * 代码版本号，开发者可自定义（长度不要超过64个字符）
     */
    private String userVersion;
    /**
     * 代码描述，开发者可自定义
     */
    private String userDesc;
    /**
     * ext.json，详情查看https://developers.weixin.qq.com/miniprogram/dev/devtools/ext.html
     */
    private JSONObject extJson;
}
