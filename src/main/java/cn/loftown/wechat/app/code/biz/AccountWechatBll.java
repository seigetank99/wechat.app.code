package cn.loftown.wechat.app.code.biz;

import cn.loftown.wechat.app.code.base.BaseResponse;
import cn.loftown.wechat.app.code.dao.*;
import cn.loftown.wechat.app.code.dto.*;
import cn.loftown.wechat.app.code.entity.AccountWechatModel;
import cn.loftown.wechat.app.code.entity.ComponentModel;
import cn.loftown.wechat.app.code.enums.AppTypeEnum;
import cn.loftown.wechat.app.code.enums.WechatLevelEnum;
import cn.loftown.wechat.app.code.enums.WechatServiceTypeEnum;
import cn.loftown.wechat.app.code.enums.WechatVerifyTypeEnum;
import cn.loftown.wechat.app.code.exception.PredictException;
import cn.loftown.wechat.app.code.model.BindOpenToWeChatRequest;
import cn.loftown.wechat.app.code.util.HttpUtil;
import cn.loftown.wechat.app.code.util.PHPTransformUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.phprpc.util.AssocArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.*;

@Component
public class AccountWechatBll {
    @Autowired
    AccountWechatDao accountWechatDao;
    @Autowired
    AccountWxappDao accountWxappDao;
    @Autowired
    UniAccountDao uniAccountDao;
    @Autowired
    AccountDao accountDao;
    @Autowired
    UniAccountUserDao uniAccountUserDao;
    @Autowired
    MemberMessageTemplateDao memberMessageTemplateDao;
    @Autowired
    ShopSyssetDao shopSyssetDao;
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

    /**
     * 保存公众号模版消息
     * @param acid
     * @return
     * @throws Exception
     */
    public BaseResponse setWeChatTemplateMessage(Integer acid) throws Exception{
        List<String> templateShortList = Arrays.asList(
                "OPENTM412822604","OPENTM411154202","OPENTM413796815",
                "OPENTM400239988","OPENTM201594720","TM00850",
                "OPENTM207498902","OPENTM414956350","OPENTM411154202",
                "OPENTM406590003","OPENTM412499953","OPENTM411733174",
                "OPENTM401905934","OPENTM407077809","OPENTM412075804",
                "OPENTM411207151","OPENTM401003199"
                );
        AccountWechatDTO accountWechatDTO = accountWechatDao.getModelById(acid);
        if (accountWechatDTO == null) {
            return new BaseResponse("参数异常，1001");
        }

        String accessToken = refreshTokenBll.getAuthorizerAccessToken(acid, AppTypeEnum.WECHATSERVICE);
        //查看下公众号消息的行业设置是否为"互联网|电子商务"，不是的话，设置为"互联网|电子商务"
        if(!getWeChatIndustry(accessToken)){
            setWeChatIndustry(accessToken);
        }

        //查询下公众号下的模版消息，操作之前要删除现有的模版消息
        JSONArray oldTemplateList = getWeChatTemplate(accessToken);
        if(oldTemplateList != null && oldTemplateList.size() > 0){
            for(int i = 0; i < oldTemplateList.size(); i++){
                JSONObject templateInfo = oldTemplateList.getJSONObject(i);
                delWeChatTemplate(accessToken, templateInfo.getString("template_id"));
            }
        }
        //添加新的模版
        for (String templateShort : templateShortList){
            addWeChatTemplate(accessToken, templateShort);
        }
        //查询下刚才添加的模版，要根据title去数据库匹配对应的模版配置
        JSONArray templateList = getWeChatTemplate(accessToken);

        HashMap<String,String> templateMap = new HashMap<>();
        for(int i = 0; i < templateList.size(); i++){
            JSONObject templateInfo = templateList.getJSONObject(i);
            templateMap.put(templateInfo.getString("title"), templateInfo.getString("template_id"));
        }

        //删除公众号原有的模版消息配置
        memberMessageTemplateDao.deleteByUniacid(accountWechatDTO.getUniacid());

        //查询unlaced=1的模版配置，复制给当前的公众号，替换TemplateId和uniacid
        List<MemberMessageTemplateDTO> templateDTOList = memberMessageTemplateDao.selectByTemplateName(1, null);
        if(templateDTOList != null && templateDTOList.size() > 0){
            for (MemberMessageTemplateDTO templateDTO : templateDTOList){
                if(!StringUtils.isEmpty(templateMap.get(templateDTO.getTemplate_name()))){
                    templateDTO.setId(null);
                    templateDTO.setTemplate_id(templateMap.get(templateDTO.getTemplate_name()));
                    templateDTO.setUniacid(accountWechatDTO.getUniacid());
                    memberMessageTemplateDao.insert(templateDTO);
                }
            }
        }

        return new BaseResponse();
    }

