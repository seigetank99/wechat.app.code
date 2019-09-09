package cn.loftown.wechat.app.code.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import java.io.IOException;

public class QiniuUtil {
    //设置好账号的ACCESS_KEY和SECRET_KEY
    String ACCESS_KEY = "5-9PJzrLSLzChzHwj0pBuI1P88xqO-5B6aB2MYtl"; //这两个登录七牛 账号里面可以找到
    String SECRET_KEY = "xasV1W4NudX0HF5YS5ntiHf_9Hv5dBHZIkTz5nBh";

    //要上传的空间
    String BUCKET_NAME = "loftown"; //对应要上传到七牛上 你的那个路径（自己建文件夹 注意设置公开）
    //普通上传
    public void upload() throws IOException {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = "/home/qiniu/test.png";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        String upToken = auth.uploadToken(BUCKET_NAME);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }
}
