package cn.loftown.wechat.app.code.dto.lf;

import lombok.Data;

import java.util.Date;

/**
 * 试驾车详情展示
 */
@Data
public class CarTypeInfoDTO {
    private Integer acid;

    private Integer uniacid;

    private Integer carTypeAcid;
    /**
     * 主标题
     */
    private String name;
    /**
     * 副标题
     */
    private String title;
    /**
     * Banner图片
     */
    private String banner;
    /**
     * 联系号码
     */
    private String storefrontPhone;

    private Integer status;

    private Date updateTime;

    private Integer updateAdminUser;

    private Date createTime;

    private Integer createAdminUser;
    /**
     * 车型详情
     */
    private String detailHtml;
    /**
     * 车型参数
     */
    private String parameterHtml;
}
