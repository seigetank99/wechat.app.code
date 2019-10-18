package cn.loftown.wechat.app.code.controller;

import cn.loftown.wechat.app.code.base.BaseResponse;
import cn.loftown.wechat.app.code.util.QiniuUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse uptRequestInfoAction(MultipartHttpServletRequest request) {
        MultipartFile file = request.getFiles("imgFile").get(0);
        return QiniuUtil.upload(file);
    }
}
