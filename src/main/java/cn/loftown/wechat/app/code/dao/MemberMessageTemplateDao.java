package cn.loftown.wechat.app.code.dao;

import cn.loftown.wechat.app.code.dto.MemberMessageTemplateDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberMessageTemplateDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByUniacid(Integer uniacid);

    int insert(MemberMessageTemplateDTO record);

    MemberMessageTemplateDTO selectByPrimaryKey(Integer id);

    List<MemberMessageTemplateDTO> selectByTemplateName(@Param("uniacid")Integer uniacid, @Param("templateName")String templateName);

    int updateByPrimaryKey(MemberMessageTemplateDTO record);
}
