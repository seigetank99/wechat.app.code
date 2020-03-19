package cn.loftown.wechat.app.code.biz;

import cn.loftown.wechat.app.code.base.BaseResponse;
import cn.loftown.wechat.app.code.dao.AccountWxappDao;
import cn.loftown.wechat.app.code.dao.ShopSyssetDao;
import cn.loftown.wechat.app.code.dao.lf.WxappBindTesterDao;
import cn.loftown.wechat.app.code.dao.lf.WxappCodeSubmitDao;
import cn.loftown.wechat.app.code.dto.AccountWxappDTO;
import cn.loftown.wechat.app.code.dto.lf.WxAppCodeSubmitDTO;
import cn.loftown.wechat.app.code.dto.lf.WxappBindTesterDTO;
import cn.loftown.wechat.app.code.entity.*;
import cn.loftown.wechat.app.code.enums.AppTypeEnum;
import cn.loftown.wechat.app.code.enums.StatusEnum;
import cn.loftown.wechat.app.code.enums.WxAppCodeStatusEnum;
import cn.loftown.wechat.app.code.model.CommitCodeRequest;
import cn.loftown.wechat.app.code.model.SubmitCodeRequest;
import cn.loftown.wechat.app.code.util.HttpUtil;
import cn.loftown.wechat.app.code.util.PHPTransformUtil;
import cn.loftown.wechat.app.code.util.WeChatResultUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AccountWxappBll {

    @Autowired
    AccountWxappDao accountWxappDao;
    @Autowired
    WxappCodeSubmitDao wxappCodeSubmitDao;
    @Autowired
    ShopSyssetDao shopSyssetDao;
    @Autowired
    WxappBindTesterDao wxappBindTesterDao;
    @Autowired
    RefreshTokenBll refreshTokenBll;
    @Autowired
    ComponentBll componentBll;

    /**
     * 分页查询公众号列表
     * @param pageIndex
     * @return
     */
    public List<AccountWxappModel> getAccountWxAppByPage(int pageIndex){
        List<AccountWxappModel> wechatModelList = new ArrayList<>();
        int pageStart = (pageIndex - 1) * 20;
        List<AccountWxappDTO> wechatDTOList = accountWxappDao.getModelByPage(StatusEnum.ENABLES.getCode(), pageStart);
        if(wechatDTOList != null && wechatDTOList.size() > 0){
            for (AccountWxappDTO wxappDTO : wechatDTOList){
                wechatModelList.add(getAccountWxAppDetail(wxappDTO));
            }
        }
        return wechatModelList;
    }

    /**
     * 设置小程序域名
     * @param model
     * @return
     * @throws Exception
     */
    public BaseResponse setWxAppDomain(CommitDomainModel model) throws Exception {
        CommitDomainModel commitDomainModel = getWxAppDomain(model.getAcid());
        if (commitDomainModel == null) {
            return new BaseResponse("操作失败，请刷新后重试");
        }
        //有更改再调用微信的更新接口
        boolean isDomainUpdate = false;
        if (!model.getRequestdomain().trim().equals(commitDomainModel.getRequestdomain().trim()) ||
                !model.getWsrequestdomain().trim().equals(commitDomainModel.getWsrequestdomain().trim()) ||
                !model.getUploaddomain().trim().equals(commitDomainModel.getUploaddomain().trim()) ||
                !model.getDownloaddomain().trim().equals(commitDomainModel.getDownloaddomain().trim())) {
            isDomainUpdate = true;
        }
        if(isDomainUpdate) {
            JSONObject request = new JSONObject();
            request.put("action", model.getRequestAction());
            request.put("requestdomain", Arrays.asList(model.getRequestdomain().split(";")));
            request.put("wsrequestdomain", Arrays.asList(model.getWsrequestdomain().split(";")));
            request.put("uploaddomain", Arrays.asList(model.getUploaddomain().split(";")));
            request.put("downloaddomain", Arrays.asList(model.getDownloaddomain().split(";")));
            String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(model.getAcid(), AppTypeEnum.WECHATMINIAPP);
            String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/modify_domain?access_token=" + authorizerAccessToken, request.toJSONString());
            JSONObject jsonObject = JSONObject.parseObject(response);
            BaseResponse result = BaseResponse.getInstance(jsonObject);
            if (result.getCode() != BaseResponse.ok()) {
                return result;
            }
        }
        //有更改再调用微信的更新接口
        boolean isUpdate = false;
        JSONObject webViewRequest = new JSONObject();
        webViewRequest.put("action", model.getWebAction());
        if (!model.getWebviewdomain().trim().equals(commitDomainModel.getWebviewdomain().trim())) {
            webViewRequest.put("webviewdomain", Arrays.asList(model.getWebviewdomain().split(";")));
            isUpdate = true;
        }
        if (isUpdate) {
            String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(model.getAcid(), AppTypeEnum.WECHATMINIAPP);
            String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/setwebviewdomain?access_token=" + authorizerAccessToken, webViewRequest.toJSONString());
            JSONObject jsonObject = JSONObject.parseObject(response);
            BaseResponse result = BaseResponse.getInstance(jsonObject);
            return result;
        }
        return new BaseResponse();
    }

    /**
     * 查询小程序域名
     * @param acid
     * @return
     * @throws Exception
     */
    public CommitDomainModel getWxAppDomain(int acid) throws Exception {
        AccountWxappDTO accountWxappDTO = accountWxappDao.getModelById(acid);
        if(accountWxappDTO == null){

        }
        CommitDomainModel result = new CommitDomainModel();
        result.setAcid(accountWxappDTO.getAcid());
        result.setWxAppName(accountWxappDTO.getName());

        JSONObject request = new JSONObject();
        request.put("action", "get");
        String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(accountWxappDTO.getAcid(), AppTypeEnum.WECHATMINIAPP);
        String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/modify_domain?access_token=" + authorizerAccessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        if(jsonObject.getInteger("errcode") == BaseResponse.ok()){
            String requestdomain = getWxAppResultUrl(jsonObject.getString("requestdomain"));
            if(StringUtils.isEmpty(requestdomain)){
                result.setRequestAction("add");
            } else {
                result.setRequestAction("set");
            }
            result.setRequestdomain(requestdomain);
            result.setWsrequestdomain(getWxAppResultUrl(jsonObject.getString("wsrequestdomain")));
            result.setUploaddomain(getWxAppResultUrl(jsonObject.getString("uploaddomain")));
            result.setDownloaddomain(getWxAppResultUrl(jsonObject.getString("downloaddomain")));

            CommitDomainModel serviceDomain = getWxAppWebDomain(accountWxappDTO);
            if(serviceDomain != null){
                result.setWebAction(serviceDomain.getWebAction());
                result.setWebviewdomain(serviceDomain.getWebviewdomain());
            }
        }
        return result;
    }

    /**
     * 查询小程序业务域名域名
     * @param wxappAcid
     * @return
     * @throws Exception
     */
    public CommitDomainModel getWxAppWebDomain(Integer wxappAcid) {
        try {
            AccountWxappDTO accountWxappDTO = accountWxappDao.getModelById(wxappAcid);
            if (accountWxappDTO == null) {

            }
            return getWxAppWebDomain(accountWxappDTO);
        } catch (Exception ex){
            return null;
        }
    }

    /**
     * 查询小程序业务域名域名
     * @param accountWxappDTO
     * @return
     * @throws Exception
     */
    public CommitDomainModel getWxAppWebDomain(AccountWxappDTO accountWxappDTO) throws Exception {
        CommitDomainModel result = new CommitDomainModel();
        result.setAcid(accountWxappDTO.getAcid());
        result.setWxAppName(accountWxappDTO.getName());

        JSONObject request = new JSONObject();
        request.put("action", "get");
        String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(accountWxappDTO.getAcid(), AppTypeEnum.WECHATMINIAPP);
        String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/setwebviewdomain?access_token=" + authorizerAccessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        if(jsonObject.getInteger("errcode") == BaseResponse.ok()){
            String webviewdomain = getWxAppResultUrl(jsonObject.getString("webviewdomain"));
            if(StringUtils.isEmpty(webviewdomain)){
                result.setWebAction("add");
            } else {
                result.setWebAction("set");
            }
            result.setWebviewdomain(webviewdomain);
        }
        return result;
    }

    private String getWxAppResultUrl(String url){
        String result = "";
        List<String> urlList = JSON.parseArray(url, String.class);
        if(urlList != null && urlList.size() > 0){
            for (String u : urlList){
                result += (u +";");
            }
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 查询正在发布的代码列表
     * @return
     */
    public List<WxappCodeSubmitModel> getWxAppCode(Integer wxAppId, Integer statusId){
        List<WxappCodeSubmitModel> codeSubmitModelList = new ArrayList<>();
        List<Integer> statusList = null;
        if(statusId != null) {
            statusList = Arrays.asList(statusId);
        } else {
            statusList = Arrays.asList(
                    WxAppCodeStatusEnum.TEMPLATE.getCode(),
                    WxAppCodeStatusEnum.SUBMIT.getCode(),
                    WxAppCodeStatusEnum.FEEDBACK.getCode(),
                    WxAppCodeStatusEnum.AUDIT.getCode());
        }
        List<WxAppCodeSubmitDTO> codeSubmitList = wxappCodeSubmitDao.selectByWxApp(wxAppId, statusList,null,null);
        for (WxAppCodeSubmitDTO submitDTO : codeSubmitList){
            WxappCodeSubmitModel submitModel = new WxappCodeSubmitModel();
            BeanUtils.copyProperties(submitDTO, submitModel);
            submitModel.setStatus(WxAppCodeStatusEnum.parse(submitDTO.getStatus()));
            codeSubmitModelList.add(submitModel);
        }
        return codeSubmitModelList;
    }

    /**
     * 根据acid查询公众号详情
     * @param acid
     * @return
     */
    public AccountWxappModel getAccountWxAppDetail(int acid){
        AccountWxappDTO wxappDTO = accountWxappDao.getModelById(acid);
        return getAccountWxAppDetail(wxappDTO);
    }

    /**
     * DTO转换成Model
     * @param wxappDTO
     * @return
     */
    public AccountWxappModel getAccountWxAppDetail(AccountWxappDTO wxappDTO){
        AccountWxappModel wxappModel = new AccountWxappModel();
        wxappModel.setAcid(wxappDTO.getAcid());
        wxappModel.setAccount(wxappDTO.getAccount());
        wxappModel.setAppId(wxappDTO.getKey());
        wxappModel.setLevel(wxappDTO.getLevel());
        wxappModel.setName(wxappDTO.getName());
        wxappModel.setOriginal(wxappDTO.getOriginal());
        if(StringUtils.isEmpty(wxappDTO.getAuth_refresh_token())){
            wxappModel.setHasAuth(false);
        } else {
            wxappModel.setHasAuth(true);
        }
        //如果扩展信息没值，去调用微信的接口查询下
        if(StringUtils.isEmpty(wxappDTO.getHead_img())){
            completionAccountWxAppDetail(wxappModel);
            //从微信那边查询到了扩展信息，落地到表里
            if(!StringUtils.isEmpty(wxappModel.getHeadImg())){
                updateAccountWxApp(wxappModel);
            }
        } else {
            wxappModel.setHeadImg(wxappDTO.getHead_img());
            wxappModel.setPrincipalName(wxappDTO.getPrincipal_name());
        }

        return wxappModel;
    }

    /**
     * 更新微信公众号的扩展信息
     * @param wxappModel
     */
    public void updateAccountWxApp(AccountWxappModel wxappModel){
        AccountWxappDTO wxappDTO = new AccountWxappDTO();
        wxappDTO.setAcid(wxappModel.getAcid());
        wxappDTO.setPrincipal_name(wxappModel.getPrincipalName());
        wxappDTO.setHead_img(wxappModel.getHeadImg());
        wxappDTO.setName(wxappModel.getName());
        accountWxappDao.updateInfo(wxappDTO);
    }

    /**
     * 刷新小程序基础信息
     * @param acid
     */
    public BaseResponse refreshInfo(int acid){
        AccountWxappDTO wxappDTO = accountWxappDao.getModelById(acid);
        if(wxappDTO == null){
            return new BaseResponse("操作失败，请刷新后重试");
        }
        AccountWxappModel wxappModel = getAccountWxAppDetail(wxappDTO);
        completionAccountWxAppDetail(wxappModel);
        updateAccountWxApp(wxappModel);
        return new BaseResponse();
    }

    /**
     * 去微信查询小程序的扩展信息
     * @param accountWxappModel
     */
    private void completionAccountWxAppDetail(AccountWxappModel accountWxappModel){
        //没有APPID的和未授权的公众号不查询扩展信息
        if(StringUtils.isEmpty(accountWxappModel.getAppId()) || accountWxappModel.getHasAuth() == false){
            return;
        }
        try {
            String componentAccessToken = refreshTokenBll.getComponentAccessToken();
            ComponentModel componentModel = componentBll.getComponentInfo();
            JSONObject request = new JSONObject();
            request.put("component_appid", componentModel.getAppId());
            request.put("authorizer_appid", accountWxappModel.getAppId());
            String response = HttpUtil.doPost("https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token="+ componentAccessToken, request.toJSONString());
            JSONObject responseJson = JSONObject.parseObject(response);
            accountWxappModel.setHeadImg(responseJson.getJSONObject("authorizer_info").getString("head_img"));
            accountWxappModel.setPrincipalName(responseJson.getJSONObject("authorizer_info").getString("principal_name"));
            accountWxappModel.setName(responseJson.getJSONObject("authorizer_info").getString("nick_name"));
        } catch (Exception ex){

        }
    }

    /**
     * 提交小程序代码到微信审核
     * @param submitModel
     * @return
     * @throws Exception
     */
    public BaseResponse submitCode(SubmitCodeRequest submitModel) throws Exception {
        WxAppCodeSubmitDTO wxAppCodeSubmitDTO = wxappCodeSubmitDao.selectByPrimaryKey(submitModel.getAcid());
        if(wxAppCodeSubmitDTO == null || (wxAppCodeSubmitDTO.getStatus() != WxAppCodeStatusEnum.TEMPLATE.getCode() &&
                wxAppCodeSubmitDTO.getStatus() != WxAppCodeStatusEnum.FEEDBACK.getCode())){
            return new BaseResponse("操作失败，请刷新后重试");
        }
        AccountWxappDTO wxappDTO = accountWxappDao.getModelById(submitModel.getWxAppAcId());
        if(wxappDTO == null){
            return new BaseResponse("操作失败，请刷新后重试");
        }
        if(submitModel.getSubmitDataList() == null || submitModel.getSubmitDataList().size() == 0){
            //重新提交不需要这部分信息，直接取库
            if(StringUtils.isEmpty(submitModel.getFeedbackInfo()) && submitModel.getJsonArray() == null){
                return new BaseResponse("请检查填写数据");
            }
        }
        JSONObject request = new JSONObject();
        JSONArray jsonArray = null;
        //如果不是被驳回了重新提交，就要组织参数
        if(StringUtils.isEmpty(submitModel.getFeedbackInfo())) {
            //如果页面参数传递过来了，就不循环组织参数了
            if(submitModel.getJsonArray() != null){
                jsonArray = submitModel.getJsonArray();
            } else {
                jsonArray = new JSONArray();
                for (SubmitCodeModel submitCodeModel : submitModel.getSubmitDataList()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("address", submitCodeModel.getPage());
                    jsonObject.put("tag", submitCodeModel.getTag());
                    jsonObject.put("first_class", submitCodeModel.getFirstCategoryName());
                    jsonObject.put("second_class", submitCodeModel.getSecondCategoryName());
                    jsonObject.put("first_id", submitCodeModel.getFirstCategory());
                    jsonObject.put("second_id", submitCodeModel.getSecondCategory());
                    jsonObject.put("title", submitCodeModel.getTitle());
                    if (!StringUtils.isEmpty(submitCodeModel.getThirdCategory())) {
                        jsonObject.put("third_class", submitCodeModel.getThirdCategoryName());
                        jsonObject.put("third_id", submitCodeModel.getThirdCategory());
                    }
                    jsonArray.add(jsonObject);
                }
            }
            request.put("item_list", jsonArray);
        } else {
            jsonArray = JSONArray.parseArray(wxAppCodeSubmitDTO.getItemList());
            request.put("item_list", jsonArray);
            request.put("feedback_info", submitModel.getFeedbackInfo());
            request.put("feedback_stuff", submitModel.getFeedbackStuff());
        }

        String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(submitModel.getWxAppAcId(), AppTypeEnum.WECHATMINIAPP);
        String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/submit_audit?access_token=" + authorizerAccessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        BaseResponse result = BaseResponse.getInstance(jsonObject);
        result.setMessage(String.format("小程序%s%s", wxAppCodeSubmitDTO.getWxAppName(), WeChatResultUtil.TransformCodeToMsg(result.getCode())));
        WxAppCodeSubmitDTO updateDTO = new WxAppCodeSubmitDTO();
        updateDTO.setAcid(wxAppCodeSubmitDTO.getAcid());
        updateDTO.setWxSubmitCode(result.getCode().toString());
        updateDTO.setWxSubmitMsg(result.getMessage());
        updateDTO.setItemList(jsonArray.toJSONString());
        updateDTO.setSubmitUser(1);
        updateDTO.setSubmitTime(new Timestamp(System.currentTimeMillis()));
        if(result.getCode() == BaseResponse.ok()){
            updateDTO.setStatus(WxAppCodeStatusEnum.SUBMIT.getCode());
            updateDTO.setAuditId(jsonObject.getInteger("auditid"));
        }
        wxappCodeSubmitDao.updateByPrimaryKey(updateDTO);
        return result;
    }

    /**
     * 修改小程序底部菜单后重新审核
     * @param uniacid
     * @return
     * @throws Exception
     */
    public BaseResponse setWxAppTabBar(Integer uniacid) throws Exception{
        AccountWxappDTO wxappDTO = accountWxappDao.getModelByUniAcid(uniacid);
        if(wxappDTO == null){
            return new BaseResponse("小程序不存在");
        }

        List<Integer> statusList = new ArrayList<>();
        statusList.add(WxAppCodeStatusEnum.TEMPLATE.getCode());
        statusList.add(WxAppCodeStatusEnum.SUBMIT.getCode());
        statusList.add(WxAppCodeStatusEnum.FEEDBACK.getCode());
        int wxAppCodeCount = wxappCodeSubmitDao.selectByWxAppCount(wxappDTO.getAcid(), statusList, null, null);
        if(wxAppCodeCount > 0){
            return new BaseResponse("小程序正在审核中，请等待审核通过后再提交");
        }

        WxAppCodeSubmitDTO appCodeSubmitDTO = wxappCodeSubmitDao.selectByItemList(wxappDTO.getAcid());
        if(appCodeSubmitDTO == null){
            return new BaseResponse("小程序还没有发布成功的版本，请发布成功后再提交");
        }

        String sets = shopSyssetDao.getSets(uniacid);
        JSONObject tabBar = PHPTransformUtil.getAppTabbarConfig(sets);

        CommitCodeRequest commitCodeRequest = new CommitCodeRequest();
        commitCodeRequest.setWxAppAcid(appCodeSubmitDTO.getWxAppAcid());
        commitCodeRequest.setWxAppName(appCodeSubmitDTO.getWxAppName());
        commitCodeRequest.setTemplateId(appCodeSubmitDTO.getTemplateId());
        commitCodeRequest.setUserDesc(appCodeSubmitDTO.getUserDesc());
        commitCodeRequest.setUserVersion(appCodeSubmitDTO.getUserVersion());

        JSONObject extJson = new JSONObject();
        extJson.put("extEnable", true);
        extJson.put("extAppid", wxappDTO.getKey());
        extJson.put("tabBar", tabBar);

        commitCodeRequest.setExtJson(extJson);

        BaseResponse<Integer> response = commitCode(commitCodeRequest);
        if(response.getCode() == BaseResponse.ok()){
            SubmitCodeRequest submitModel = new SubmitCodeRequest();
            submitModel.setAcid(response.getData());
            submitModel.setWxAppAcId(appCodeSubmitDTO.getWxAppAcid());
            submitModel.setWxAppName(appCodeSubmitDTO.getWxAppName());
            submitModel.setJsonArray(JSONArray.parseArray(appCodeSubmitDTO.getItemList()));

            response = submitCode(submitModel);
            if(response.getCode() != BaseResponse.ok()){
                response.setMessage("代码提交成功，但是提交审核失败，请联系管理员重新提交审核");
            }
        }
        return response;
    }

    /**
     * 为授权的小程序帐号上传小程序代码
     * @param commitModel
     * @return
     */
    public BaseResponse<Integer> commitCode(CommitCodeRequest commitModel){
        AccountWxappDTO wxappDTO = accountWxappDao.getModelById(commitModel.getWxAppAcid());
        if(wxappDTO == null){
            return new BaseResponse("小程序不存在");
        }
        CommitDomainModel commitDomainModel = getWxAppWebDomain(commitModel.getWxAppAcid());
        if(commitDomainModel == null || StringUtils.isEmpty(commitDomainModel.getWebviewdomain())){
            return new BaseResponse("请先设置小程序业务域名");
        }
        List<Integer> statusList = new ArrayList<>();
        statusList.add(WxAppCodeStatusEnum.TEMPLATE.getCode());
        statusList.add(WxAppCodeStatusEnum.SUBMIT.getCode());
        statusList.add(WxAppCodeStatusEnum.FEEDBACK.getCode());
        statusList.add(WxAppCodeStatusEnum.AUDIT.getCode());
        int wxAppCodeCount = wxappCodeSubmitDao.selectByWxAppCount(commitModel.getWxAppAcid(), statusList, null, null);
        if(wxAppCodeCount > 0){
            return new BaseResponse("存在未完成的代码，请先禁用");
        }

        //先保存DB
        int acId = saveCommitCode(commitModel);
        //再调用微信接口
        JSONObject request = new JSONObject();
        request.put("template_id", commitModel.getTemplateId());
        request.put("user_version", commitModel.getUserVersion());
        request.put("user_desc", commitModel.getUserDesc());
        JSONObject ext_json = null;
        if(commitModel.getExtJson() != null){
            ext_json = commitModel.getExtJson();
        } else {
            ext_json = new JSONObject();
            ext_json.put("extAppid", wxappDTO.getKey());
        }
        request.put("ext_json", ext_json.toJSONString());
        try {
            String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(commitModel.getWxAppAcid(), AppTypeEnum.WECHATMINIAPP);
            String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/commit?access_token=" + authorizerAccessToken, request.toJSONString());
            JSONObject jsonObject = JSONObject.parseObject(response);
            BaseResponse result = BaseResponse.getInstance(jsonObject);
            result.setMessage(String.format("小程序%s%s", wxappDTO.getName(), WeChatResultUtil.TransformCodeToMsg(result.getCode())));
            WxAppCodeSubmitDTO updateDTO = new WxAppCodeSubmitDTO();
            updateDTO.setAcid(acId);
            updateDTO.setWxCommitCode(result.getCode().toString());
            updateDTO.setWxCommitMsg(result.getMessage());
            updateDTO.setSubmitUser(1);
            updateDTO.setSubmitTime(new Timestamp(System.currentTimeMillis()));
            wxappCodeSubmitDao.updateByPrimaryKey(updateDTO);
            //把数据库ID返回出去
            result.setData(acId);
            return result;
        } catch (Exception ex){
            return new BaseResponse(ex.getMessage());
        }
    }

    /**
     * 查询小程序审核结果
     * @param acid
     * @return
     * @throws Exception
     */
    public BaseResponse refresAuditStatus(Integer acid) throws Exception{
        WxAppCodeSubmitDTO wxAppCodeSubmitDTO = wxappCodeSubmitDao.selectByPrimaryKey(acid);
        if(wxAppCodeSubmitDTO == null || wxAppCodeSubmitDTO.getStatus() != WxAppCodeStatusEnum.SUBMIT.getCode()){
            return new BaseResponse("操作失败，请刷新后重试");
        }
        String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(wxAppCodeSubmitDTO.getWxAppAcid(), AppTypeEnum.WECHATMINIAPP);
        JSONObject request = new JSONObject();
        request.put("auditid", wxAppCodeSubmitDTO.getAuditId());
        String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/get_auditstatus?access_token="+authorizerAccessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        BaseResponse result = BaseResponse.getInstance(jsonObject);
        result.setMessage(String.format("小程序%s%s", wxAppCodeSubmitDTO.getWxAppName(), WeChatResultUtil.TransformCodeToMsg(result.getCode())));
        if(result.getCode().equals(BaseResponse.ok())){
            WxAppCodeSubmitDTO updateDTO = new WxAppCodeSubmitDTO();
            updateDTO.setAcid(acid);
            boolean isUpdateDB = false;
            switch (jsonObject.getInteger("status")){
                case 0:
                    updateDTO.setStatus(WxAppCodeStatusEnum.AUDIT.getCode());
                    isUpdateDB = true;
                    break;
                case 1:
                    updateDTO.setStatus(WxAppCodeStatusEnum.FEEDBACK.getCode());
                    updateDTO.setWxSubmitCode(jsonObject.getString("reason"));
                    updateDTO.setWxSubmitMsg(jsonObject.getString("screenshot"));
                    isUpdateDB = true;
                    break;
                case 2:
                    break;
                case 3:
                    updateDTO.setStatus(WxAppCodeStatusEnum.DELETE.getCode());
                    isUpdateDB = true;
                    break;
            }
            if(isUpdateDB) {
                wxappCodeSubmitDao.updateByPrimaryKey(updateDTO);
            }
        }
        return result;
    }

    /**
     * 发布审核通过的小程序
     * @param acid
     * @return
     */
    public BaseResponse releaseCode(Integer acid) throws Exception {
        WxAppCodeSubmitDTO wxAppCodeSubmitDTO = wxappCodeSubmitDao.selectByPrimaryKey(acid);
        if(wxAppCodeSubmitDTO == null || wxAppCodeSubmitDTO.getStatus() != WxAppCodeStatusEnum.AUDIT.getCode()){
            return new BaseResponse("操作失败，请刷新后重试");
        }
        String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(wxAppCodeSubmitDTO.getWxAppAcid(), AppTypeEnum.WECHATMINIAPP);
        JSONObject request = new JSONObject();
        String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/release?access_token="+authorizerAccessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        BaseResponse result = BaseResponse.getInstance(jsonObject);
        result.setMessage(String.format("小程序%s%s", wxAppCodeSubmitDTO.getWxAppName(), WeChatResultUtil.TransformCodeToMsg(result.getCode())));
        if(result.getCode().equals(BaseResponse.ok())){
            WxAppCodeSubmitDTO updateDTO = new WxAppCodeSubmitDTO();
            updateDTO.setAcid(acid);
            updateDTO.setStatus(WxAppCodeStatusEnum.RELEASE.getCode());
            updateDTO.setReleaseUser(1);
            updateDTO.setReleaseTime(new Timestamp(System.currentTimeMillis()));
            wxappCodeSubmitDao.updateByPrimaryKey(updateDTO);
        }
        return result;
    }

    /**
     * 小程序版本回退
     * @param acid
     * @return
     * @throws Exception
     */
    public BaseResponse revertCode(Integer acid) throws Exception {
        WxAppCodeSubmitDTO wxAppCodeSubmitDTO = wxappCodeSubmitDao.selectByPrimaryKey(acid);
        if(wxAppCodeSubmitDTO == null || wxAppCodeSubmitDTO.getStatus() != WxAppCodeStatusEnum.AUDIT.getCode()){
            return new BaseResponse("操作失败，请刷新后重试");
        }
        String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(wxAppCodeSubmitDTO.getWxAppAcid(), AppTypeEnum.WECHATMINIAPP);
        String response = HttpUtil.doGet("https://api.weixin.qq.com/wxa/revertcoderelease?access_token="+authorizerAccessToken);
        JSONObject jsonObject = JSONObject.parseObject(response);
        BaseResponse result = BaseResponse.getInstance(jsonObject);
        result.setMessage(String.format("小程序%s%s", wxAppCodeSubmitDTO.getWxAppName(), WeChatResultUtil.TransformCodeToMsg(result.getCode())));
        if(result.getCode().equals(BaseResponse.ok())){
            WxAppCodeSubmitDTO updateDTO = new WxAppCodeSubmitDTO();
            updateDTO.setAcid(acid);
            updateDTO.setStatus(WxAppCodeStatusEnum.REVERT.getCode());
            updateDTO.setRevertUser(1);
            updateDTO.setRevertTime(new Timestamp(System.currentTimeMillis()));
            wxappCodeSubmitDao.updateByPrimaryKey(updateDTO);
        }
        return result;
    }

    /**
     * 作废提交的版本
     * @param acid
     * @return
     * @throws Exception
     */
    public BaseResponse cancelCode(int acid) throws Exception{
        WxAppCodeSubmitDTO wxAppCodeSubmitDTO = wxappCodeSubmitDao.selectByPrimaryKey(acid);
        if(wxAppCodeSubmitDTO == null ){
            return new BaseResponse("操作失败，请刷新后重试");
        }
        if( wxAppCodeSubmitDTO.getStatus() == WxAppCodeStatusEnum.RELEASE.getCode()){
            return new BaseResponse("生产中的版本不能作废");
        }
        WxAppCodeSubmitDTO updateDTO = new WxAppCodeSubmitDTO();
        updateDTO.setAcid(wxAppCodeSubmitDTO.getAcid());
        updateDTO.setStatus(WxAppCodeStatusEnum.DELETE.getCode());
        wxappCodeSubmitDao.updateByPrimaryKey(updateDTO);
        return new BaseResponse();
    }

    /**
     * 修改小程序线上代码的可见状态
     * @param acid
     * @return
     * @throws Exception
     */
    public BaseResponse changeVisitStatus(Integer acid) throws Exception {
        WxAppCodeSubmitDTO wxAppCodeSubmitDTO = wxappCodeSubmitDao.selectByPrimaryKey(acid);
        if(wxAppCodeSubmitDTO == null || wxAppCodeSubmitDTO.getStatus() != WxAppCodeStatusEnum.RELEASE.getCode() || wxAppCodeSubmitDTO.getStatus() != WxAppCodeStatusEnum.INVISIBLE.getCode()){
            return new BaseResponse("操作失败，请刷新后重试");
        }
        boolean isVisible = false;
        if(wxAppCodeSubmitDTO.getStatus() == WxAppCodeStatusEnum.INVISIBLE.getCode()){
            isVisible = true;
        }
        String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(wxAppCodeSubmitDTO.getWxAppAcid(), AppTypeEnum.WECHATMINIAPP);
        JSONObject request = new JSONObject();
        request.put("action", isVisible ? "open" : "close");
        String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/change_visitstatus?access_token="+authorizerAccessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        BaseResponse result = BaseResponse.getInstance(jsonObject);
        result.setMessage(String.format("小程序%s%s", wxAppCodeSubmitDTO.getWxAppName(), WeChatResultUtil.TransformCodeToMsg(result.getCode())));
        if(result.getCode().equals(BaseResponse.ok())){
            WxAppCodeSubmitDTO updateDTO = new WxAppCodeSubmitDTO();
            updateDTO.setAcid(acid);
            updateDTO.setStatus(isVisible ? WxAppCodeStatusEnum.RELEASE.getCode() : WxAppCodeStatusEnum.INVISIBLE.getCode());
            updateDTO.setRevertUser(1);
            updateDTO.setRevertTime(new Timestamp(System.currentTimeMillis()));
            wxappCodeSubmitDao.updateByPrimaryKey(updateDTO);
        }
        return result;
    }

    /**
     * 获取小程序的第三方提交代码的页面配置
     * @param acid
     * @return
     * @throws Exception
     */
    public List<String> getWxAppPage(int acid) throws Exception{
        String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(acid, AppTypeEnum.WECHATMINIAPP);
        String response = HttpUtil.doGet("https://api.weixin.qq.com/wxa/get_page?access_token=" + authorizerAccessToken);
        JSONObject jsonObject = JSONObject.parseObject(response);
        List<String> result = JSON.parseArray(jsonObject.getString("page_list"), String.class);
        return result;
    }

    /**
     * 获取授权小程序帐号已设置的类目
     * @param acid
     * @return
     * @throws Exception
     */
    public JSONArray getWxAppCategory(int acid) throws Exception{
        String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(acid, AppTypeEnum.WECHATMINIAPP);
        String response = HttpUtil.doGet("https://api.weixin.qq.com/wxa/get_category?access_token=" + authorizerAccessToken);
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("category_list");
        return jsonArray;
    }

    /**
     * 查询小程序体验者列表
     * @param wxAppId
     * @return
     */
    public List<WxappBindTesterModel> getBindTestUser(int wxAppId) throws Exception {
        List<WxappBindTesterModel> result = new ArrayList<>();
        AccountWxappDTO wxappDTO = accountWxappDao.getModelById(wxAppId);
        if (wxappDTO == null) {
            return result;
        }

        List<String> dbTesterList = new ArrayList<>();
        List<WxappBindTesterDTO> dtoList = wxappBindTesterDao.selectByWxAppId(wxAppId, StatusEnum.ENABLES.getCode());
        if (dtoList != null && dtoList.size() > 0) {
            for (WxappBindTesterDTO dto : dtoList) {
                WxappBindTesterModel model = new WxappBindTesterModel();
                BeanUtils.copyProperties(dto, model);
                result.add(model);
                dbTesterList.add(dto.getWxUserStr());
            }
        }
        JSONObject request = new JSONObject();
        request.put("action", "get_experiencer");
        String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(wxAppId, AppTypeEnum.WECHATMINIAPP);
        String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/memberauth?access_token=" + authorizerAccessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject.getInteger("errcode") == BaseResponse.ok()) {
            JSONArray members = jsonObject.getJSONArray("members");
            List<String> testerList = new ArrayList<>();
            for(int i = 0; i < members.size(); i++){
                testerList.add(members.getJSONObject(i).getString("userstr"));
            }

            for (String tester : testerList) {
                if (dbTesterList.contains(tester)) {
                    continue;
                }
                WxappBindTesterDTO insertDto = new WxappBindTesterDTO();
                insertDto.setWxNumber("未知");
                insertDto.setWxUserStr(tester);
                insertDto.setWxAppAcid(wxAppId);
                insertDto.setUniacid(wxappDTO.getUniacid());
                insertDto.setStatus(StatusEnum.ENABLES.getCode());
                insertDto.setCreateDate(new Timestamp(System.currentTimeMillis()));
                wxappBindTesterDao.insert(insertDto);
                WxappBindTesterModel model = new WxappBindTesterModel();
                BeanUtils.copyProperties(insertDto, model);
                result.add(model);
            }
        }
        return result;
    }

    /**
     * 给小程序绑定体验者
     * @param model
     * @return
     * @throws Exception
     */
    public BaseResponse setBindTestUser(WxappBindTesterModel model) throws Exception {
        AccountWxappDTO accountWxappDTO = accountWxappDao.getModelById(model.getWxAppAcid());
        if(accountWxappDTO == null){
            return new BaseResponse("操作失败，请刷新后重试");
        }
        JSONObject request = new JSONObject();
        request.put("wechatid", model.getWxNumber().trim());
        String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(accountWxappDTO.getAcid(), AppTypeEnum.WECHATMINIAPP);
        String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/bind_tester?access_token=" + authorizerAccessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        if(jsonObject.getInteger("errcode") == BaseResponse.ok()) {
            WxappBindTesterDTO wxappBindTesterDTO = new WxappBindTesterDTO();
            wxappBindTesterDTO.setWxAppAcid(accountWxappDTO.getAcid());
            wxappBindTesterDTO.setUniacid(accountWxappDTO.getUniacid());
            wxappBindTesterDTO.setWxNumber(model.getWxNumber());
            wxappBindTesterDTO.setWxUserStr(jsonObject.getString("userstr"));
            wxappBindTesterDTO.setStatus(StatusEnum.ENABLES.getCode());
            wxappBindTesterDTO.setCreateDate(new Timestamp(System.currentTimeMillis()));
            wxappBindTesterDao.insert(wxappBindTesterDTO);
        }
        BaseResponse result = BaseResponse.getInstance(jsonObject);
        return result;
    }

    /**
     * 删除小程序绑定的体验者
     * @param model
     * @return
     * @throws Exception
     */
    public BaseResponse delBindTestUser(WxappBindTesterModel model) throws Exception {
        WxappBindTesterDTO wxappBindTesterDTO = wxappBindTesterDao.selectByPrimaryKey(model.getAcid());
        if(wxappBindTesterDTO == null){
            return new BaseResponse("操作失败，请刷新后重试");
        }
        if(wxappBindTesterDTO.getStatus() != StatusEnum.ENABLES.getCode()){
            return new BaseResponse();
        }
        AccountWxappDTO accountWxappDTO = accountWxappDao.getModelById(model.getWxAppAcid());
        if(accountWxappDTO == null){
            return new BaseResponse("操作失败，请刷新后重试");
        }

        JSONObject request = new JSONObject();
        request.put("userstr", wxappBindTesterDTO.getWxUserStr().trim());
        String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(accountWxappDTO.getAcid(), AppTypeEnum.WECHATMINIAPP);
        String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/unbind_tester?access_token=" + authorizerAccessToken, request.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        if(jsonObject.getInteger("errcode") == BaseResponse.ok()) {
            wxappBindTesterDao.updateStatus(wxappBindTesterDTO.getAcid(), StatusEnum.DISABLES.getCode());
        }
        BaseResponse result = BaseResponse.getInstance(jsonObject);
        return result;
    }

    /**
     * 转换
     * @param commitModel
     * @return
     */
    private Integer saveCommitCode(CommitCodeRequest commitModel) {
        WxAppCodeSubmitDTO dto = new WxAppCodeSubmitDTO();
        dto.setWxAppAcid(commitModel.getWxAppAcid());
        dto.setWxAppName(commitModel.getWxAppName());
        dto.setTemplateId(commitModel.getTemplateId());
        dto.setUserVersion(commitModel.getUserVersion());
        dto.setUserDesc(commitModel.getUserDesc());
        dto.setStatus(WxAppCodeStatusEnum.TEMPLATE.getCode());
        dto.setCreateUser(1);
        dto.setCreateTime(new Timestamp(System.currentTimeMillis()));
        wxappCodeSubmitDao.insert(dto);
        return dto.getAcid();
    }
}
