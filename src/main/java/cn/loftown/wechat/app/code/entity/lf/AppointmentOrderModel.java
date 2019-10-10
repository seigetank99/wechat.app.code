package cn.loftown.wechat.app.code.entity.lf;

import cn.loftown.wechat.app.code.entity.BaseModel;
import cn.loftown.wechat.app.code.enums.OrderCancelTypeEnum;
import cn.loftown.wechat.app.code.enums.OrderStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class AppointmentOrderModel extends BaseModel {

    private Integer userId;

    private OrderStatusEnum orderStatus;

    private Integer orderStatusCode;

    private String contactName;

    private String contactMobile;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    private Date appointmentTime;

    private Integer mileage;

    private String carTypeName;

    private String carNumber;

    private Integer userCarAcid;

    private OrderCancelTypeEnum cancelType;

    private Integer cancelTypeCode;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private Integer operateAdminUser;
}
