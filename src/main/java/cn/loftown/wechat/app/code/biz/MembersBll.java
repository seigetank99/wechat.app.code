package cn.loftown.wechat.app.code.biz;

import cn.loftown.wechat.app.code.entity.AccessTokenModel;
import cn.loftown.wechat.app.code.enums.AppTypeEnum;
import cn.loftown.wechat.app.code.util.HttpUtil;
import cn.loftown.wechat.app.code.util.TransforUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MembersBll {
    @Autowired
    private RefreshTokenBll refreshTokenBll;

    public List<String> getMemberList(int acid) {
        try {
            String assesstoken = refreshTokenBll.getAuthorizerAccessToken(acid, AppTypeEnum.WECHATMINIAPP);
            AccessTokenModel accessTokenModel = TransforUtil.getComponentAccessToken(assesstoken);
            JSONObject request = new JSONObject();
            request.put("action","get_experiencer");
            String response = HttpUtil.doPost("https://api.weixin.qq.com/wxa/memberauth?access_token=" + accessTokenModel.getAccessToken(), request.toJSONString());
            JSONObject jsonObject = JSONObject.parseObject(response);
            List<String> result = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("members");
            for(int i = 0; i < jsonArray.size(); i++){
                JSONObject json =  jsonArray.getJSONObject(i);
               result.add(json.getString("userstr"));
            }
            return result;
        } catch (Exception ex) {

        }
        return null;

    }
}
