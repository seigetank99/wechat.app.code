package cn.loftown.wechat.app.code.dao.lf;

import cn.loftown.wechat.app.code.dto.lf.WxappBindTesterDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface WxappBindTesterDao {

    @Options(useGeneratedKeys = true, keyProperty = "acid", keyColumn = "acid")
    int insert(WxappBindTesterDTO record);

    List<WxappBindTesterDTO> selectByWxAppId(@Param("wxAppId") Integer wxAppId, @Param("status") Integer status);

    @Update("UPDATE lf_wxapp_bind_tester SET `status`=#{status} WHERE `acid`= #{acid}")
    void updateStatus(@Param("acid") Integer acid, @Param("status") Integer status);

    int deleteByPrimaryKey(Integer acid);

    WxappBindTesterDTO selectByPrimaryKey(Integer acid);

    List<WxappBindTesterDTO> selectAll();

    int updateByPrimaryKey(WxappBindTesterDTO record);
}
