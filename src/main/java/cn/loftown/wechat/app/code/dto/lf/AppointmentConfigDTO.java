package cn.loftown.wechat.app.code.dto.lf;

import lombok.Data;

import java.util.Date;

@Data
public class AppointmentConfigDTO {
    private Integer acid;

    private Integer uniacid;

    private Boolean autoConfirm;

    private String lastTime;

    private String langDay;

    private Date cancelTime;

    private String appointmentConfig;

    private Integer updateUser;

    private Date updateTime;

    private Integer createUser;

    private Date createTime;
}
