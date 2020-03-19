package cn.loftown.wechat.app.code.controller;

import cn.loftown.wechat.app.code.biz.AccountWxappBll;
import cn.loftown.wechat.app.code.biz.CodeBaseBll;
import cn.loftown.wechat.app.code.biz.RefreshTokenBll;
import cn.loftown.wechat.app.code.entity.AccountWxappModel;
import cn.loftown.wechat.app.code.entity.CodeBaseModel;
import cn.loftown.wechat.app.code.entity.SelectModel;
import cn.loftown.wechat.app.code.enums.AppTypeEnum;
import cn.loftown.wechat.app.code.enums.WxAppCodeStatusEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class CommonController {
    @Autowired
    CodeBaseBll codeBaseBll;
    @Autowired
    RefreshTokenBll refreshTokenBll;
    @Autowired
    AccountWxappBll accountWxappBll;


    @RequestMapping(value = "/getSelectValue", method = RequestMethod.POST)
    public List<SelectModel> getSelectValue(@RequestParam("type") Integer type, @RequestParam("acid") Integer acid) {
        List<SelectModel> selectModels = new ArrayList<>();
        try {
            switch (type) {
                case 1:
                    //查询代码库模版
                    getTemplates(selectModels);
                    break;
                case 2:
                    //获取小程序的第三方提交代码的页面配置
                    getWxAppPage(selectModels, acid);
                    break;
                case 3:
                    //获取授权小程序帐号已设置的类目
                    getWxAppCategory(selectModels, acid);
                    break;
                case 4:
                    //获取代码审核的状态列表
                    getWxAppCodeStatus(selectModels);
                    break;
                case 5:
                    //查询所有的小程序
                    getWxApps(selectModels);
                    break;
            }
        } catch (Exception ex){

        }
        return selectModels;
    }

    @RequestMapping(value = "/getAppLookImg", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody()
    public byte[] getAppLookImg(@RequestParam("acid") Integer acid) {
        try {
            String authorizerAccessToken = refreshTokenBll.getAuthorizerAccessToken(acid, AppTypeEnum.WECHATMINIAPP);
            if(!StringUtils.isEmpty(authorizerAccessToken)) {
                URL url = new URL("https://api.weixin.qq.com/wxa/get_qrcode?access_token=" + authorizerAccessToken);
                HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
                httpUrl.connect();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(httpUrl.getInputStream());
                byte[] bytes = new byte[bufferedInputStream.available()];
                bufferedInputStream.read(bytes, 0, bufferedInputStream.available());
                return bytes;
            }
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private void getWxApps(List<SelectModel> selectModels){
        List<AccountWxappModel> wxappModelList = accountWxappBll.getAccountWxAppByPage(1);
        if(wxappModelList != null && wxappModelList.size() > 0){
            for (AccountWxappModel wxappModel : wxappModelList){
                SelectModel selectModel = new SelectModel();
                selectModel.setId(wxappModel.getAcid().toString());
                selectModel.setValue(wxappModel.getName());
                selectModels.add(selectModel);
            }
        }
    }

    private void getWxAppCodeStatus(List<SelectModel> selectModels){
        Arrays.stream(WxAppCodeStatusEnum.values()).forEach(item ->{
            SelectModel selectModel = new SelectModel();
            selectModel.setId(item.getCode().toString());
            selectModel.setValue(item.getName());
            selectModels.add(selectModel);
        });
    }

    private void getTemplates(List<SelectModel> selectModels){
        List<CodeBaseModel> codeBaseModels = codeBaseBll.getTemplateList();
        if(codeBaseModels == null || codeBaseModels.size() == 0){
            return;
        }
        for (CodeBaseModel codeBaseModel : codeBaseModels){
            SelectModel selectModel = new SelectModel();
            selectModel.setId(codeBaseModel.getId().toString());
            selectModel.setValue(codeBaseModel.getUserVersion());
            selectModels.add(selectModel);
        }
    }

    private void getWxAppPage(List<SelectModel> selectModels, int acid) throws Exception{
        List<String> pages = accountWxappBll.getWxAppPage(acid);
        if(pages == null || pages.size() == 0){
            return;
        }
        for (String string : pages){
            SelectModel selectModel = new SelectModel();
            selectModel.setId(string);
            selectModel.setValue(string);
            selectModels.add(selectModel);
        }
    }

    private void getWxAppCategory(List<SelectModel> selectModels, int acid) throws Exception{
        JSONArray jsonArray = accountWxappBll.getWxAppCategory(acid);
        for(int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if(!StringUtils.isEmpty(jsonObject.getString("first_class"))){
                boolean hasData = false;
                for(SelectModel select : selectModels) {
                    if(select.getId().equals(jsonObject.getString("first_id"))){
                        hasData = true;
                        break;
                    }
                }
                if(hasData == false) {
                    SelectModel selectModel = new SelectModel();
                    selectModel.setId(jsonObject.getString("first_id"));
                    selectModel.setValue(jsonObject.getString("first_class"));
                    selectModel.setParentId("0");
                    selectModels.add(selectModel);
                }
            }
            if(!StringUtils.isEmpty(jsonObject.getString("second_class"))){
                boolean hasData = false;
                for(SelectModel select : selectModels) {
                    if(select.getParentId().equals(jsonObject.getString("first_id")) && select.getId().equals(jsonObject.getString("second_id"))){
                        hasData = true;
                        break;
                    }
                }
                if(hasData == false) {
                    SelectModel selectModel = new SelectModel();
                    selectModel.setId(jsonObject.getString("second_id"));
                    selectModel.setValue(jsonObject.getString("second_class"));
                    selectModel.setParentId(jsonObject.getString("first_id"));
                    selectModels.add(selectModel);
                }
            }
            if(!StringUtils.isEmpty(jsonObject.getString("third_class"))){
                boolean hasData = false;
                for(SelectModel select : selectModels) {
                    if(select.getParentId().equals(jsonObject.getString("second_id")) && select.getId().equals(jsonObject.getString("third_id"))){
                        hasData = true;
                        break;
                    }
                }
                if(hasData == false) {
                    SelectModel selectModel = new SelectModel();
                    selectModel.setId(jsonObject.getString("third_id"));
                    selectModel.setValue(jsonObject.getString("third_class"));
                    selectModel.setParentId(jsonObject.getString("second_id"));
                    selectModels.add(selectModel);
                }
            }
        }
    }
}
