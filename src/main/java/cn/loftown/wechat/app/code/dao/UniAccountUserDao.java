package cn.loftown.wechat.app.code.dao;

import cn.loftown.wechat.app.code.dto.UniAccountUserDTO;

import java.util.List;

public interface UniAccountUserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(UniAccountUserDTO record);

    UniAccountUserDTO selectByPrimaryKey(Integer id);

    List<UniAccountUserDTO> selectAll();

    int updateByPrimaryKey(UniAccountUserDTO record);
}
