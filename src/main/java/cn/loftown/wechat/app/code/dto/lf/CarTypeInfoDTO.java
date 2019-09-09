package cn.loftown.wechat.app.code.dto.lf;

import lombok.Data;

import java.util.Date;

@Data
public class CarTypeInfoDTO {
    private Integer acid;

    private Integer typeAcid;

    private Integer carTypeAcid;

    private String name;

    private String title;

    private String banner;

    private String storefrontPhone;

    private Date updateTime;

    private Integer updateAdminUser;

    private Date createTime;

    private Integer createAdminUser;

    private String detailHtml;

    private String parameterHtml;
}
