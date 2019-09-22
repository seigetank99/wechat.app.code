package cn.loftown.wechat.app.code.dao.lf;

import cn.loftown.wechat.app.code.dto.lf.UserCarDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserCarDao {
    int deleteByPrimaryKey(Integer acid);

    int insert(UserCarDTO record);

    UserCarDTO selectByPrimaryKey(Integer acid);

    List<UserCarDTO> getUserCar(@Param("uniacid") Integer uniacid, @Param("userId") Integer userId,
                                @Param("statusId") Integer statusId, @Param("carTypeAcid") Integer carTypeAcid);

    int getUserCarCountByUser(@Param("userId") Integer userId, @Param("statusId") Integer statusId, @Param("carNumber") String carNumber);

    int updateByPrimaryKey(UserCarDTO record);
}
