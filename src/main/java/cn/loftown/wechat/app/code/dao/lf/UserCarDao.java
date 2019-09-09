package cn.loftown.wechat.app.code.dao.lf;

import cn.loftown.wechat.app.code.dto.lf.UserCarDTO;

import java.util.List;

public interface UserCarDao {
    int deleteByPrimaryKey(Integer acid);

    int insert(UserCarDTO record);

    UserCarDTO selectByPrimaryKey(Integer acid);

    List<UserCarDTO> selectAll();

    int updateByPrimaryKey(UserCarDTO record);
}
