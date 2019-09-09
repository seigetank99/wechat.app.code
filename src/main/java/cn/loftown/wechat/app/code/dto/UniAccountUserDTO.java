package cn.loftown.wechat.app.code.dto;

import lombok.Data;

@Data
public class UniAccountUserDTO {
    private Integer id;

    private Integer uniacid;

    private Integer uid;

    private String role;

    private Byte rank;
}
