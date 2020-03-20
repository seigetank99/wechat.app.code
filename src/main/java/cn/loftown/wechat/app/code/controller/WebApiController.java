package cn.loftown.wechat.app.code.controller;

import cn.loftown.wechat.app.code.base.BaseResponse;
import cn.loftown.wechat.app.code.biz.AccountWechatBll;
import cn.loftown.wechat.app.code.biz.AccountWxappBll;
import cn.loftown.wechat.app.code.biz.RefreshTokenBll;
import cn.loftown.wechat.app.code.enums.AppTypeEnum;
import cn.loftown.wechat.app.code.model.BaseRequest;
import cn.loftown.wechat.app.code.model.BindOpenToWeChatRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webApi")
public class WebApiController {
    @Autowired
    RefreshTokenBll refreshTokenBll;
    @Autowired
    AccountWxappBll accountWxappBll;
    @Autowired
    AccountWechatBll accountWechatBll;

    @RequestMapping(value = "/getAccessToken")
    public BaseResponse getAccessToken(@RequestParam("acid") Integer acid, @RequestParam("appType") Integer appType) {
        try {
            AppTypeEnum appTypeEnum = AppTypeEnum.parse(appType);
            if(appTypeEnum == null || acid < 1){
                return new BaseResponse("参数异常");
            }
            BaseResponse response = new BaseResponse();
            response.setData(refreshTokenBll.getAuthorizerAccessToken(acid, appTypeEnum));
            return response;
        } catch (Exception ex) {
            return new BaseResponse(ex.getMessage());
        }
    }
    @RequestMapping(value = "/setWxAppTabBar")
    public BaseResponse setWxAppTabBar(@RequestParam("uniacid") Integer uniacid) {
        if (uniacid < 1 && uniacid != -100) {
            return new BaseResponse("参数异常");
        }
        try {
            return accountWxappBll.setWxAppTabBar(uniacid);
        } catch (Exception ex) {
            return new BaseResponse(ex.getMessage());
        }
    }

    @RequestMapping(value = "/bindOpenToWeChat")
    public BaseResponse bindOpenToWeChat(BindOpenToWeChatRequest request) {
        try {
            return accountWechatBll.bindOpenToWeChat(request);
        } catch (Exception ex) {
            return new BaseResponse(ex.getMessage());
        }
    }

    @RequestMapping(value = "/setWeChatTemplateMsg")
    public BaseResponse setWeChatTemplateMsg(BaseRequest request) {
        try {
            return accountWechatBll.setWeChatTemplateMsg(request.getWxAppAcid());
        } catch (Exception ex) {
            return new BaseResponse(ex.getMessage());
        }
    }
}
