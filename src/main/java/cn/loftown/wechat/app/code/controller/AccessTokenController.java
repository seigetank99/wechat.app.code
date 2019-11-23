package cn.loftown.wechat.app.code.controller;

import cn.loftown.wechat.app.code.base.BaseResponse;
import cn.loftown.wechat.app.code.biz.RefreshTokenBll;
import cn.loftown.wechat.app.code.enums.AppTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class AccessTokenController {
    @Autowired
    RefreshTokenBll refreshTokenBll;

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
}
