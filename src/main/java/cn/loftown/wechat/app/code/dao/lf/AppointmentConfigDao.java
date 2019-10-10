package cn.loftown.wechat.app.code.dao.lf;

import cn.loftown.wechat.app.code.dto.lf.AppointmentConfigDTO;
import org.apache.ibatis.annotations.Param;

public interface AppointmentConfigDao {
    int deleteByPrimaryKey(Integer acid);

    int insert(AppointmentConfigDTO record);

    AppointmentConfigDTO selectByPrimaryKey(Integer acid);

    AppointmentConfigDTO getConfigByUniacid(@Param("uniacid") Integer uniacid);

    int updateByPrimaryKey(AppointmentConfigDTO record);
}
