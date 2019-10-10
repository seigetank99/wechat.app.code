package cn.loftown.wechat.app.code.entity;

import lombok.Data;

@Data
public class BaseModel {
    /**
     * 自增主键acid
     */
    protected Integer acid;
    /**
     * 商家唯一acid
     */
    protected Integer uniacid;
}
