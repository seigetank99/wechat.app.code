package cn.loftown.wechat.app.code.controller;

import cn.loftown.wechat.app.code.base.BaseResponse;
import cn.loftown.wechat.app.code.base.TableResponse;
import cn.loftown.wechat.app.code.biz.AccountWxappBll;
import cn.loftown.wechat.app.code.entity.AccountWxappModel;
import cn.loftown.wechat.app.code.entity.WxappCodeSubmitModel;
import cn.loftown.wechat.app.code.model.CommitCodeRequest;
import cn.loftown.wechat.app.code.model.SubmitCodeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wxapp")
public class WxAppController {
    @Autowired
    AccountWxappBll accountWxappBll;

    @RequestMapping(value = "/getWxapps", method = RequestMethod.POST)
    public TableResponse<AccountWxappModel> getWxapps() {
        List<AccountWxappModel> wxappModelList = accountWxappBll.getAccountWxAppByPage(1);
        TableResponse<AccountWxappModel> response = new TableResponse();
        response.setData(wxappModelList);
        response.setCode(0);
        response.setMessage("");
        response.setTotal(2);
        return response;
    }

    @RequestMapping(value = "/getWxappCommits", method = RequestMethod.POST)
    public TableResponse<WxappCodeSubmitModel> getWxappCommits() {
        List<WxappCodeSubmitModel> wxappModelList = accountWxappBll.getWxAppCode();
        TableResponse<WxappCodeSubmitModel> response = new TableResponse();
        response.setData(wxappModelList);
        response.setCode(0);
        response.setMessage("");
        response.setTotal(wxappModelList.size());
        return response;
    }

    @RequestMapping(value = "/commitCode", method = RequestMethod.POST)
    public BaseResponse commitCode(CommitCodeRequest request) {
        try {
            return accountWxappBll.commitCode(request);
        } catch (Exception ex){
            return new BaseResponse(ex.getMessage());
        }
    }
    @RequestMapping(value = "/submitCode", method = RequestMethod.POST)
    public BaseResponse submitCode(@RequestBody SubmitCodeRequest request) {
        try {
            return accountWxappBll.submitCode(request);
        } catch (Exception ex){
            return new BaseResponse(ex.getMessage());
        }
    }

    @RequestMapping(value = "/refreshCode", method = RequestMethod.POST)
    public BaseResponse refreshCode(@RequestParam("acid") Integer acid) {
        try {
            return accountWxappBll.refresAuditStatus(acid);
        } catch (Exception ex){
            return new BaseResponse(ex.getMessage());
        }
    }

    @RequestMapping(value = "/releaseCode", method = RequestMethod.POST)
    public BaseResponse releaseCode(@RequestParam("acid") Integer acid) {
        try {
            return accountWxappBll.releaseCode(acid);
        } catch (Exception ex){
            return new BaseResponse(ex.getMessage());
        }
    }

    @RequestMapping(value = "/changeVisitStatus", method = RequestMethod.POST)
    public BaseResponse changeVisitStatus(@RequestParam("acid") Integer acid) {
        try {
            return accountWxappBll.changeVisitStatus(acid);
        } catch (Exception ex){
            return new BaseResponse(ex.getMessage());
        }
    }

    @RequestMapping(value = "/revertCode", method = RequestMethod.POST)
    public BaseResponse revertCode(@RequestParam("acid") Integer acid) {
        try {
            return accountWxappBll.revertCode(acid);
        } catch (Exception ex){
            return new BaseResponse(ex.getMessage());
        }
    }
}
