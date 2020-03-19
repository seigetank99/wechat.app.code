package cn.loftown.wechat.app.code.controller;

import cn.loftown.wechat.app.code.base.TableResponse;
import cn.loftown.wechat.app.code.biz.AccountWechatBll;
import cn.loftown.wechat.app.code.entity.AccountWechatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wechat")
public class WeChatController {
    @Autowired
    AccountWechatBll accountWechatBll;

    @RequestMapping(value = "/getWechats", method = RequestMethod.POST)
    public TableResponse<AccountWechatModel> getWechats() {
        List<AccountWechatModel> wechatModels = accountWechatBll.getAccountWechatsByPage(1);
        TableResponse<AccountWechatModel> response = new TableResponse();
        response.setData(wechatModels);
        response.setCode(0);
        response.setMessage("");
        response.setTotal(2L);
        try {
            accountWechatBll.setWeChatTemplateMessage(1);
        } catch (Exception ex){

        }
        return response;
    }
}
