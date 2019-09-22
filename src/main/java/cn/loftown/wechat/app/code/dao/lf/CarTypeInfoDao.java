package cn.loftown.wechat.app.code.dao.lf;

import cn.loftown.wechat.app.code.dto.lf.CarTypeInfoDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarTypeInfoDao {
    int deleteByPrimaryKey(Integer acid);

    int insert(CarTypeInfoDTO record);

    CarTypeInfoDTO selectByPrimaryKey(Integer acid);

    List<CarTypeInfoDTO> selectAll();

    int getCarTypeInfoCount(@Param("uniacid") Integer uniacid, @Param("statusId") Integer statusId, @Param("carTypeAcid") Integer carTypeAcid);

    int updateByPrimaryKey(CarTypeInfoDTO record);
}
