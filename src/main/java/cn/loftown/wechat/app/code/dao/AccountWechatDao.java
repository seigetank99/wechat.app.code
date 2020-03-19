package cn.loftown.wechat.app.code.dao;

import cn.loftown.wechat.app.code.dto.AccountWechatDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AccountWechatDao {
    @Select("select * from ims_account_wechats where `acid` = #{acid}")
    AccountWechatDTO getModelById(@Param("acid") Integer acid);

    List<AccountWechatDTO> getModelByPage(@Param("pageStart") Integer pageStart);

    @Update("UPDATE ims_account_wechats SET `head_img`=#{head_img},`service_type_info`=#{service_type_info},`verify_type_info`=#{verify_type_info},`alias`=#{alias},`qrcode_url`=#{qrcode_url},`principal_name`=#{principal_name} WHERE `acid`= #{acid}")
    void updateInfo(AccountWechatDTO accountWechatDTO);

    int insert(AccountWechatDTO record);

    int updateByPrimaryKey(AccountWechatDTO record);
}
