package cn.loftown.wechat.app.code.model.lf;

import lombok.Data;

import java.util.Date;

@Data
public class AppointmentOrderModel {
    private Integer acid;

    private Integer uniacid;

    private Integer userid;

    private String contactName;

    private String contactMobile;

    private Date appointmentTime;

    private Integer mileage;

    private String carTypeName;

    private Integer userCarAcid;

    private Integer cancelType;

    private Integer operateAdminUser;
}
