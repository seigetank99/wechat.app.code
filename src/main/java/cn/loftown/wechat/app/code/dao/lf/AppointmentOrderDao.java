package cn.loftown.wechat.app.code.dao.lf;

import cn.loftown.wechat.app.code.dto.lf.AppointmentOrderDTO;

import java.util.List;

public interface AppointmentOrderDao {
    int deleteByPrimaryKey(Integer acid);

    int insert(AppointmentOrderDTO record);

    AppointmentOrderDTO selectByPrimaryKey(Integer acid);

    List<AppointmentOrderDTO> selectAll();

    int updateByPrimaryKey(AppointmentOrderDTO record);
}
