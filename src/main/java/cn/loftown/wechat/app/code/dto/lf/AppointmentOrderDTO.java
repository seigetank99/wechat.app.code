package cn.loftown.wechat.app.code.dto.lf;

import lombok.Data;

import java.util.Date;

@Data
public class AppointmentOrderDTO {
    private Integer acid;

    private Integer uniacid;

    private Integer userid;

    private Integer orderStatus;

    private String contactName;

    private String contactMobile;

    private Integer mileage;

    private String carTypeName;

    private Integer userCarAcid;

    private String carNumber;

    private Date appointmentTime;

    private Integer cancelType;

    private Date createTime;

    private Date confirmTime;

    private Date finishTime;

    private Integer cancelAdminUser;

    private Integer confirmAdminUser;

    private Integer finishAdminUser;

    private Date updateTime;

}
