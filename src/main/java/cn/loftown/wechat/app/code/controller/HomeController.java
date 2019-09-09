package cn.loftown.wechat.app.code.controller;

import cn.loftown.wechat.app.code.biz.AccountWxappBll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    @Autowired
    AccountWxappBll accountWxappBll;

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/index")
    public String index(){
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
}
