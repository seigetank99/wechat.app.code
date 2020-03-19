package cn.loftown.wechat.app.code.biz;

import cn.loftown.wechat.app.code.dao.CoreCacheDao;
import cn.loftown.wechat.app.code.dto.CoreCacheDTO;
import cn.loftown.wechat.app.code.entity.ComponentModel;
import cn.loftown.wechat.app.code.util.PHPTransformUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ComponentBll {

    @Autowired
    private CoreCacheDao coreCacheDao;

    public ComponentModel getComponentInfo(){
        //查询下平台的AppID
        CoreCacheDTO cacheDTO = coreCacheDao.getSettingValue("platform");
        if(cacheDTO == null){
            return null;
        }
        try {
            HashMap<String, String> platformMap = PHPTransformUtil.formatPhpDataToMap(cacheDTO.getValue());
            if(platformMap != null && platformMap.size() > 0){
                ComponentModel componentModel = new ComponentModel();
                componentModel.setAppId(platformMap.get("appid"));
                componentModel.setAuthstate(Integer.parseInt(platformMap.get("authstate")));
                componentModel.setAppsecret(platformMap.get("appsecret"));
                componentModel.setEncodingaesKey(platformMap.get("encodingaeskey"));
                componentModel.setToken(platformMap.get("token"));
                return componentModel;
            }
        } catch (Exception ex){
            //todo 异常处理
        }
        return null;
    }
}
