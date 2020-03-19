package cn.loftown.wechat.app.code.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ShopSyssetDao {
    @Select("select sets from ims_ewei_shop_sysset where `uniacid` = #{uniacid}")
    String getSets(@Param("uniacid") Integer uniacid);

    @Select("select plugins from ims_ewei_shop_sysset where `uniacid` = #{uniacid}")
    String getPlugins(@Param("uniacid") Integer uniacid);

    @Select("select sec from ims_ewei_shop_sysset where `uniacid` = #{uniacid}")
    String getSecs(@Param("uniacid") Integer uniacid);

    @Update("UPDATE ims_ewei_shop_sysset SET `sets`=#{sets} WHERE `uniacid`= #{uniacid}")
    void updateSets(@Param("uniacid") Integer uniacid, @Param("sets") String sets);
}
