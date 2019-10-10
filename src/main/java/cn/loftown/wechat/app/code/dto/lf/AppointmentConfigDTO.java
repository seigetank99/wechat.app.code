package cn.loftown.wechat.app.code.dto.lf;

import lombok.Data;

import java.util.Date;

@Data
public class AppointmentConfigDTO {
    private Integer acid;

    private Integer uniacid;

    /**
     * 是否自动确认订单
     */
    private Integer autoConfirm;

    /**
     * 至少提前时间
     */
    private Long advanceTime;

    /**
     * 最长预订天数
     */
    private Long reserveTime;

    /**
     * 预约失效时长
     */
    private Long invalidTime;

    /**
     * 预约时段配置
     */
    private String appointmentConfig;

    private Integer updateUser;

    private Date updateTime;

    private Integer createUser;

    private Date createTime;
}
