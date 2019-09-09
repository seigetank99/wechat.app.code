package cn.loftown.wechat.app.code.dao.lf;

import cn.loftown.wechat.app.code.dto.lf.CarTypeInfoDTO;

import java.util.List;

public interface CarTypeInfoDao {
    int deleteByPrimaryKey(Integer acid);

    int insert(CarTypeInfoDTO record);

    CarTypeInfoDTO selectByPrimaryKey(Integer acid);

    List<CarTypeInfoDTO> selectAll();

    int updateByPrimaryKey(CarTypeInfoDTO record);
}
