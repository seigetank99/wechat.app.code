package cn.loftown.wechat.app.code.biz;

import cn.loftown.wechat.app.code.entity.CodeBaseModel;
import cn.loftown.wechat.app.code.util.DateTimeUtil;
import cn.loftown.wechat.app.code.util.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CodeBaseBll {
    @Autowired
    private RefreshTokenBll refreshTokenBll;

    /**
     * 获取草稿箱内的所有临时代码草稿
     * @return
     */
    public List<CodeBaseModel> getDraftsList() {
        try {
            String componentAccessToken = refreshTokenBll.getComponentAccessToken();
            String response = HttpUtil.doGet("https://api.weixin.qq.com/wxa/gettemplatedraftlist?access_token=" + componentAccessToken);
            JSONObject jsonObject = JSONObject.parseObject(response);
            List<CodeBaseModel> result = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("draft_list");
            for(int i = 0; i < jsonArray.size(); i++){
                JSONObject json =  jsonArray.getJSONObject(i);
                CodeBaseModel codeBaseModel = new CodeBaseModel();
                codeBaseModel.setId(json.getInteger("draft_id"));
                codeBaseModel.setUserVersion(json.getString("user_version"));
                codeBaseModel.setUserDesc(json.getString("user_desc"));
                codeBaseModel.setSourceMiniprogramAppid(json.getString("source_miniprogram_appid"));
                codeBaseModel.setSourceMiniprogram(json.getString("source_miniprogram"));
                codeBaseModel.setDeveloper(json.getString("developer"));
                codeBaseModel.setCreateTime(new Date(json.getLong("create_time") * 1000));
                codeBaseModel.setCreateTimeFormat(DateTimeUtil.formatToString(codeBaseModel.getCreateTime()));
                result.add(codeBaseModel);
            }
            return result;
        } catch (Exception ex) {

        }
        return null;
    }

    /**
     * 获取代码模版库中的所有小程序代码模版
     * @return
     */
    public List<CodeBaseModel> getTemplateList() {
        try {
            String componentAccessToken = refreshTokenBll.getComponentAccessToken();
            String response = HttpUtil.doGet("https://api.weixin.qq.com/wxa/gettemplatelist?access_token=" + componentAccessToken);
            JSONObject jsonObject = JSONObject.parseObject(response);
            List<CodeBaseModel> result = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("template_list");
            for(int i = 0; i < jsonArray.size(); i++){
                JSONObject json =  jsonArray.getJSONObject(i);
                CodeBaseModel codeBaseModel = new CodeBaseModel();
                codeBaseModel.setId(json.getInteger("template_id"));
                codeBaseModel.setUserVersion(json.getString("user_version"));
                codeBaseModel.setUserDesc(json.getString("user_desc"));
                codeBaseModel.setSourceMiniprogramAppid(json.getString("source_miniprogram_appid"));
                codeBaseModel.setSourceMiniprogram(json.getString("source_miniprogram"));
                codeBaseModel.setDeveloper(json.getString("developer"));
                codeBaseModel.setCreateTime(new Date(json.getLong("create_time") * 1000));
                codeBaseModel.setCreateTimeFormat(DateTimeUtil.formatToString(codeBaseModel.getCreateTime()));
                result.add(codeBaseModel);
            }
            return result;
        } catch (Exception ex) {

        }
        return null;

    }

    /**
     * 将草稿箱的草稿选为小程序代码模版
     * @param draftId 草稿编号
     */
    public String addtotemplate(int draftId){
        JSONObject request = new JSONObject();
        request.put("draft_id",draftId);
        try {
            String assesstoken = refreshTokenBll.getComponentAccessToken();
            String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/addtotemplate?access_token=" + assesstoken, request.toJSONString());
            JSONObject jsonObject = JSONObject.parseObject(response);
            if(jsonObject.getInteger("errcode") != 0){
                return jsonObject.getString("errmsg");
            }
        } catch (Exception ex){

        }
        return "";
    }

    /**
     * 删除指定小程序代码模版
     * @param templateId
     * @return
     */
    public String deletetemplate(int templateId){
        JSONObject request = new JSONObject();
        request.put("template_id", templateId);
        try {
            String assesstoken = refreshTokenBll.getComponentAccessToken();
            String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/deletetemplate?access_token=" + assesstoken, request.toJSONString());
            JSONObject jsonObject = JSONObject.parseObject(response);
            if(jsonObject.getInteger("errcode") != 0){
                return jsonObject.getString("errmsg");
            }
        } catch (Exception ex){

        }
        return "";
    }
}
