package cn.loftown.wechat.app.code.util;

import java.util.UUID;

public class BaseUtil {
    public static String getUuid(){
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }
}
