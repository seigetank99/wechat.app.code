package cn.loftown.wechat.app.code.dao.lf;

import cn.loftown.wechat.app.code.dto.lf.AppointmentOrderDTO;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

public interface AppointmentOrderDao {
    int deleteByPrimaryKey(Integer acid);

    int insert(AppointmentOrderDTO record);

    AppointmentOrderDTO selectByPrimaryKey(Integer acid);

    List<AppointmentOrderDTO> getOrderByUser(@Param("userId") Integer userId, @Param("orderStatus") Integer orderStatus,
                                             @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime);

    List<AppointmentOrderDTO> getOrderByAdmin(@Param("uniacid") Integer uniacid, @Param("orderStatus") Integer orderStatus,
                                             @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime);

    int updateByPrimaryKey(AppointmentOrderDTO record);
}
