package cn.loftown.wechat.app.code.model;

import lombok.Data;

@Data
public class BaseRequest {
    private Integer pageIndex;
    private Integer pageSize;
    private Integer uniacid;
}
