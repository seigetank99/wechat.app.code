package cn.loftown.wechat.app.code.dto.lf;

import lombok.Data;

import java.util.Date;

/**
 * 车辆类型表
 */
@Data
public class CarTypeDTO {
    private Integer acid;

    private Integer uniacid;
    /**
     * 车型名称
     */
    private String name;
    /**
     * 车型图片
     */
    private String imageUrl;
    /**
     * 车型状态
     */
    private Integer status;
    /**
     * 车型类型 1试驾 2预约 3车型
     */
    private Integer typeId;
    /**
     * 排序
     */
    private Integer orderId;
    /**
     * 厂家指导价
     */
    private Long proposedPrice;
    /**
     * 所属车型 0不属于任何车型
     */
    private Integer parentAcid;
    /**
     * 最后更新时间
     */
    private Date updateTime;
    /**
     * 创建人
     */
    private Integer createUser;
    /**
     * 创建时间
     */
    private Date createTime;
}
