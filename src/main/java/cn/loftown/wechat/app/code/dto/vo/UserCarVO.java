package cn.loftown.wechat.app.code.dto.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserCarVO {
    private Integer acid;

    private Integer userid;
    /**
     * 车型名称
     */
    private String carTypeName;
    /**
     * 车型图片
     */
    private String carTypeImg;
    /**
     * 车牌号码
     */
    private String carNumber;
    /**
     * 里程数
     */
    private Integer mileage;
    /**
     * 最近一次预约时间
     */
    private Timestamp lastAppointmentTime;
}
