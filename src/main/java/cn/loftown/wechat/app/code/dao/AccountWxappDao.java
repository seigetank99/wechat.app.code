package cn.loftown.wechat.app.code.dao;

import cn.loftown.wechat.app.code.dto.AccountWxappDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@MapperScan
public interface AccountWxappDao {
    @Select("select * from ims_account_wxapp where `acid` = #{acid}")
    AccountWxappDTO getModelById(@Param("acid") Integer acid);

    @Select("select * from ims_account_wxapp where `uniacid` = #{uniacid}")
    AccountWxappDTO getModelByUniAcid(@Param("uniacid") Integer uniacid);

    @Select("select * from ims_account_wxapp where statusId = #{statusId} limit #{pageStart},20")
    List<AccountWxappDTO> getModelByPage(@Param("statusId") Integer statusId, @Param("pageStart") Integer pageStart);

    @Update("UPDATE ims_account_wxapp SET `principal_name`=#{principal_name},`head_img`=#{head_img} WHERE `acid`= #{acid}")
    void updateInfo(AccountWxappDTO accountWxappDTO);

    int deleteByPrimaryKey(Integer acid);

    int insert(AccountWxappDTO record);

    AccountWxappDTO selectByPrimaryKey(Integer acid);

    List<AccountWxappDTO> selectAll();

    int updateByPrimaryKey(AccountWxappDTO record);
}
