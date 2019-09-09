package cn.loftown.wechat.app.code.util;

import cn.loftown.wechat.app.code.entity.AccessTokenModel;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;

public class TransforUtil {
    /**
     * PHP项目里存的不知道什么格式的，需要特殊的解析手段
     * @param assesstoken
     * @return
     */
    public static AccessTokenModel getComponentAccessToken(String assesstoken) throws Exception{
        if(!StringUtils.isEmpty(assesstoken)){
            AccessTokenModel accessTokenModel = new AccessTokenModel();
            HashMap<String,String> hashMap = formatPhpDataToMap(assesstoken);
            accessTokenModel.setAccessToken(hashMap.get("value"));
            accessTokenModel.setTimeOutDate(new Date(Long.parseLong(hashMap.get("expire")) * 1000));
            return accessTokenModel;
        }
        return null;
    }

    /**
     * 把PHP的垃圾数据格式转化成HashMap
     * @param phpData
     * @return
     * @throws Exception
     */
    public static HashMap<String,String> formatPhpDataToMap(String phpData) throws Exception{
        if(phpData.indexOf("{")> -1) {
            phpData = phpData.substring(phpData.indexOf("{") + 1, phpData.indexOf("}"));
        }
        if(phpData.indexOf("{")> -1){
            throw new Exception("no can format to phpData");
        }
        HashMap<String,String> hashMap = new HashMap();
        String[] arrStr = phpData.split(";");
        if(arrStr.length == 1){
            String[] arror = arrStr[0].split(":");
            hashMap.put("value", getValue(arror));
            return hashMap;
        }
        for (int i = 0; i< arrStr.length -1; i += 2){
            String or1 = arrStr[i];
            String[] arror1 = or1.split(":");
            String v1 = getValue(arror1);

            String or2 = arrStr[i + 1];
            String[] arror2 = or2.split(":");
            String v2 = getValue(arror2);
            if(StringUtils.isEmpty(v1) || StringUtils.isEmpty(v2)){
                continue;
            }
            hashMap.put(v1, v2);
        }
        return hashMap;
    }

    /**
     * 为了兼容PHP项目，再转成那种垃圾恶心的数据格式
     * @param value
     * @param expire
     * @return
     */
    public static String formatMapToPhpData(String value, Long expire){
        return String.format("a:2:{s:5:\"value\";s:157:\"%s\";s:6:\"expire\";i:%d;}", value, expire / 1000);
    }

    private static String getValue(String[] arror2){
        switch (arror2[0]) {
            case "s":
                return arror2[2].replace("\"", "");
            case "i":
                return arror2[1];
            default:
                return "";
        }
    }
}