    public BaseResponse setWeChatTemplateMessageConfig(Integer acid) {
        AccountWechatDTO accountWechatDTO = accountWechatDao.getModelById(acid);
        if (accountWechatDTO == null) {
            return new BaseResponse("参数异常，1001");
        }

        List<MemberMessageTemplateDTO> templateDTOList = memberMessageTemplateDao.selectByTemplateName(accountWechatDTO.getUniacid(), null);

        if(templateDTOList == null || templateDTOList.size() == 0){
            return new BaseResponse("请先添加微信模版");
        }

        String appSets = shopSyssetDao.getSets(accountWechatDTO.getUniacid());

        AssocArray appAssocArray = PHPTransformUtil.getAppConfig(appSets, "notice");
        AssocArray app = PHPTransformUtil.getAssocArray(appSets);
        if(app == null){
            return new BaseResponse("获取配置异常");
        }
        if(appAssocArray == null){
            appAssocArray = new AssocArray();
        }
        List<String> templateList = Arrays.asList(
                "saler_pay","saler_finish","saler_stockwarn","saler_refund",
                "saler_goodpay","carrier","cancel","willcancel","pay","send",
                "virtualsend","orderstatus","refund1","refund3","refund4","refund2",
                "recharge_ok","withdraw_ok","backrecharge_ok","backpoint_ok","upgrade",
                "o2o_sverify","o2o_bverify");
        List<String> typeList = Arrays.asList("template","close_advanced","sms","close_sms");

        for (String template : templateList) {
            String key = template + "_template";
            Optional<MemberMessageTemplateDTO> templateDTO = templateDTOList.stream().filter(x -> x.getTypecode().equals(template)).findFirst();
            if(templateDTO.isPresent()){
                appAssocArray.set(key,templateDTO.get().getId().toString());
            }
        }

        app.set("notice", appAssocArray);
        appSets = PHPTransformUtil.getPHPContent(app);
        shopSyssetDao.updateSets(accountWechatDTO.getUniacid(), appSets);
        return new BaseResponse();
    }

    /**
     * 创建后台用户 todo没有测试过，不能直接用
     * @param userId
     * @param adminUser
     */
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

