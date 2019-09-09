package cn.loftown.wechat.app.code.dto;

import lombok.Data;

@Data
public class UniAccountDTO {
    private Integer uniacid;

    private Integer groupid;

    private String name;

    private String description;

    private Integer defaultAcid;

    private Integer rank;

    private String titleInitial;

}
