package cn.loftown.wechat.app.code.dao;

import cn.loftown.wechat.app.code.dto.CoreCacheDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface CoreCacheDao {
    @Select("select `value` from ims_core_cache where `key` = #{key,jdbcType = VARCHAR}")
    CoreCacheDTO getCacheValue(@Param("key") String key);

    @Select("select `value` from ims_core_settings where `key` = #{key}")
    CoreCacheDTO getSettingValue(@Param("key") String key);

    @Insert("INSERT INTO ims_core_cache(`key`,`value`) VALUES(#{key},#{value})")
    void addCacheInfo(CoreCacheDTO cacheDTO);

    @Update("UPDATE ims_core_cache SET `value`=#{value} WHERE `key`= #{key}")
    void updateCacheInfo(CoreCacheDTO cacheDTO);
}
