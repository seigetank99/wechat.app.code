package cn.loftown.wechat.app.code.dao.lf;

import cn.loftown.wechat.app.code.dto.lf.AppointmentConfigDTO;

import java.util.List;

public interface AppointmentConfigDao {
    int deleteByPrimaryKey(Integer acid);

    int insert(AppointmentConfigDTO record);

    AppointmentConfigDTO selectByPrimaryKey(Integer acid);

    List<AppointmentConfigDTO> selectAll();

    int updateByPrimaryKey(AppointmentConfigDTO record);
}
