package cn.loftown.wechat.app.code.dao.lf;

import cn.loftown.wechat.app.code.dto.lf.WxAppCodeSubmitDTO;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

public interface WxappCodeSubmitDao {

    int deleteByPrimaryKey(Integer acid);

    @Options(useGeneratedKeys = true, keyProperty = "acid", keyColumn = "acid")
    int insert(WxAppCodeSubmitDTO record);

    WxAppCodeSubmitDTO selectByPrimaryKey(Integer acid);

    List<WxAppCodeSubmitDTO> selectByWxApp(@Param("wxAppAcId")Integer wxAppAcId, @Param("statusList")List<Integer> statusList,
                                           @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime);
    int selectByWxAppCount(@Param("wxAppAcId")Integer wxAppAcId, @Param("statusList")List<Integer> statusList,
                                           @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime);

    int updateByPrimaryKey(WxAppCodeSubmitDTO record);

    /**
     * 查询一条最近审核成功的记录
     * @param wxAppAcId
     * @return
     */
    WxAppCodeSubmitDTO selectByItemList(@Param("wxAppAcId")Integer wxAppAcId);
}
