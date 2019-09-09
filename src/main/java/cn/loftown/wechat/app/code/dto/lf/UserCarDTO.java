package cn.loftown.wechat.app.code.dto.lf;

import lombok.Data;

import java.util.Date;

@Data
public class UserCarDTO {
    private Integer acid;

    private Integer uniacid;

    private Integer userid;

    private Integer carTypeAcid;

    private String carNumber;

    private Short status;

    private Date updateTime;

    private Date createTime;
}
