package cn.loftown.wechat.app.code.dto.lf;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class WxAppCodeSubmitDTO {
    /**
     * 主键，自增
     */
    private Integer acid;
    /**
     * 模版编号
     */
    private Integer templateId;

    private String templateVersion;
    /**
     * 代码版本号，用户可自定义（长度不要超过64个字符）
     */
    private String userVersion;
    /**
     * 代码描述，用户可自定义
     */
    private String userDesc;
    /**
     * 对应小程序编号
     */
    private Integer wxAppAcid;
    private String wxAppName;
    /**
     * 提交结果
     */
    private String wxCommitCode;
    /**
     * 提交错误信息
     */
    private String wxCommitMsg;
    /**
     * 状态
     */
    private Integer status;
    /**
     *  提交审核结果
     */
    private String wxSubmitCode;
    /**
     * 提交审核返回信息
     */
    private String wxSubmitMsg;
    /**
     * 提交审核项的一个列表（至少填写1项，至多填写5项）
     */
    private String itemList;
    /**
     * 反馈内容，不超过200字
     */
    private String feedbackInfo;
    /**
     * 图片media_id列表
     */
    private String feedbackStuff;
    /**
     * 审核编号
     */
    private Integer auditId;
    /**
     * 创建人
     */
    private Integer createUser;
    /**
     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 提交人
     */
    private Integer submitUser;
    /**
     * 提交时间
     */
    private Timestamp submitTime;
    /**
     * 提交审核人
     */
    private Integer auditUser;
    /**
     * 提交审核时间
     */
    private Timestamp auditTime;
    /**
     * 发布人
     */
    private Integer releaseUser;
    /**
     * 发布时间
     */
    private Timestamp releaseTime;
    /**
     * 回退人
     */
    private Integer revertUser;
    /**
     * 回退时间
     */
    private Timestamp revertTime;

    private Timestamp updateTime;
}
