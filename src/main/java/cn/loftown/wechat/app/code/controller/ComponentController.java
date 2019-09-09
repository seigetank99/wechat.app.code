package cn.loftown.wechat.app.code.controller;

import cn.loftown.wechat.app.code.base.TableResponse;
import cn.loftown.wechat.app.code.biz.CodeBaseBll;
import cn.loftown.wechat.app.code.entity.CodeBaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/component")
public class ComponentController {

    @Autowired
    CodeBaseBll codeBaseBll;

    @RequestMapping(value = "/getDrafts", method = RequestMethod.POST)
    public TableResponse<CodeBaseModel> getDrafts() {
        List<CodeBaseModel> draftsModels = codeBaseBll.getDraftsList();
        TableResponse<CodeBaseModel> response = new TableResponse();
        response.setData(draftsModels);
        response.setCode(0);
        response.setMessage("");
        response.setTotal(2);
        return response;
    }

    @RequestMapping(value = "/getTemplates", method = RequestMethod.POST)
    public TableResponse<CodeBaseModel> getTemplates() {
        List<CodeBaseModel> codeBaseModels = codeBaseBll.getTemplateList();
        TableResponse<CodeBaseModel> response = new TableResponse();
        response.setData(codeBaseModels);
        response.setCode(0);
        response.setMessage("");
        response.setTotal(2);
        return response;
    }
}
