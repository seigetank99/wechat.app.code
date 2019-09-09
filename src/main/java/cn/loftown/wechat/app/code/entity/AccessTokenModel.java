package cn.loftown.wechat.app.code.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AccessTokenModel {
    private String accessToken;
    private Date timeOutDate;
}
