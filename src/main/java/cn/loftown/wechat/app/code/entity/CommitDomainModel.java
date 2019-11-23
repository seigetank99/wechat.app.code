package cn.loftown.wechat.app.code.entity;

import lombok.Data;

@Data
public class CommitDomainModel {
    private String requestAction;
    private String webAction;
    private Integer acid;
    private String WxAppName;
    private String requestdomain;
    private String wsrequestdomain;
    private String uploaddomain;
    private String downloaddomain;
    private String webviewdomain;
}
