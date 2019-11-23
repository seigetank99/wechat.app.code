package cn.loftown.wechat.app.code.controller;

import cn.loftown.wechat.app.code.biz.AccountWxappBll;
import cn.loftown.wechat.app.code.util.TransforUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

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
        String phpData = "a:2:{s:3:\"app\";a:18:{s:7:\"isclose\";i:0;s:9:\"closetext\";s:0:\"\";s:8:\"openbind\";i:0;s:8:\"sms_bind\";i:0;s:8:\"bindtext\";s:0:\"\";s:7:\"hidecom\";i:0;s:6:\"navbar\";i:0;s:10:\"sendappurl\";s:1:\"0\";s:8:\"customer\";i:0;s:13:\"customercolor\";s:7:\"#ff5555\";s:12:\"tmessage_pay\";i:0;s:13:\"tmessage_send\";i:0;s:20:\"tmessage_virtualsend\";i:0;s:15:\"tmessage_finish\";i:0;s:5:\"phone\";i:0;s:11:\"phonenumber\";s:0:\"\";s:10:\"phonecolor\";s:7:\"#ff5555\";s:6:\"tabbar\";s:1193:\"a:5:{s:5:\"color\";s:7:\"#999999\";s:13:\"selectedColor\";s:7:\"#ff5555\";s:11:\"borderStyle\";s:0:\"\";s:15:\"backgroundColor\";s:7:\"#f7f7fa\";s:4:\"list\";a:5:{i:0;a:4:{s:8:\"pagePath\";s:17:\"pages/index/index\";s:8:\"iconPath\";s:31:\"static/images/tabbar/icon-1.png\";s:16:\"selectedIconPath\";s:38:\"static/images/tabbar/icon-1-active.png\";s:4:\"text\";s:6:\"首页\";}i:1;a:4:{s:8:\"pagePath\";s:25:\"pages/shop/caregory/index\";s:8:\"iconPath\";s:31:\"static/images/tabbar/icon-2.png\";s:16:\"selectedIconPath\";s:38:\"static/images/tabbar/icon-2-active.png\";s:4:\"text\";s:12:\"全部分类\";}i:2;a:4:{s:8:\"pagePath\";s:33:\"/pages/sale/coupon/my/index/index\";s:8:\"iconPath\";s:32:\"static/images/tabbar/icon-18.png\";s:16:\"selectedIconPath\";s:39:\"static/images/tabbar/icon-18-active.png\";s:4:\"text\";s:6:\"拼团\";}i:3;a:4:{s:8:\"pagePath\";s:23:\"pages/member/cart/index\";s:8:\"iconPath\";s:31:\"static/images/tabbar/icon-4.png\";s:16:\"selectedIconPath\";s:38:\"static/images/tabbar/icon-4-active.png\";s:4:\"text\";s:9:\"购物车\";}i:4;a:4:{s:8:\"pagePath\";s:24:\"pages/member/index/index\";s:8:\"iconPath\";s:31:\"static/images/tabbar/icon-5.png\";s:16:\"selectedIconPath\";s:38:\"static/images/tabbar/icon-5-active.png\";s:4:\"text\";s:12:\"会员中心\";}}}\";}s:3:\"pay\";a:1:{s:5:\"wxapp\";i:0;}}";
        try {
            //List<String> hashMap = TransforUtil.unserializePHParray(phpData);

        } catch (Exception ex){

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
        model.addAttribute("commitDomainModel", accountWxappBll.getWxAppDomain(acid));
        return "wxapp/webDomain";
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
