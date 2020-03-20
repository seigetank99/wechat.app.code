package cn.loftown.wechat.app.code.biz;

import cn.loftown.wechat.app.code.dao.CoreCacheDao;
import cn.loftown.wechat.app.code.dto.CoreCacheDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CacheBll {
    @Autowired
    CoreCacheDao coreCacheDao;

    private boolean isOpenRedis = false;
    public void set(String key, String value){
        if(isOpenRedis){

        } else {
            CoreCacheDTO cacheDTO = new CoreCacheDTO();
            cacheDTO.setKey(key);
            cacheDTO.setValue(value);
            coreCacheDao.updateCacheInfo(cacheDTO);
        }
    }
}
