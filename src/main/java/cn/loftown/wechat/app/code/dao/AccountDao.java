package cn.loftown.wechat.app.code.dao;

import cn.loftown.wechat.app.code.dto.AccountDTO;
import org.apache.ibatis.annotations.Options;

import java.util.List;

public interface AccountDao {

    int deleteByPrimaryKey(Integer acid);

    @Options(useGeneratedKeys = true, keyProperty = "acid", keyColumn = "acid")
    int insert(AccountDTO record);

    AccountDTO selectByPrimaryKey(Integer acid);

    List<AccountDTO> selectAll();

    int updateByPrimaryKey(AccountDTO record);
}
