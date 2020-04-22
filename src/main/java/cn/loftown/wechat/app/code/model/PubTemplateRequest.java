package cn.loftown.wechat.app.code.model;

import lombok.Data;

import java.util.List;

@Data
public class PubTemplateRequest {
    private Integer uniacid;
    private Integer start;
    private Integer limit;

    /**
     * 类目 id，多个用逗号隔开 类目查询参数
     */
    private String ids;

    /**
     * getPubTemplateTitles 查询类目下的模版
     * getPubTemplateKeyWords 查询模版下的关键字
     * getTemplate 查询小程序的订阅消息
     * getCategory 查询小程序的服务类目
     * addTemplate 添加小程序订阅消息
     * delTemplate 删除小程序订阅消息
     */
    private String operateType;
    /**
     * 要删除的模版ID
     */
    private String priTmplId;

    /**
     * 添加参数
     * @param tid 模板标题 id，可通过接口获取，也可登录小程序后台查看获取
     * @param kidList 开发者自行组合好的模板关键词列表，关键词顺序可以自由搭配（例如 [3,5,4] 或 [4,5,3]），最多支持5个，最少2个关键词组合
     * @param sceneDesc 服务场景描述，15个字以内
     */
    private String tid;
    private List<Integer> kidList;
    private String sceneDesc;
}
