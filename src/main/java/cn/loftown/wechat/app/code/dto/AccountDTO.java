package cn.loftown.wechat.app.code.dto;

import lombok.Data;

@Data
public class AccountDTO {
    private Integer acid;

    private Integer uniacid;

    private String hash;

    private Byte type;

    private Byte isconnect;

    private Byte isdeleted;

    private Long endtime;
}
