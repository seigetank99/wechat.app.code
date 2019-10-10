package cn.loftown.wechat.app.code.entity.lf;

import cn.loftown.wechat.app.code.entity.BaseModel;
import lombok.Data;

@Data
public class CarTypeModel extends BaseModel {
    /**
     * 车型名称
     */
    private String name;
    /**
     * 车型图片
     */
    private String imageUrl;
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
     * 创建人
     */
    private Integer createUser;
}
