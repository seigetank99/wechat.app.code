package cn.loftown.wechat.app.code.model;

import lombok.Data;

@Data
public class GetWxappCommitRequest {
    private Integer wxAppId;
    private Integer statusId;
}
