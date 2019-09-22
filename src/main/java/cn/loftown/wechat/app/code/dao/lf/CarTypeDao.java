package cn.loftown.wechat.app.code.dao.lf;

import cn.loftown.wechat.app.code.dto.lf.CarTypeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarTypeDao {
    int deleteByPrimaryKey(Integer acid);

    int insert(CarTypeDTO record);

    CarTypeDTO selectByPrimaryKey(Integer acid);

    List<CarTypeDTO> selectAll();

    int getCarTypeCount(@Param("uniacid") Integer uniacid, @Param("statusId") Integer statusId, @Param("typeId") Integer typeId, @Param("name") String name);

    int updateByPrimaryKey(CarTypeDTO record);
}