    /**
     * 授权后创建或者绑定公众号或小程序到第三方平台
     * @param request
     * @return
     */
    public BaseResponse bindOpenToWeChat(BindOpenToWeChatRequest request) throws Exception {
        AppTypeEnum appTypeEnum = AppTypeEnum.parse(request.getAppType());
        if (appTypeEnum == null || request.getAcid() < 1 || StringUtils.isEmpty(request.getBindType())) {
            return new BaseResponse("参数异常，1000");
        }

        String authorizerAppID = "";
        int uniacid = 0;
        //根据类型查询下认证实体的authorizerAppID和authorizerRefreshToken
        if (AppTypeEnum.WECHATSERVICE.equals(appTypeEnum)) {
            AccountWechatDTO accountWechatDTO = accountWechatDao.getModelById(request.getAcid());
            if (accountWechatDTO == null) {
                return new BaseResponse("参数异常，1001");
            }
            authorizerAppID = accountWechatDTO.getKey();
            uniacid = accountWechatDTO.getUniacid();
        } else if (AppTypeEnum.WECHATMINIAPP.equals(appTypeEnum)) {
            AccountWxappDTO accountWxappDTO = accountWxappDao.getModelById(request.getAcid());
            if (accountWxappDTO == null) {
                return new BaseResponse("参数异常，1002");
            }
            authorizerAppID = accountWxappDTO.getKey();
            uniacid = accountWxappDTO.getUniacid();
        } else {
            return new BaseResponse();
        }
        UniAccountDTO uniAccountDTO = uniAccountDao.selectByPrimaryKey(uniacid);
        if (uniAccountDTO == null) {
            return new BaseResponse("参数异常，1003");
        }
        //已经创建过平台了，就直接绑定
        if ("create".equals(request.getBindType()) && !StringUtils.isEmpty(uniAccountDTO.getWeChatOpen())) {
            request.setBindType("bind");
        }
        if ("bind".equals(request.getBindType()) && StringUtils.isEmpty(uniAccountDTO.getWeChatOpen())) {
            request.setBindType("create");
        }
        String accessToken = refreshTokenBll.getAuthorizerAccessToken(request.getAcid(), appTypeEnum);

        BaseResponse result = null;

        switch (request.getBindType()) {
            case "create":
                    result = createWeChatOpen(authorizerAppID, accessToken);
                break;
            case "bind":
                result = bindWeChatOpen(authorizerAppID, uniAccountDTO.getWeChatOpen(), accessToken);
                break;
            case "unbind":
                result = unbindWeChatOpen(authorizerAppID, uniAccountDTO.getWeChatOpen(), accessToken);
                break;
            case "get":
                result = getWeChatOpen(authorizerAppID, accessToken);
                break;
            default:
                return new BaseResponse();
        }
        if (result.getCode() != BaseResponse.ok()) {
            return result;
        }
        //绑定的话，记录下数据库的值
        if ("create".equals(request.getBindType()) || "bind".equals(request.getBindType())) {
            if (AppTypeEnum.WECHATSERVICE.equals(appTypeEnum)) {
                AccountWechatDTO updateDTO = new AccountWechatDTO();
                updateDTO.setAcid(request.getAcid());
                updateDTO.setBind_wechat_open(1);
                updateDTO.setBind_wechat_open_key(result.getData().toString());
                accountWechatDao.updateByPrimaryKey(updateDTO);
            } else if (AppTypeEnum.WECHATMINIAPP.equals(appTypeEnum)) {
                AccountWxappDTO updateDTO = new AccountWxappDTO();
                updateDTO.setAcid(request.getAcid());
                updateDTO.setBind_wechat_open(1);
                updateDTO.setBind_wechat_open_key(result.getData().toString());
                accountWxappDao.updateByPrimaryKey(updateDTO);
            }

            if ("create".equals(request.getBindType())) {
                UniAccountDTO updateDTO = new UniAccountDTO();
                updateDTO.setUniacid(uniacid);
                updateDTO.setWeChatOpen(result.getData().toString());
                uniAccountDao.updateByPrimaryKey(updateDTO);
            }
        }
        //解绑的话，更新下数据库
        if ("unbind".equals(request.getBindType())) {
            if (AppTypeEnum.WECHATSERVICE.equals(appTypeEnum)) {
                AccountWechatDTO updateDTO = new AccountWechatDTO();
                updateDTO.setAcid(request.getAcid());
                updateDTO.setBind_wechat_open(0);
                updateDTO.setBind_wechat_open_key("");
                accountWechatDao.updateByPrimaryKey(updateDTO);
            } else if (AppTypeEnum.WECHATMINIAPP.equals(appTypeEnum)) {
                AccountWxappDTO updateDTO = new AccountWxappDTO();
                updateDTO.setAcid(request.getAcid());
                updateDTO.setBind_wechat_open(0);
                updateDTO.setBind_wechat_open_key("");
                accountWxappDao.updateByPrimaryKey(updateDTO);
            }
        }

        return result;
    }

