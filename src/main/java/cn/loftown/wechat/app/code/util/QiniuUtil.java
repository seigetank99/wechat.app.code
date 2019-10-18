package cn.loftown.wechat.app.code.util;

import cn.loftown.wechat.app.code.base.BaseResponse;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;

public class QiniuUtil {
    //设置好账号的ACCESS_KEY和SECRET_KEY
    static String ACCESS_KEY = "5-9PJzrLSLzChzHwj0pBuI1P88xqO-5B6aB2MYtl"; //这两个登录七牛 账号里面可以找到
    static String SECRET_KEY = "xasV1W4NudX0HF5YS5ntiHf_9Hv5dBHZIkTz5nBh";

    //要上传的空间
    static String BUCKET_NAME = "loftown"; //对应要上传到七牛上 你的那个路径（自己建文件夹 注意设置公开）

    //普通上传
    public static BaseResponse<String> upload(MultipartFile file) {
        BaseResponse<String> baseResponse = new BaseResponse();
        //构造一个带指定Region对象的配置类
        Configuration cfg = new Configuration(Region.region0());
        UploadManager uploadManager = new UploadManager(cfg);
        //外链域名
        String domian = "http://shop-img.loftown.cn/";

        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        String upToken = auth.uploadToken(BUCKET_NAME);
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/yyyy/MM");
        String fileName = "images/" + sdfDateTime.format(new Date()) + "/" + setFileName(file);
        if (!file.isEmpty()) {
            try {
                Response response = uploadManager.put(file.getBytes(), fileName, upToken);
                if (response.statusCode != 200) {
                    baseResponse.setCode(BaseResponse.business_error());
                    baseResponse.setMessage("上传失败！");
                } else {
                    baseResponse.setData(domian + fileName);
                }
            } catch (Exception e) {
                baseResponse.setCode(BaseResponse.business_error());
                baseResponse.setMessage("上传失败！");
            }
        } else {
            baseResponse.setCode(BaseResponse.business_error());
            baseResponse.setMessage("没有需要上传的文件！");
        }

        return baseResponse;
    }

    private static String setFileName(MultipartFile file){
        String extend = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        return BaseUtil.getUuid() + extend;
    }
}
