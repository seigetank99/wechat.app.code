package cn.loftown.wechat.app.code.model;

import lombok.Data;

@Data
public class BindOpenToWeChatRequest {
    /**
     * 公众号或小程序的主键
     */
    public Integer acid;
    /**
     * 应用类型，对应AppTypeEnum枚举
     */
    public Integer appType;
    /**
     * 绑定类型，bind单绑定，create创建并绑定，unbind解除绑定，get查询绑定
     */
    public String bindType;
}
