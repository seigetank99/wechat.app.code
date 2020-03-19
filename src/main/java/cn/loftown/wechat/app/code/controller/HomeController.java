package cn.loftown.wechat.app.code.controller;

import cn.loftown.wechat.app.code.biz.AccountWechatBll;
import cn.loftown.wechat.app.code.biz.AccountWxappBll;
import cn.loftown.wechat.app.code.entity.CommitDomainModel;
import cn.loftown.wechat.app.code.util.PHPTransformUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
public class HomeController {
    @Autowired
    AccountWxappBll accountWxappBll;
    @Autowired
    AccountWechatBll accountWechatBll;

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/index")
    public String index(){
        try {
            //accountWechatBll.setWeChatTemplateMessage(9);
            accountWechatBll.setWeChatTemplateMessageConfig(9);
        } catch (Exception ex){
            System.out.println(ex);
        }
        return "index";
    }

    /**
     * 公众号列表页面
     * @return
     */
    @RequestMapping("/wechat/wechats")
    public String getWechats(){
        return "wechat/wechats";
    }
    /**
     * 公众号详情页面
     * @return
     */
    @RequestMapping("/wechat/modify")
    public String wechatModify(){
        return "wechat/modify";
    }

    /**
     * 小程序审核页面
     * @return
     */
    @RequestMapping("/wxapp/submitAudit")
    public String submitAudit(Model model, @RequestParam("acid") Integer acid, @RequestParam("wxAppAcid") Integer wxAppAcid) {
        model.addAttribute("wxApp", accountWxappBll.getAccountWxAppDetail(wxAppAcid));
        model.addAttribute("acid", acid);
        return "wxapp/submitAudit";
    }
    /**
     * 小程序审核页面
     * @return
     */
    @RequestMapping("/wxapp/submitAgain")
    public String submitAgain(Model model, @RequestParam("acid") Integer acid, @RequestParam("wxAppAcid") Integer wxAppAcid) {
        model.addAttribute("wxApp", accountWxappBll.getAccountWxAppDetail(wxAppAcid));
        model.addAttribute("acid", acid);
        return "wxapp/submitAgain";
    }
    /**
     * 小程序提交页面
     * @return
     */
    @RequestMapping("/wxapp/commit")
    public String commit(Model model, @RequestParam("acid") Integer acid) {
        model.addAttribute("wxApp", accountWxappBll.getAccountWxAppDetail(acid));
        return "wxapp/commit";
    }
    /**
     * 小程序域名设置页面
     * @return
     */
    @RequestMapping("/wxapp/domain")
    public String domain(Model model, @RequestParam("acid") Integer acid) throws Exception {
        CommitDomainModel commitDomainModel = accountWxappBll.getWxAppDomain(acid);
        if(StringUtils.isEmpty(commitDomainModel.getWebviewdomain())){
            commitDomainModel.setWebviewdomain("https://");
        }
        if(StringUtils.isEmpty(commitDomainModel.getDownloaddomain())){
            commitDomainModel.setDownloaddomain("https://");
        }
        if(StringUtils.isEmpty(commitDomainModel.getRequestdomain())){
            commitDomainModel.setRequestdomain("https://");
        }
        if(StringUtils.isEmpty(commitDomainModel.getUploaddomain())){
            commitDomainModel.setUploaddomain("https://");
        }
        if(StringUtils.isEmpty(commitDomainModel.getWsrequestdomain())){
            commitDomainModel.setWsrequestdomain("wss://");
        }
        model.addAttribute("commitDomainModel", commitDomainModel);
        return "wxapp/webDomain";
    }
    /**
     * 小程序体验者列表页面
     * @return
     */
    @RequestMapping("/wxapp/tester")
    public String tester(Model model, @RequestParam("acid") Integer acid, @RequestParam("name") String name) {
        model.addAttribute("wxAppId", acid);
        model.addAttribute("wxAppName", name);
        return "wxapp/bindTestUser";
    }

    /**
     * 小程序列表页面
     * @return
     */
    @RequestMapping("/wxapp/wxapps")
    public String wxapps() {
        return "wxapp/wxapps";
    }

    /**
     * 小程序代码提交列表页面
     * @return
     */
    @RequestMapping("/wxapp/wxappCommits")
    public String wxappCommits() {
        return "wxapp/wxappCommits";
    }

    /**
     * 代码草稿箱
     * @return
     */
    @RequestMapping("/component/drafts")
    public String drafts() {
        return "component/drafts";
    }
    /**
     * 代码模版库
     * @return
     */
    @RequestMapping("/component/templates")
    public String templates() {
        return "component/templates";
    }

    /**
     * 预约配置
     * @return
     */
    @RequestMapping("/lf/appointment/config")
    public String appointmentConfig() {
        return "appointment/config";
    }
    /**
     * 预约订单
     * @return
     */
    @RequestMapping("/lf/appointment/order")
    public String appointmentOrder() {
        return "appointment/order";
    }
}
