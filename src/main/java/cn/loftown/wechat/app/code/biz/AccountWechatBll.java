package cn.loftown.wechat.app.code.biz;

import cn.loftown.wechat.app.code.dao.AccountDao;
import cn.loftown.wechat.app.code.dao.AccountWechatDao;
import cn.loftown.wechat.app.code.dao.UniAccountDao;
import cn.loftown.wechat.app.code.dao.UniAccountUserDao;
import cn.loftown.wechat.app.code.dto.AccountDTO;
import cn.loftown.wechat.app.code.dto.AccountWechatDTO;
import cn.loftown.wechat.app.code.dto.UniAccountDTO;
import cn.loftown.wechat.app.code.dto.UniAccountUserDTO;
import cn.loftown.wechat.app.code.entity.AccountWechatModel;
import cn.loftown.wechat.app.code.entity.ComponentModel;
import cn.loftown.wechat.app.code.enums.WechatLevelEnum;
import cn.loftown.wechat.app.code.enums.WechatServiceTypeEnum;
import cn.loftown.wechat.app.code.enums.WechatVerifyTypeEnum;
import cn.loftown.wechat.app.code.util.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class AccountWechatBll {
    @Autowired
    AccountWechatDao accountWechatDao;
    @Autowired
    UniAccountDao uniAccountDao;
    @Autowired
    AccountDao accountDao;
    @Autowired
    UniAccountUserDao uniAccountUserDao;
    @Autowired
    RefreshTokenBll refreshTokenBll;
    @Autowired
    ComponentBll componentBll;

    /**
     * 分页查询公众号列表
     * @param pageIndex
     * @return
     */
    public List<AccountWechatModel> getAccountWechatsByPage(int pageIndex){
        List<AccountWechatModel> wechatModelList = new ArrayList<>();
        int pageStart = (pageIndex - 1) * 20;
        List<AccountWechatDTO> wechatDTOList = accountWechatDao.getModelByPage(pageStart);
        if(wechatDTOList != null && wechatDTOList.size() > 0){
            for (AccountWechatDTO wechatDTO : wechatDTOList){
                wechatModelList.add(getAccountWechatDetail(wechatDTO));
            }
        }
        return wechatModelList;
    }

    /**
     * 根据acid查询公众号详情
     * @param acid
     * @return
     */
    public AccountWechatModel getAccountWechatDetail(int acid){
        AccountWechatDTO wechatDTO = accountWechatDao.getModelById(acid);
        return getAccountWechatDetail(wechatDTO);
    }

    /**
     * DTO转换成Model
     * @param wechatDTO
     * @return
     */
    public AccountWechatModel getAccountWechatDetail(AccountWechatDTO wechatDTO){
        AccountWechatModel wechatModel = new AccountWechatModel();
        wechatModel.setAcid(wechatDTO.getAcid());
        wechatModel.setAccount(wechatDTO.getAccount());
        wechatModel.setAppId(wechatDTO.getKey());
        wechatModel.setLevelEnum(WechatLevelEnum.parse(wechatDTO.getLevel()));
        wechatModel.setName(wechatDTO.getName());
        wechatModel.setOriginal(wechatDTO.getOriginal());
        if(StringUtils.isEmpty(wechatDTO.getAuth_refresh_token())){
            wechatModel.setHasAuth(false);
        } else {
            wechatModel.setHasAuth(true);
        }
        //如果扩展信息没值，去调用微信的接口查询下
        if(StringUtils.isEmpty(wechatDTO.getHead_img())){
            completionAccountWechatDetail(wechatModel);
            //从微信那边查询到了扩展信息，落地到表里
            if(!StringUtils.isEmpty(wechatModel.getHeadImg())){
                updateAccountWechat(wechatModel);
            }
        } else {
            wechatModel.setHeadImg(wechatDTO.getHead_img());
            wechatModel.setServiceTypeEnum(WechatServiceTypeEnum.parse(wechatDTO.getService_type_info()));
            wechatModel.setVerifyTypeEnum(WechatVerifyTypeEnum.parse(wechatDTO.getVerify_type_info()));
            wechatModel.setAlias(wechatDTO.getAlias());
            wechatModel.setQrcodeUrl(wechatDTO.getQrcode_url());
            wechatModel.setPrincipalName(wechatDTO.getPrincipal_name());
        }

        return wechatModel;
    }

    /**
     * 更新微信公众号的扩展信息
     * @param wechatModel
     */
    public void updateAccountWechat(AccountWechatModel wechatModel){
        AccountWechatDTO wechatDTO = new AccountWechatDTO();
        wechatDTO.setAcid(wechatModel.getAcid());
        wechatDTO.setHead_img(wechatModel.getHeadImg());
        wechatDTO.setService_type_info(wechatModel.getServiceTypeEnum().getCode());
        wechatDTO.setVerify_type_info(wechatModel.getVerifyTypeEnum().getCode());
        wechatDTO.setAlias(wechatModel.getAlias());
        wechatDTO.setQrcode_url(wechatModel.getQrcodeUrl());
        wechatDTO.setPrincipal_name(wechatModel.getPrincipalName());
        accountWechatDao.updateInfo(wechatDTO);
    }

    public void createWeChatByAdminUser(Integer userId, String adminUser){
        UniAccountDTO uniAccountDTO = new UniAccountDTO();
        uniAccountDTO.setName("");
        uniAccountDTO.setDescription("");
        uniAccountDTO.setGroupid(0);
        uniAccountDTO.setDefaultAcid(0);
        uniAccountDTO.setRank(0);
        uniAccountDTO.setTitleInitial("");
        uniAccountDao.insert(uniAccountDTO);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setUniacid(uniAccountDTO.getUniacid());
        accountDTO.setType((byte) 3);
        accountDTO.setHash(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        accountDTO.setIsconnect((byte) 1);
        accountDTO.setIsdeleted((byte) 0);
        accountDTO.setEndtime(System.currentTimeMillis());
        accountDao.insert(accountDTO);

        AccountWechatDTO accountWechatDTO = new AccountWechatDTO();
        accountWechatDTO.setAcid(accountDTO.getAcid());
        accountWechatDTO.setUniacid(uniAccountDTO.getUniacid());
        accountWechatDTO.setName(adminUser);
        accountWechatDTO.setToken("");
        accountWechatDTO.setEncodingaeskey("");
        accountWechatDTO.setLevel(0);
        accountWechatDTO.setAccount("");
        accountWechatDTO.setOriginal("");
        accountWechatDTO.setSignature("");
        accountWechatDTO.setCountry("");
        accountWechatDTO.setProvince("");
        accountWechatDTO.setCity("");
        accountWechatDTO.setUsername("");
        accountWechatDTO.setPassword("");
        accountWechatDTO.setLastupdate(System.currentTimeMillis());
        accountWechatDTO.setKey("");
        accountWechatDTO.setSecret("");
        accountWechatDTO.setSubscribeurl("");
        accountWechatDTO.setAuth_refresh_token("");
        accountWechatDao.insert(accountWechatDTO);

        UniAccountUserDTO accountUserDTO = new UniAccountUserDTO();
        accountUserDTO.setRole("owner");
        accountWechatDTO.setUniacid(uniAccountDTO.getUniacid());
        accountUserDTO.setUid(userId);
        accountUserDTO.setRank((byte) 0);
        uniAccountUserDao.insert(accountUserDTO);
    }

    /**
     * 去微信查询公众号的扩展信息
     * @param accountWechatModel
     */
    private void completionAccountWechatDetail(AccountWechatModel accountWechatModel){
        //没有APPID的和未授权的公众号不查询扩展信息
        if(StringUtils.isEmpty(accountWechatModel.getAppId()) || accountWechatModel.getHasAuth() == false){
            return;
        }
        try {
            String componentAccessToken = refreshTokenBll.getComponentAccessToken();
            ComponentModel componentModel = componentBll.getComponentInfo();
            JSONObject request = new JSONObject();
            request.put("component_appid", componentModel.getAppId());
            request.put("authorizer_appid", accountWechatModel.getAppId());

            String response = HttpUtil.doPost("https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token="+ componentAccessToken, request.toJSONString());
            JSONObject responseJson = JSONObject.parseObject(response);

            JSONObject jsonObject = responseJson.getJSONObject("authorizer_info");
            accountWechatModel.setHeadImg(jsonObject.getString("head_img"));
            JSONObject serviceType = jsonObject.getJSONObject("service_type_info");
            accountWechatModel.setServiceTypeEnum(WechatServiceTypeEnum.parse(serviceType.getInteger("id")));
            JSONObject verifyType = jsonObject.getJSONObject("verify_type_info");
            accountWechatModel.setVerifyTypeEnum(WechatVerifyTypeEnum.parse(verifyType.getInteger("id")));
            accountWechatModel.setAlias(jsonObject.getString("alias"));
            accountWechatModel.setQrcodeUrl(jsonObject.getString("qrcode_url"));
            accountWechatModel.setPrincipalName(jsonObject.getString("principal_name"));
        } catch (Exception ex){

        }
    }
}
