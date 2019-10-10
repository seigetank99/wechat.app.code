package cn.loftown.wechat.app.code.entity;

import lombok.Data;

@Data
public class CommitDomainModel {
    private String action;
    private String webAction;
    private Integer acid;
    private String name;
    private String requestdomain;
    private String wsrequestdomain;
    private String uploaddomain;
    private String downloaddomain;
    private String webviewdomain;
}
