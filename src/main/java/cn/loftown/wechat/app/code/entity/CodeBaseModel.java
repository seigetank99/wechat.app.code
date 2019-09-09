package cn.loftown.wechat.app.code.entity;


import lombok.Data;

import java.util.Date;

@Data
public class CodeBaseModel {
    /**
     * 模版编号
     */
    private Integer id;
    /**
     * 版本号
     */
    private String userVersion;
    /**
     * 版本说明
     */
    private String userDesc;
    /**
     * 创建来源小程序appid
     */
    private String sourceMiniprogramAppid;
    /**
     * 创建来源小程序名称
     */
    private String sourceMiniprogram;
    /**
     * 创建人微信昵称
     */
    private String developer;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建时间格式化
     */
    private String createTimeFormat;

}
