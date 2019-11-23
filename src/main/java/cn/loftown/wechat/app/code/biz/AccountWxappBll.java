package cn.loftown.wechat.app.code.biz;

import cn.loftown.wechat.app.code.base.BaseResponse;
import cn.loftown.wechat.app.code.dao.AccountWxappDao;
import cn.loftown.wechat.app.code.dao.lf.WxappCodeSubmitDao;
import cn.loftown.wechat.app.code.dto.AccountWxappDTO;
import cn.loftown.wechat.app.code.dto.lf.WxAppCodeSubmitDTO;
import cn.loftown.wechat.app.code.entity.AccountWxappModel;
import cn.loftown.wechat.app.code.entity.ComponentModel;
import cn.loftown.wechat.app.code.entity.SubmitCodeModel;
import cn.loftown.wechat.app.code.entity.WxappCodeSubmitModel;
import cn.loftown.wechat.app.code.enums.AppTypeEnum;
import cn.loftown.wechat.app.code.enums.WxAppCodeStatusEnum;
import cn.loftown.wechat.app.code.model.CommitCodeRequest;
import cn.loftown.wechat.app.code.entity.CommitDomainModel;
import cn.loftown.wechat.app.code.model.SubmitCodeRequest;
import cn.loftown.wechat.app.code.util.HttpUtil;
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
import java.util.List;

@Component
public class AccountWxappBll {

    @Autowired
    AccountWxappDao accountWxappDao;
    @Autowired
    WxappCodeSubmitDao wxappCodeSubmitDao;
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
//        WxAppCodeSubmitDTO updateDTO = new WxAppCodeSubmitDTO();
//        updateDTO.setAcid(1);
//        updateDTO.setWxCommitCode("0");
//        updateDTO.setWxCommitMsg("ok");
//        wxappCodeSubmitDao.updateByPrimaryKey(updateDTO);

        List<AccountWxappModel> wechatModelList = new ArrayList<>();
        int pageStart = (pageIndex - 1) * 20;
        List<AccountWxappDTO> wechatDTOList = accountWxappDao.getModelByPage(pageStart);
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
        if(commitDomainModel == null){
            return new BaseResponse("操作失败，请刷新后重试");
        }
        JSONObject request = new JSONObject();
        request.put("action", model.getRequestAction());
        boolean isUpdate = false;
        if(!model.getRequestdomain().trim().equals(commitDomainModel.getRequestdomain().trim())) {
            request.put("requestdomain", model.getRequestdomain().split(";"));
            isUpdate = true;
        }
        if(!model.getWsrequestdomain().trim().equals(commitDomainModel.getWsrequestdomain().trim())) {
            request.put("wsrequestdomain", model.getWsrequestdomain().split(";"));
            isUpdate = true;
        }
        if(!model.getUploaddomain().trim().equals(commitDomainModel.getUploaddomain().trim())) {
            request.put("uploaddomain", model.getUploaddomain().split(";"));
            isUpdate = true;
        }
        if(!model.getDownloaddomain().trim().equals(commitDomainModel.getDownloaddomain().trim())) {
            request.put("downloaddomain", model.getDownloaddomain().split(";"));
            isUpdate = true;
        }
        String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(model.getAcid(), AppTypeEnum.WECHATMINIAPP);
        if(isUpdate) {
            String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/modify_domain?access_token=" + authorizerAccessToken, request.toJSONString());
            JSONObject jsonObject = JSONObject.parseObject(response);
            BaseResponse result = BaseResponse.getInstance(jsonObject);
            if (result.getCode() != BaseResponse.ok()) {
                return result;
            }
        }
        isUpdate = false;
        request = new JSONObject();
        request.put("action", model.getWebAction());
        if(!model.getWebviewdomain().trim().equals(commitDomainModel.getWebviewdomain().trim())) {
            request.put("webviewdomain", model.getWebviewdomain().split(";"));
            isUpdate = true;
        }
        if(isUpdate) {
            String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/setwebviewdomain?access_token=" + authorizerAccessToken, request.toJSONString());
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
    public List<WxappCodeSubmitModel> getWxAppCode(){
        List<WxappCodeSubmitModel> codeSubmitModelList = new ArrayList<>();
        List<WxAppCodeSubmitDTO> codeSubmitList = wxappCodeSubmitDao.selectByWxApp(null,null,null,null);
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
        accountWxappDao.updateInfo(wxappDTO);
    }

    /**
     * 去微信查询公众号的扩展信息
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
            if(StringUtils.isEmpty(submitModel.getFeedbackInfo())){
                return new BaseResponse("请检查填写数据");
            }
        }
        JSONObject request = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if(StringUtils.isEmpty(submitModel.getFeedbackInfo())) {
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
            request.put("item_list", jsonArray);
        } else {
            request.put("item_list", JSONArray.parseArray(wxAppCodeSubmitDTO.getItemList()));
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
     * 为授权的小程序帐号上传小程序代码
     * @param commitModel
     * @return
     */
    public BaseResponse commitCode(CommitCodeRequest commitModel){
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
        JSONObject ext_json = new JSONObject();
        ext_json.put("extAppid", wxappDTO.getKey());
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
        wxappCodeSubmitDao.insert(dto);
        return dto.getAcid();
    }
}
