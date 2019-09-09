package cn.loftown.wechat.app.code.dto.lf;

import lombok.Data;

import java.util.Date;

@Data
public class CarTypeDTO {
    private Integer acid;

    private Integer uniacid;

    private String name;

    private String imageUrl;

    private Short status;

    private Short typeId;

    private Integer orderId;

    private Long proposedPrice;

    private Integer parentAcid;

    private Date updateTime;

    private Integer createUser;

    private Date createTime;
}
