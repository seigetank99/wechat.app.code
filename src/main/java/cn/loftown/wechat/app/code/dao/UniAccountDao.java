package cn.loftown.wechat.app.code.dao;

import cn.loftown.wechat.app.code.dto.UniAccountDTO;
import org.apache.ibatis.annotations.Options;

import java.util.List;

public interface UniAccountDao {
    int deleteByPrimaryKey(Integer uniacid);

    @Options(useGeneratedKeys = true, keyProperty = "uniacid", keyColumn = "uniacid")
    int insert(UniAccountDTO record);

    UniAccountDTO selectByPrimaryKey(Integer uniacid);

    List<UniAccountDTO> selectAll();

    int updateByPrimaryKey(UniAccountDTO record);
}