    /**
     * 创建并绑定公众号或小程序到第三方平台
     * @param appId
     * @param accessToken
     * @return
     * @throws Exception
     */
    private BaseResponse createWeChatOpen(String appId, String accessToken) throws Exception{
        JSONObject request = new JSONObject();
        request.put("appid", appId);
        String response = HttpUtil.doPost("https://api.weixin.qq.com/cgi-bin/open/create?access_token=" + accessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        BaseResponse result = BaseResponse.getInstance(jsonObject);
        if(result.getCode() == BaseResponse.ok()){
            result.setData(jsonObject.getString("open_appid"));
        }
        return result;
    }

    /**
     * 绑定公众号或小程序到第三方平台
     * @param appId
     * @param openAppId
     * @param accessToken
     * @return
     * @throws Exception
     */
    private BaseResponse bindWeChatOpen(String appId, String openAppId, String accessToken) throws Exception{
        if(StringUtils.isEmpty(openAppId)){
            return new BaseResponse("开放平台账号未创建，不能绑定");
        }
        JSONObject request = new JSONObject();
        request.put("appid", appId);
        request.put("open_appid", openAppId);
        String response = HttpUtil.doPost("https://api.weixin.qq.com/cgi-bin/open/bind?access_token=" + accessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        BaseResponse result = BaseResponse.getInstance(jsonObject);
        result.setData(openAppId);
        return result;
    }

    /**
     * 解绑公众号或小程序到第三方平台
     * @param appId
     * @param openAppId
     * @param accessToken
     * @return
     * @throws Exception
     */
    private BaseResponse unbindWeChatOpen(String appId, String openAppId, String accessToken) throws Exception{
        if(StringUtils.isEmpty(openAppId)){
            return new BaseResponse("开放平台账号未创建，不能解绑");
        }
        JSONObject request = new JSONObject();
        request.put("appid", appId);
        request.put("open_appid", openAppId);
        String response = HttpUtil.doPost("https://api.weixin.qq.com/cgi-bin/open/unbind?access_token=" + accessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        BaseResponse result = BaseResponse.getInstance(jsonObject);
        return result;
    }

    /**
     * 查询公众号或小程序到第三方平台
     * @param appId
     * @param accessToken
     * @return
     * @throws Exception
     */
    private BaseResponse getWeChatOpen(String appId, String accessToken) throws Exception{
        JSONObject request = new JSONObject();
        request.put("appid", appId);
        String response = HttpUtil.doPost("https://api.weixin.qq.com/cgi-bin/open/get?access_token=" + accessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        BaseResponse result = BaseResponse.getInstance(jsonObject);
        if(result.getCode() == BaseResponse.ok()){
            result.setData(jsonObject.getString("open_appid"));
        }
        return result;
    }

    /**
     * 判断公众号是否设置"互联网|电子商务"的行业信息
     * @param accessToken
     * @return
     * @throws Exception
     */
    private Boolean getWeChatIndustry(String accessToken) throws Exception {
        String response = HttpUtil.doGet("https://api.weixin.qq.com/cgi-bin/template/get_industry?access_token=" + accessToken);
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject.get("primary_industry") != null) {
            JSONObject primary_industry = jsonObject.getJSONObject("primary_industry");
            if ("互联网|电子商务".equals(primary_industry.getString("second_class"))) {
                return true;
            }
        }
        if (jsonObject.get("secondary_industry") != null) {
            JSONObject primary_industry = jsonObject.getJSONObject("secondary_industry");
            if ("互联网|电子商务".equals(primary_industry.getString("second_class"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置公众号所属行业
     * @param accessToken
     * @throws Exception
     */
    public void setWeChatIndustry(String accessToken) throws Exception{
        JSONObject request = new JSONObject();
        request.put("industry_id1", "1");
        String response = HttpUtil.doPost("https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=" + accessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        BaseResponse result = BaseResponse.getInstance(jsonObject);
        if(result.getCode() != BaseResponse.ok()){
            throw new PredictException(String.format("设置公众号行业失败！%s %s", result.getCode(), result.getMessage()));
        }
    }

    /**
     * 查询公众号的模版消息
     * @param accessToken
     * @return
     * @throws Exception
     */
    public JSONArray getWeChatTemplate(String accessToken) throws Exception{
        String response = HttpUtil.doGet("https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=" + accessToken);
        JSONObject jsonObject = JSONObject.parseObject(response);
        return jsonObject.getJSONArray("template_list");
    }

    /**
     * 添加公众号模版消息
     * @param accessToken
     * @throws Exception
     */
    public String addWeChatTemplate(String accessToken, String templateShort) throws Exception{
        JSONObject request = new JSONObject();
        request.put("template_id_short", templateShort);
        String response = HttpUtil.doPost("https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=" + accessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        BaseResponse result = BaseResponse.getInstance(jsonObject);
        if(result.getCode() != BaseResponse.ok()){
            throw new PredictException(String.format("添加公众号模版消息失败！%s %s", result.getCode(), result.getMessage()));
        }
        return jsonObject.getString("template_id");
    }

    /**
     * 删除公众号模版消息
     * @param accessToken
     * @throws Exception
     */
    public void delWeChatTemplate(String accessToken, String templateId) throws Exception{
        JSONObject request = new JSONObject();
        request.put("template_id", templateId);
        String response = HttpUtil.doPost("https://api.weixin.qq.com/cgi-bin/template/del_private_template?access_token=" + accessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        BaseResponse result = BaseResponse.getInstance(jsonObject);
        if(result.getCode() != BaseResponse.ok()){
            throw new PredictException(String.format("添加公众号模版消息失败！%s %s", result.getCode(), result.getMessage()));
        }
    }
}
