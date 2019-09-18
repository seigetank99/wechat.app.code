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
        WxAppCodeSubmitDTO updateDTO = new WxAppCodeSubmitDTO();
        updateDTO.setAcid(1);
        updateDTO.setWxCommitCode("0");
        updateDTO.setWxCommitMsg("ok");
        wxappCodeSubmitDao.updateByPrimaryKey(updateDTO);

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
        if(wxAppCodeSubmitDTO == null || wxAppCodeSubmitDTO.getStatus() != WxAppCodeStatusEnum.TEMPLATE.getCode()){
            return new BaseResponse("操作失败，请刷新后重试");
        }
        AccountWxappDTO wxappDTO = accountWxappDao.getModelById(submitModel.getWxAppAcId());
        if(wxappDTO == null){
            return new BaseResponse("操作失败，请刷新后重试");
        }
        if(submitModel.getSubmitDataList() == null || submitModel.getSubmitDataList().size() == 0){
            return new BaseResponse("请检查填写数据");
        }
        JSONArray jsonArray = new JSONArray();
        for (SubmitCodeModel submitCodeModel : submitModel.getSubmitDataList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("address", submitCodeModel.getPage());
            jsonObject.put("tag", submitCodeModel.getTag());
            jsonObject.put("first_class", submitCodeModel.getFirstCategoryName());
            jsonObject.put("second_class", submitCodeModel.getSecondCategoryName());
            jsonObject.put("first_id",submitCodeModel.getFirstCategory());
            jsonObject.put("second_id", submitCodeModel.getSecondCategory());
            jsonObject.put("title", submitCodeModel.getTitle());
            if(!StringUtils.isEmpty(submitCodeModel.getThirdCategory())){
                jsonObject.put("third_class", submitCodeModel.getThirdCategoryName());
                jsonObject.put("third_id", submitCodeModel.getThirdCategory());
            }
            jsonArray.add(jsonObject);
        }
        JSONObject request = new JSONObject();
        if(!StringUtils.isEmpty(submitModel.getFeedbackInfo())) {
            request.put("feedback_info", submitModel.getFeedbackInfo());
            request.put("feedback_stuff", submitModel.getFeedbackStuff());
        }
        request.put("item_list", jsonArray);
        System.out.println(request.toJSONString());
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