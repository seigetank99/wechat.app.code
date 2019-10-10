package cn.loftown.wechat.app.code.entity.lf;

import cn.loftown.wechat.app.code.entity.BaseModel;
import lombok.Data;

@Data
public class AppointmentConfigModel extends BaseModel {

    private Integer operateAdminUser;

    /**
     * 是否自动确认订单
     */
    private Integer autoConfirm;

    /**
     * 至少提前时间
     */
    private Integer advanceNum;
    private Integer advanceType;

    /**
     * 最长预订天数
     */
    private Integer reserveNum;
    private Integer reserveType;

    /**
     * 预约失效时长
     */
    private Integer hasInvalid;
    private Integer invalidNum;
    private Integer invalidType;

    /**
     * 预约时段配置
     */
    private String selectViewData;
}
