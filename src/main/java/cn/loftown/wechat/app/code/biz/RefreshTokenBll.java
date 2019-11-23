package cn.loftown.wechat.app.code.biz;

import cn.loftown.wechat.app.code.dao.AccountWechatDao;
import cn.loftown.wechat.app.code.dao.AccountWxappDao;
import cn.loftown.wechat.app.code.dao.CoreCacheDao;
import cn.loftown.wechat.app.code.dto.AccountWechatDTO;
import cn.loftown.wechat.app.code.dto.AccountWxappDTO;
import cn.loftown.wechat.app.code.dto.CoreCacheDTO;
import cn.loftown.wechat.app.code.entity.AccessTokenModel;
import cn.loftown.wechat.app.code.entity.ComponentModel;
import cn.loftown.wechat.app.code.enums.AppTypeEnum;
import cn.loftown.wechat.app.code.util.HttpUtil;
import cn.loftown.wechat.app.code.util.TransforUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;

@Component
public class RefreshTokenBll {
    /**
     * 访问缓存数据库
     */
    @Autowired
    private CoreCacheDao coreCacheDao;
    @Autowired
    private AccountWechatDao accountWechatDao;
    @Autowired
    private AccountWxappDao accountWxappDao;
    @Autowired
    private ComponentBll componentBll;

    private Long boundary = 2 * 60 * 1000L;

    /**
     * 获取平台自身的accessToken
     * @return
     */
    public String getComponentAccessToken() throws Exception{
        String componentAccessKey = "account:component:assesstoken";
        CoreCacheDTO componentCacheDTO = coreCacheDao.getCacheValue(componentAccessKey);
        if(componentCacheDTO != null) {
            AccessTokenModel accessTokenModel = TransforUtil.getComponentAccessToken(componentCacheDTO.getValue());
            if (accessTokenModel.getTimeOutDate().getTime() > System.currentTimeMillis() + boundary) {
                return accessTokenModel.getAccessToken();
            }
        }
        CoreCacheDTO ticketCacheDTO = coreCacheDao.getCacheValue("account:ticket");
        if(ticketCacheDTO == null){
            return null;
        }

        HashMap<String,String> hashMap = TransforUtil.formatPhpDataToMap(ticketCacheDTO.getValue());

        //查询下平台的AppID
        ComponentModel componentModel = componentBll.getComponentInfo();
        if(componentModel == null || StringUtils.isEmpty(componentModel.getAppId())){
            return null;
        }

        JSONObject request = new JSONObject();
        request.put("component_appid", componentModel.getAppId());
        request.put("component_appsecret", componentModel.getAppsecret());
        request.put("component_verify_ticket", hashMap.get("value"));

        String response = HttpUtil.doPost("https://api.weixin.qq.com/cgi-bin/component/api_component_token", request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        String componentAccessToken = jsonObject.getString("component_access_token");
        Long expires = jsonObject.getLong("expires_in") * 1000;

        String value = TransforUtil.formatMapToPhpData(componentAccessToken, System.currentTimeMillis() + expires);

        //拿到结果后刷新数据库的记录
        saveToDB(componentCacheDTO, componentAccessKey, value);

        return componentAccessToken;
    }

    /**
     * 获取授权应用的accessToken
     * @param acid 主键
     * @param appType 1公众号 2小程序
     * @return
     */
    public String getAuthorizerAccessToken(int acid, AppTypeEnum appType) throws Exception{
        String authorizerAppID = "";
        String authorizerRefreshToken = "";
        String authorizerAccessKey = "";

        //根据类型查询下认证实体的authorizerAppID和authorizerRefreshToken
        if(AppTypeEnum.WECHATSERVICE.equals(appType)){
            AccountWechatDTO accountWechatDTO = accountWechatDao.getModelById(acid);
            authorizerAppID = accountWechatDTO.getKey();
            authorizerRefreshToken = accountWechatDTO.getAuth_refresh_token();
            authorizerAccessKey = "account:auth:accesstoken:"+ authorizerAppID;
        } else if(AppTypeEnum.WECHATMINIAPP.equals(appType)){
            AccountWxappDTO accountWxappDTO = accountWxappDao.getModelById(acid);
            authorizerAppID = accountWxappDTO.getKey();
            authorizerRefreshToken = accountWxappDTO.getAuth_refresh_token();
            authorizerAccessKey = "accesstoken:"+ authorizerAppID;
        }

        //有为空说明数据异常，记录日志返回
        if(StringUtils.isEmpty(authorizerAppID) || StringUtils.isEmpty(authorizerRefreshToken) || StringUtils.isEmpty(authorizerAccessKey)){
            return null;
        }

        //先去数据库查询下是否有AccessToken，有了的话验证下是否过期。未过期直接返回，已过期从新获取
        CoreCacheDTO authorCacheDTO = coreCacheDao.getCacheValue(authorizerAccessKey);
        if(authorCacheDTO != null){
            AccessTokenModel accessTokenModel = TransforUtil.getComponentAccessToken(authorCacheDTO.getValue());
            if(accessTokenModel.getTimeOutDate().getTime() > System.currentTimeMillis() + boundary){
                return accessTokenModel.getAccessToken();
            }
        }
        //查询下平台的AppID
        ComponentModel componentModel = componentBll.getComponentInfo();
        if(componentModel == null || StringUtils.isEmpty(componentModel.getAppId())){
            return null;
        }
        //调用微信的接口，刷新AccessToken
        JSONObject request = new JSONObject();
        request.put("component_appid",componentModel.getAppId());
        request.put("authorizer_appid", authorizerAppID);
        request.put("authorizer_refresh_token",authorizerRefreshToken);
        String response = HttpUtil.doPost("https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token=" + getComponentAccessToken(), request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        String authorizerAccessToken = jsonObject.getString("authorizer_access_token");
        Long expires = jsonObject.getLong("expires_in") * 1000;

        String value = TransforUtil.formatMapToPhpData(authorizerAccessToken, System.currentTimeMillis() + expires);

        //拿到结果后刷新数据库的记录
        saveToDB(authorCacheDTO, authorizerAccessKey, value);

        return authorizerAccessToken;
    }

    private void saveToDB(CoreCacheDTO coreCache, String key, String value){
        if(coreCache == null){
            coreCache = new CoreCacheDTO();
            coreCache.setKey(key);
            coreCache.setValue(value);
            coreCacheDao.addCacheInfo(coreCache);
        } else {
            coreCache.setKey(key);
            coreCache.setValue(value);
            coreCacheDao.updateCacheInfo(coreCache);
        }
    }
}
