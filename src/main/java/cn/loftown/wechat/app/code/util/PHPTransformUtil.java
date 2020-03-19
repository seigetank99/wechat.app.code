package cn.loftown.wechat.app.code.util;

import cn.loftown.wechat.app.code.entity.AccessTokenModel;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.phprpc.util.AssocArray;
import org.phprpc.util.PHPSerializer;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PHPTransformUtil {
    /**
     * PHP项目里存的不知道什么格式的，需要特殊的解析手段
     *
     * @param assesstoken
     * @return
     */
    public static AccessTokenModel getComponentAccessToken(String assesstoken) throws Exception {
        if (!StringUtils.isEmpty(assesstoken)) {
            AccessTokenModel accessTokenModel = new AccessTokenModel();
            HashMap<String, String> hashMap = formatPhpDataToMap(assesstoken);
            accessTokenModel.setAccessToken(hashMap.get("value"));
            accessTokenModel.setTimeOutDate(new Date(Long.parseLong(hashMap.get("expire")) * 1000));
            return accessTokenModel;
        }
        return null;
    }

    /**
     * 把PHP的垃圾数据格式转化成HashMap
     *
     * @param phpData
     * @return
     * @throws Exception
     */
    public static HashMap<String, String> formatPhpDataToMap(String phpData) throws Exception {
        if (phpData.indexOf("{") > -1) {
            phpData = phpData.substring(phpData.indexOf("{") + 1, phpData.indexOf("}"));
        }
        if (phpData.indexOf("{") > -1) {
            throw new Exception("no can format to phpData");
        }
        HashMap<String, String> hashMap = new HashMap();
        String[] arrStr = phpData.split(";");
        if (arrStr.length == 1) {
            String[] arror = arrStr[0].split(":");
            hashMap.put("value", getValue(arror));
            return hashMap;
        }
        for (int i = 0; i < arrStr.length - 1; i += 2) {
            String or1 = arrStr[i];
            String[] arror1 = or1.split(":");
            String v1 = getValue(arror1);

            String or2 = arrStr[i + 1];
            String[] arror2 = or2.split(":");
            String v2 = getValue(arror2);
            if (StringUtils.isEmpty(v1) || StringUtils.isEmpty(v2)) {
                continue;
            }
            hashMap.put(v1, v2);
        }
        return hashMap;
    }

    /**
     * 将PHP的格式化字符串转换为HashMap
     * @param content
     * @return
     */
    public static AssocArray getAssocArray(String content){
        PHPSerializer p = new PHPSerializer();
        if (StringUtils.isEmpty(content))
            return null;
        try {
            AssocArray arrayContent = (AssocArray) p.unserialize(content.getBytes());
            return arrayContent;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 将HashMap转换为PHP字符串
     * @param assocArray
     * @return
     */
    public static String getPHPContent(AssocArray assocArray){
        PHPSerializer p = new PHPSerializer();
        if (assocArray == null)
            return null;
        try {
            byte[] data = p.serialize(assocArray);
            String result = new String(data);
            return result;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 对php序列化的字符串，进行反序列化
     */
    public static AssocArray getAppConfig(String content, String key) {
        PHPSerializer p = new PHPSerializer();
        if (StringUtils.isEmpty(content))
            return null;
        try {
            AssocArray arrayContent = (AssocArray) p.unserialize(content.getBytes());
            AssocArray assocKey = (AssocArray) arrayContent.get(key);
            return assocKey;
        } catch (Exception e) {
            System.out.println("反序列化PHParray: " + content + " 失败！！！");
        }
        return null;
    }


    /**
     * 对php序列化的字符串，进行反序列化
     */
    public static JSONObject getAppTabbarConfig(String content) {
        JSONObject tabbar = new JSONObject();
        PHPSerializer p = new PHPSerializer();
        if (StringUtils.isEmpty(content))
            return null;
        try {
            AssocArray arrayContent = (AssocArray) p.unserialize(content.getBytes());
            AssocArray assocApp = (AssocArray) arrayContent.get("app");
            AssocArray assocKey = (AssocArray) p.unserialize((byte[]) assocApp.get("tabbar"));

            if (assocKey.get("color") != null) {
                String color = new String(((byte[]) assocKey.get("color")));
                if(!StringUtils.isEmpty(color)) {
                    tabbar.put("color", color);
                }
            }
            if (assocKey.get("selectedColor") != null) {
                String selectedColor = new String(((byte[]) assocKey.get("selectedColor")));
                if(!StringUtils.isEmpty(selectedColor)) {
                    tabbar.put("selectedColor", selectedColor);
                }
            }
            if (assocKey.get("borderStyle") != null) {
                String borderStyle = new String(((byte[]) assocKey.get("borderStyle")));
                if(!StringUtils.isEmpty(borderStyle)) {
                    tabbar.put("borderStyle", borderStyle);
                }
            }
            if (assocKey.get("backgroundColor") != null) {
                String backgroundColor = new String(((byte[]) assocKey.get("backgroundColor")));
                if(!StringUtils.isEmpty(backgroundColor)) {
                    tabbar.put("backgroundColor", backgroundColor);
                }
            }
            AssocArray assocList = (AssocArray) assocKey.get("list");
            if(assocList != null){
                JSONArray list = new JSONArray();
//                HashMap<String, String> mapList = assocList.toHashMap();
//                for (Map.Entry<String, String> entry : mapList.entrySet()) {
//                    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//                }
                for(int i = 0; i < assocList.size(); i ++){
                    JSONObject item = new JSONObject();
                    AssocArray assocItem = (AssocArray) assocList.get(i);
                    item.put("pagePath", new String(((byte[]) assocItem.get("pagePath"))));
                    item.put("text", new String(((byte[]) assocItem.get("text"))));
                    item.put("iconPath", new String(((byte[]) assocItem.get("iconPath"))));
                    item.put("selectedIconPath", new String(((byte[]) assocItem.get("selectedIconPath"))));
                    list.add(item);
                }
                tabbar.put("list", list);
            }
            return tabbar;
        } catch (Exception e) {
            System.out.println("反序列化PHParray: " + content + " 失败！！！");
        }
        return null;
    }


    /**
     * 为了兼容PHP项目，再转成那种垃圾恶心的数据格式
     *
     * @param value
     * @param expire
     * @return
     */
    public static String formatMapToPhpData(String value, Long expire) {
        return String.format("a:2:{s:5:\"value\";s:157:\"%s\";s:6:\"expire\";i:%d;}", value, expire / 1000);
    }

    private static String getValue(String[] arror2) {
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
