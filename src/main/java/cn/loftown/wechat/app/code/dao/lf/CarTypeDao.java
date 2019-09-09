package cn.loftown.wechat.app.code.dao.lf;

import cn.loftown.wechat.app.code.dto.lf.CarTypeDTO;

import java.util.List;

public interface CarTypeDao {
    int deleteByPrimaryKey(Integer acid);

    int insert(CarTypeDTO record);

    CarTypeDTO selectByPrimaryKey(Integer acid);

    List<CarTypeDTO> selectAll();

    int updateByPrimaryKey(CarTypeDTO record);
}
